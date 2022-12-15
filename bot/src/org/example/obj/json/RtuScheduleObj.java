package org.example.obj.json;

public class RtuScheduleObj {

    private final Integer eventDateId;
    private final Integer eventId;
    private final Integer statusId;
    private final String eventTempName;
    private final String roomInfoText;
    private final String eventTempNameEn;
    private final String roomInfoTextEn;
    private final Long eventDate;
    private final DateTimeObj customStart;
    private final DateTimeObj customEnd;

    public RtuScheduleObj(Integer eventDateId, Integer eventId, Integer statusId, String eventTempName, String roomInfoText, String eventTempNameEn, String roomInfoTextEn, Long eventDate, DateTimeObj customStart, DateTimeObj customEnd) {
        this.eventDateId = eventDateId;
        this.eventId = eventId;
        this.statusId = statusId;
        this.eventTempName = eventTempName;
        this.roomInfoText = roomInfoText;
        this.eventTempNameEn = eventTempNameEn;
        this.roomInfoTextEn = roomInfoTextEn;
        this.eventDate = eventDate;
        this.customStart = customStart;
        this.customEnd = customEnd;
    }

    public Integer getEventDateId() {
        return eventDateId;
    }

    public Integer getEventId() {
        return eventId;
    }

    public Integer getStatusId() {
        return statusId;
    }

    public String getEventTempName() {
        return eventTempName;
    }

    public String getRoomInfoText() {
        return roomInfoText;
    }

    public String getEventTempNameEn() {
        return eventTempNameEn;
    }

    public String getRoomInfoTextEn() {
        return roomInfoTextEn;
    }

    public Long getEventDate() {
        return eventDate;
    }

    public DateTimeObj getCustomStart() {
        return customStart;
    }

    public DateTimeObj getCustomEnd() {
        return customEnd;
    }

    @Override
    public int hashCode() {
        return eventDateId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof RtuScheduleObj) {
            RtuScheduleObj that = (RtuScheduleObj) obj;
            return this.eventDateId.equals(that.eventDateId) &&
                    this.eventId.equals(that.eventId) &&
                    this.statusId.equals(that.statusId) &&
                    this.eventTempName.equals(that.eventTempName) &&
                    this.roomInfoText.equals(that.roomInfoText) &&
                    this.eventTempNameEn.equals(that.eventTempNameEn) &&
                    this.roomInfoTextEn.equals(that.roomInfoTextEn) &&
                    this.eventDate.equals(that.eventDate) &&
                    this.customStart.equals(that.customStart) &&
                    this.customEnd.equals(that.customEnd);
        }
        return false;
    }
}
