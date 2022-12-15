package org.example.obj;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RtuPageDataObj {

    private Map<String, String> availableSemesterList;
    private List<RtuFacultyObj> facultyObjList;

    public RtuPageDataObj() {
        this.availableSemesterList = new HashMap<>();
        this.facultyObjList = new ArrayList<>();
    }

    public Map<String, String> getAvailableSemesterList() {
        return availableSemesterList;
    }

    public void setAvailableSemesterList(Map<String, String> availableSemesterList) {
        this.availableSemesterList = availableSemesterList;
    }

    public List<RtuFacultyObj> getFacultyObjList() {
        return facultyObjList;
    }

    public void setFacultyObjList(List<RtuFacultyObj> facultyObjList) {
        this.facultyObjList = facultyObjList;
    }
}
