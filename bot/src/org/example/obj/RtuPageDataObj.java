package org.example.obj;

import org.example.obj.json.RtuGroupObj;

import java.util.*;

public class RtuPageDataObj {

    private final Map<String, String> availableSemesterList;
    private List<RtuFacultyObj> facultyObjList;

    private final Set<RtuGroupObj> groupObjSet;

    public RtuPageDataObj() {
        this.availableSemesterList = new HashMap<>();
        this.facultyObjList = new ArrayList<>();
        this.groupObjSet = new HashSet<>();
    }

    public Map<String, String> getAvailableSemesterList() {
        return availableSemesterList;
    }

    public List<RtuFacultyObj> getFacultyObjList() {
        return facultyObjList;
    }

    public void setFacultyObjList(List<RtuFacultyObj> facultyObjList) {
        this.facultyObjList = facultyObjList;
    }

    public Set<RtuGroupObj> getGroupObjSet() {
        return groupObjSet;
    }
}
