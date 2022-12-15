package org.example.obj.json;

import org.example.obj.ChatExtended;

public class RtuGroupObj {
    private final Integer semesterProgramId;
    private final Integer semesterId;
    private final Integer programId;
    private final Integer course;
    private final String group;

    public RtuGroupObj(Integer semesterProgramId, Integer semesterId, Integer programId, Integer course, String group) {
        this.semesterProgramId = semesterProgramId;
        this.semesterId = semesterId;
        this.programId = programId;
        this.course = course;
        this.group = group;
    }

    public Integer getSemesterProgramId() {
        return semesterProgramId;
    }

    public Integer getSemesterId() {
        return semesterId;
    }

    public Integer getProgramId() {
        return programId;
    }

    public Integer getCourse() {
        return course;
    }

    public String getGroup() {
        return group;
    }

    @Override
    public int hashCode() {
        return semesterProgramId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof RtuGroupObj) {
            RtuGroupObj that = (RtuGroupObj) obj;
            return this.group.equals(that.group) && this.course.equals(that.course) && this.programId.equals(that.programId) && this.semesterId.equals(that.semesterId) && this.semesterProgramId.equals(that.semesterProgramId);
        }
        return false;
    }

    public boolean equalsToChatExtended(ChatExtended o) {
        return Integer.valueOf(o.getSelectedProgramId()).equals(this.programId) && o.getSelectedGroupId().equals(this.group) && Integer.valueOf(o.getSelectedCourseId()).equals(this.course) && Integer.valueOf(o.getSelectedSemesterId()).equals(this.semesterId);
    }
}
