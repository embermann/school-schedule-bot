package org.example;

import org.example.enums.AcceptedCommands;
import org.example.enums.MessageEntityTypes;
import org.example.enums.MessageTextEnum;
import org.example.enums.RtuDialogStateEnum;
import org.example.obj.ChatExtended;
import org.example.obj.RtuFacultyObj;
import org.example.obj.json.DateTimeObj;
import org.example.obj.json.RtuGroupObj;
import org.example.obj.RtuPageDataObj;
import org.example.obj.json.RtuScheduleObj;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

public class Bot extends TelegramLongPollingBot {

    Logger LOG =  LoggerFactory.getLogger(Bot.class);

    private final Properties properties;
    private final String[] HEADERS = new String[]{"User-Agent", "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:107.0) Gecko/20100101 Firefox/107.0", "Accept", "*/*", "Accept-Encoding", "gzip, deflate, br", "Content-Type", "application/x-www-form-urlencoded; charset=UTF-8"};
    private RtuPageDataObj rtuPageDataObj;
    private HashMap<Long, ChatExtended> chats;
    public Bot(Properties properties) {

        this.properties = properties;
        this.chats = new HashMap<>();
        this.rtuPageDataObj = new RtuPageDataObj();
        this.scrapRtuPage();
    }

    @Override
    public String getBotUsername() {
        return properties.getProperty("username");
    }

    @Override
    public String getBotToken() {
        return properties.getProperty("token");
    }

    @Override
    public void onUpdateReceived(Update update) {
        LOG.info("Update received: {}", update.getUpdateId());

        processUpdate(update);
    }

    private void processUpdate(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            Long chatId = message.getChatId();
            LOG.info("Message received: {} From chat: {}", message.getMessageId(), chatId);

            Chat chat = message.getChat();
            ChatExtended currentChat = chats.get(chat.getId());
            if (currentChat == null) {
                currentChat = new ChatExtended(chat);
                chats.put(chatId, currentChat);
            }

            if (message.hasEntities()) {
                LOG.info("Message has entities in chat: {}", chatId);
                checkEntities(message.getEntities(), chatId, currentChat);
            }

        } else if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            Message message = callbackQuery.getMessage();
            Long chatId = message.getChatId();
            ChatExtended currentChat = chats.get(chatId);

            parseCallbacks(currentChat, callbackQuery);

        }
    }

    private void parseCallbacks(ChatExtended currentChat, CallbackQuery callbackQuery) {
        switch (currentChat.getState()) {
            case SEMESTER:
                currentChat.setInlineDialogInitiated(true);
                String selectedSemesterId = callbackQuery.getData();
                currentChat.setState(RtuDialogStateEnum.FACULTY);
                currentChat.setSelectedSemesterId(selectedSemesterId);
                try {
                    execute(sendAnswerCallbackQuery(callbackQuery.getId()));
                    execute(sendEditMessageReplyMarkup(currentChat.getMessageId(), currentChat.getChatData().getId(), getKeyboardMarkupForRtuFaculties()));
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
                break;
            case FACULTY:
                String selectedFacultyId = callbackQuery.getData();
                currentChat.setState(RtuDialogStateEnum.PROGRAM);
                try {
                    execute(sendAnswerCallbackQuery(callbackQuery.getId()));
                    execute(sendEditMessageReplyMarkup(currentChat.getMessageId(), currentChat.getChatData().getId(), getKeyboardMarkupForRtuPrograms(selectedFacultyId)));
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
                break;
            case PROGRAM:
                String selectedProgramId = callbackQuery.getData();
                currentChat.setState(RtuDialogStateEnum.COURSE);
                currentChat.setSelectedProgramId(selectedProgramId);
                try {
                    execute(sendAnswerCallbackQuery(callbackQuery.getId()));
                    execute(sendEditMessageReplyMarkup(currentChat.getMessageId(), currentChat.getChatData().getId(), getKeyboardMarkupForRtuCourses(currentChat)));
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
                break;
            case COURSE:
                String selectedCourseId = callbackQuery.getData();
                currentChat.setState(RtuDialogStateEnum.GROUP);
                currentChat.setSelectedCourseId(selectedCourseId);
                try {
                    execute(sendAnswerCallbackQuery(callbackQuery.getId()));
                    execute(sendEditMessageReplyMarkup(currentChat.getMessageId(), currentChat.getChatData().getId(), getKeyboardMarkupForRtuGroups(currentChat)));
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
                break;
            case GROUP:
                String selectedGroupId = callbackQuery.getData();
                currentChat.setSelectedGroupId(selectedGroupId);
                for (RtuGroupObj rtuGroupObj : rtuPageDataObj.getGroupObjSet()) {
                    if (rtuGroupObj.equalsToChatExtended(currentChat)) {
                        currentChat.setSelectedSemesterProgramId(String.valueOf(rtuGroupObj.getSemesterProgramId()));
                        break;
                    }
                }


                try {
                    execute(sendAnswerCallbackQuery(callbackQuery.getId()));
                    execute(sendEditMessageReplyMarkup(currentChat.getMessageId(), currentChat.getChatData().getId(), null));
                    execute(sendRtuScheduleMessage(getRtuScheduleInTextFormat(currentChat), currentChat.getChatData().getId()));
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
                break;

        }
    }

    private void checkEntities(List<MessageEntity> entities, Long chatId, ChatExtended currentChat) {
        for (MessageEntity entity : entities) {
            switch (MessageEntityTypes.getByName(entity.getType())) {
                case BOT_COMMAND:
                    LOG.info("Found {} in chat: {}", MessageEntityTypes.BOT_COMMAND.getName(), chatId);
                    try {
                        processCommand(entity, chatId, currentChat);
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                    break;

                case NOT_FOUND: // == default
            }

        }
    }

    private void processCommand(MessageEntity entity, Long chatId, ChatExtended currentChat) throws TelegramApiException {
        switch (AcceptedCommands.getByCommand(entity.getText())) {
            case START:
                LOG.info("Found {} command in chat: {}", AcceptedCommands.START.getName(), chatId);
                execute(sendStartCommandReply(chatId));
                break;

            case RTU:
                LOG.info("Found {} command in chat: {}", AcceptedCommands.RTU.getName(), chatId);
                currentChat.setState(RtuDialogStateEnum.SEMESTER);
                Message sentMessage = execute(sendInlineKeyboard(chatId, "Choose", getKeyboardMarkupForRtuSemesters()));
                chats.get(chatId).setMessageId(sentMessage.getMessageId());
                break;
            case UNKNOWN_COMMAND: // == default
        }

    }

    /**
     * Getting keyboards
     * **/
    private InlineKeyboardMarkup getKeyboardMarkupForRtuSemesters() {
        Map<String, String> semesterData = rtuPageDataObj.getAvailableSemesterList();

        ArrayList<List<InlineKeyboardButton>> buttonRows = new ArrayList<>();
        for (Map.Entry<String,String> semester : semesterData.entrySet()) {
            ArrayList<InlineKeyboardButton> buttonRow = new ArrayList<>(1);
            InlineKeyboardButton button = new InlineKeyboardButton(semester.getValue());
            button.setCallbackData(semester.getKey());
            buttonRow.add(button);
            buttonRows.add(buttonRow);
        }
        return new InlineKeyboardMarkup(buttonRows);
    }

    private InlineKeyboardMarkup getKeyboardMarkupForRtuCourses(ChatExtended currentChat) {
        byte[] courseList = getRtuCourses(currentChat);

        ArrayList<List<InlineKeyboardButton>> buttonRows = new ArrayList<>();
        for (byte course : courseList) {
            String courseStr = String.valueOf(course);
            ArrayList<InlineKeyboardButton> buttonRow = new ArrayList<>(1);
            InlineKeyboardButton button = new InlineKeyboardButton(courseStr);
            button.setCallbackData(courseStr);
            buttonRow.add(button);
            buttonRows.add(buttonRow);
        }
        return new InlineKeyboardMarkup(buttonRows);
    }

    private InlineKeyboardMarkup getKeyboardMarkupForRtuGroups(ChatExtended currentChat) {
        List<RtuGroupObj> list = getRtuGroups(currentChat);

        ArrayList<List<InlineKeyboardButton>> buttonRows = new ArrayList<>();
        for (RtuGroupObj obj : list) {
            ArrayList<InlineKeyboardButton> buttonRow = new ArrayList<>(1);
            InlineKeyboardButton button = new InlineKeyboardButton(obj.getGroup());
            button.setCallbackData(obj.getGroup());
            buttonRow.add(button);
            buttonRows.add(buttonRow);
        }
        return new InlineKeyboardMarkup(buttonRows);
    }

    private InlineKeyboardMarkup getKeyboardMarkupForRtuFaculties() {
        ArrayList<List<InlineKeyboardButton>> buttonRows = new ArrayList<>();
        for (RtuFacultyObj facultyObj : rtuPageDataObj.getFacultyObjList()) {
            ArrayList<InlineKeyboardButton> buttonRow = new ArrayList<>(1);
            InlineKeyboardButton button = new InlineKeyboardButton(facultyObj.getLabel());
            button.setCallbackData(facultyObj.getId());
            buttonRow.add(button);
            buttonRows.add(buttonRow);
        }
        return new InlineKeyboardMarkup(buttonRows);
    }

    private InlineKeyboardMarkup getKeyboardMarkupForRtuPrograms(String programId) {
        ArrayList<List<InlineKeyboardButton>> buttonRows = new ArrayList<>();
        for (RtuFacultyObj facultyObj : rtuPageDataObj.getFacultyObjList()) {
            if (facultyObj.getId().equals(programId)) {
                for (RtuFacultyObj.RtuProgramObj programObj : facultyObj.getProgramObjList()) {
                    ArrayList<InlineKeyboardButton> buttonRow = new ArrayList<>(1);
                    InlineKeyboardButton button = new InlineKeyboardButton(programObj.getLabel());
                    button.setCallbackData(programObj.getValue());
                    buttonRow.add(button);
                    buttonRows.add(buttonRow);
                }
            }
        }
        return new InlineKeyboardMarkup(buttonRows);
    }

    /**
     * Scraping websites/responses/preparing data for keyboards
     * **/
    private byte[] getRtuCourses(ChatExtended currentChat) {
        HttpClient httpClient = HttpClient.newHttpClient();

        try {
            String body = "semesterId=" + currentChat.getSelectedSemesterId() + "&" + "programId=" + currentChat.getSelectedProgramId();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("https://nodarbibas.rtu.lv/findCourseByProgramId"))
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .version(HttpClient.Version.HTTP_2)
                    .headers(HEADERS)
                    .build();

            HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
            GZIPInputStream gzipInputStream = new GZIPInputStream(new ByteArrayInputStream(response.body()));
            InputStreamReader reader = new InputStreamReader(gzipInputStream);
            BufferedReader in = new BufferedReader(reader);

            String read = in.lines().collect(Collectors.joining());

            String[] string = read.replaceAll("\\[", "")
                    .replaceAll("]", "")
                    .split(",");
            byte[] arr = new byte[string.length];
            for (byte i = 0; i < string.length; i++) {
                arr[i] = Byte.parseByte(string[i]);
            }

            return arr;

        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    private List<RtuGroupObj> getRtuGroups(ChatExtended currentChat) {
        HttpClient httpClient = HttpClient.newHttpClient();

        try {
            String body = "courseId=" + currentChat.getSelectedCourseId() +
                    "&semesterId=" + currentChat.getSelectedSemesterId() +
                    "&programId=" + currentChat.getSelectedProgramId();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("https://nodarbibas.rtu.lv/findGroupByCourseId"))
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .version(HttpClient.Version.HTTP_2)
                    .headers(HEADERS)
                    .build();

            HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
            GZIPInputStream gzipInputStream = new GZIPInputStream(new ByteArrayInputStream(response.body()));
            InputStreamReader reader = new InputStreamReader(gzipInputStream);
            BufferedReader in = new BufferedReader(reader);

            String read = in.lines().collect(Collectors.joining());
            JSONArray arr = new JSONArray(read);

            HashSet<RtuGroupObj> rtuGroupObjHashSet = new HashSet<>();
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                RtuGroupObj rtuGroupObj = new RtuGroupObj(
                        obj.getInt("semesterProgramId"),
                        obj.getInt("semesterId"),
                        obj.getInt("programId"),
                        obj.getInt("course"),
                        obj.getString("group")
                );
                rtuPageDataObj.getGroupObjSet().add(rtuGroupObj);
                rtuGroupObjHashSet.add(rtuGroupObj);
            }

            List<RtuGroupObj> list = new ArrayList<>(rtuGroupObjHashSet);
            list.sort(new SortByGroup());

            return list;

        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    private void scrapRtuPage() {
        Document doc;
        try {
            doc = Jsoup.connect("https://nodarbibas.rtu.lv").get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Element semesterSelect = doc.getElementById("semester-id");
        if (semesterSelect == null) throw new RuntimeException("No semester node found in document");

        Elements semesterSelectChildren = semesterSelect.children();
        if (semesterSelectChildren.size() == 0)
            throw new RuntimeException("No semesterSelect children found in document");

        for (Element semesterSelectChild : semesterSelectChildren) {
            String key = semesterSelectChild.attr("value");
            String value = semesterSelectChild.ownText();
            rtuPageDataObj.getAvailableSemesterList().put(key, value);
        }

        List<RtuFacultyObj> rtuFacultyObjList = new ArrayList<>();

        Element programSelect = doc.getElementById("program-id");
        if (programSelect == null) throw new RuntimeException("No program node found in document");

        Elements programSelectChildren = programSelect.children()
                .stream()
                .filter(n -> n.tag().getName().equals("optgroup"))
                .collect(Collectors.toCollection(Elements::new));
        if (programSelectChildren.size() == 0) throw new RuntimeException("No programSelectChildren children found in document");

        int id = 0;
        for (Element programSelectChild : programSelectChildren) {
            id++;
            List<RtuFacultyObj.RtuProgramObj> rtuProgramObjList = new ArrayList<>();

            Elements programSelectChildChildren = programSelectChild.children();
            if (programSelectChildChildren.size() == 0) throw new RuntimeException("No programSelectChildChildren children found in document");

            for (Element programSelectChildChild : programSelectChildChildren) {
                rtuProgramObjList.add(
                        new RtuFacultyObj.RtuProgramObj(
                                programSelectChildChild.attr("value"),
                                programSelectChildChild.ownText()
                        )
                );
            }
            rtuFacultyObjList.add(
                    new RtuFacultyObj(
                            String.valueOf(id),
                            programSelectChild.attr("label"),
                            rtuProgramObjList
                    )
            );
        }

        rtuPageDataObj.setFacultyObjList(rtuFacultyObjList);
    }

    private HashSet<RtuScheduleObj> getRtuSchedule(ChatExtended currentChat) {
        HttpClient httpClient = HttpClient.newHttpClient();
        Calendar calendar = Calendar.getInstance();

        try {
            String body = "semesterProgramId=" + currentChat.getSelectedSemesterProgramId() +
                    "&year=" + calendar.get(Calendar.YEAR) +
                    "&month=" + calendar.get(Calendar.MONTH);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("https://nodarbibas.rtu.lv/getSemesterProgEventList"))
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .version(HttpClient.Version.HTTP_2)
                    .headers(HEADERS)
                    .build();

            HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
            GZIPInputStream gzipInputStream = new GZIPInputStream(new ByteArrayInputStream(response.body()));
            InputStreamReader reader = new InputStreamReader(gzipInputStream);
            BufferedReader in = new BufferedReader(reader);

            String read = in.lines().collect(Collectors.joining());
            JSONArray arr = new JSONArray(read);

            HashSet<RtuScheduleObj> rtuScheduleObjHashSet = new HashSet<>();
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                JSONObject customStart = (JSONObject) obj.get("customStart");
                JSONObject customEnd = (JSONObject) obj.get("customEnd");
                RtuScheduleObj rtuScheduleObj = new RtuScheduleObj(
                        obj.getInt("eventDateId"),
                        obj.getInt("eventId"),
                        obj.getInt("statusId"),
                        obj.getString("eventTempName"),
                        obj.getString("roomInfoText"),
                        obj.getString("eventTempNameEn"),
                        obj.getString("roomInfoTextEn"),
                        obj.getLong("eventDate"),
                        new DateTimeObj(customStart.getInt("hour"),
                                customStart.getInt("minute"),
                                customStart.getInt("second"),
                                customStart.getInt("nano")),
                        new DateTimeObj(customEnd.getInt("hour"),
                                customEnd.getInt("minute"),
                                customEnd.getInt("second"),
                                customEnd.getInt("nano"))
                );
                rtuScheduleObjHashSet.add(rtuScheduleObj);
            }

            return rtuScheduleObjHashSet;

        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
    /**
     * Preparing text for messages
     * **/

    private String getRtuScheduleInTextFormat(ChatExtended currentChat) {
        HashSet<RtuScheduleObj> rtuScheduleObjHashSet = getRtuSchedule(currentChat);
        StringBuilder result = new StringBuilder();
        for (RtuScheduleObj obj : rtuScheduleObjHashSet) {
            StringBuilder sb = new StringBuilder();
            Calendar cal = Calendar.getInstance();
            Calendar now = Calendar.getInstance();
            cal.setTime(new Date(obj.getEventDate()));
            DateTimeObj start = obj.getCustomStart();
            DateTimeObj end = obj.getCustomEnd();
            if (now.get(Calendar.WEEK_OF_MONTH) == cal.get(Calendar.WEEK_OF_MONTH)) {
                sb.append("Class: ").append(obj.getEventTempName()).append("\n")
                        .append("Room: ").append(obj.getRoomInfoText()).append("\n")
                        .append("Date: ").append(cal.get(Calendar.DAY_OF_MONTH)).append("/").append(cal.get(Calendar.MONTH)).append("\n")
                        .append("Time: ").append(start.getHour()).append(":").append(start.getMinute()).append("-").append(end.getHour()).append(":").append(end.getMinute()).append("\n");

                result.append(sb).append("\n");
            }

        }
//        System.out.println(result.toString().length());
        return result.toString();
    }

    /**
     *  Messages
     * **/
    private SendMessage sendStartCommandReply(Long chatId) {
        return SendMessage.builder()
                .chatId(chatId)
                .text(MessageTextEnum.START_MESSAGE.getMessage())
                .parseMode("HTML")
                .build();
    }

    private SendMessage sendRtuScheduleMessage(String schedule, Long chatId) {
        return SendMessage.builder()
                .chatId(chatId)
                .text(schedule)
                .build();
    }

    private AnswerCallbackQuery sendAnswerCallbackQuery(String id) {
        return new AnswerCallbackQuery(id);
    }

    private EditMessageReplyMarkup sendEditMessageReplyMarkup(Integer messageId, Long chatId, InlineKeyboardMarkup inlineKeyboardMarkup) {
        return EditMessageReplyMarkup.builder()
                .messageId(messageId)
                .chatId(chatId)
                .replyMarkup(inlineKeyboardMarkup)
                .build();
    }

    private SendMessage sendInlineKeyboard(Long chatId, String messageText, InlineKeyboardMarkup inlineKeyboardMarkup) {
        return SendMessage.builder()
                .chatId(chatId)
                .text(messageText)
                .replyMarkup(inlineKeyboardMarkup)
                .build();
    }

    public static class SortByGroup implements Comparator<RtuGroupObj> {
        public int compare(RtuGroupObj a, RtuGroupObj b) {
            return Integer.valueOf(a.getGroup()).compareTo(Integer.valueOf(b.getGroup()));
        }
    }
}
