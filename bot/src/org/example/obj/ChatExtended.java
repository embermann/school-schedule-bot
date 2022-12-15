package org.example.obj;

import org.example.enums.RtuDialogStateEnum;
import org.telegram.telegrambots.meta.api.objects.Chat;

public class ChatExtended {

    private boolean inlineDialogInitiated;
    private Integer messageId;
    private String selectedProgramId;
    private String selectedCourseId;
    private String selectedGroupId;
    private String selectedSemesterId;
    private RtuDialogStateEnum state;
    private Chat chatData;

    public ChatExtended(Chat chatData) {
        this.inlineDialogInitiated = false;
        this.state = RtuDialogStateEnum.NOTHING;
        this.chatData = chatData;
    }

    public String getSelectedSemesterId() {
        return selectedSemesterId;
    }

    public void setSelectedSemesterId(String selectedSemesterId) {
        this.selectedSemesterId = selectedSemesterId;
    }

    public boolean isInlineDialogInitiated() {
        return inlineDialogInitiated;
    }

    public void setInlineDialogInitiated(boolean inlineDialogInitiated) {
        this.inlineDialogInitiated = inlineDialogInitiated;
    }

    public Integer getMessageId() {
        return messageId;
    }

    public void setMessageId(Integer messageId) {
        this.messageId = messageId;
    }

    public RtuDialogStateEnum getState() {
        return state;
    }

    public void setState(RtuDialogStateEnum state) {
        this.state = state;
    }

    public Chat getChatData() {
        return chatData;
    }

    public void setChatData(Chat chatData) {
        this.chatData = chatData;
    }

    public String getSelectedProgramId() {
        return selectedProgramId;
    }

    public void setSelectedProgramId(String selectedProgramId) {
        this.selectedProgramId = selectedProgramId;
    }

    public String getSelectedCourseId() {
        return selectedCourseId;
    }

    public void setSelectedCourseId(String selectedCourseId) {
        this.selectedCourseId = selectedCourseId;
    }

    public String getSelectedGroupId() {
        return selectedGroupId;
    }

    public void setSelectedGroupId(String selectedGroupId) {
        this.selectedGroupId = selectedGroupId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatExtended that = (ChatExtended) o;
        return this.chatData.getId().equals(that.chatData.getId()) && this.chatData.getType().equals(that.chatData.getType());
    }


}
