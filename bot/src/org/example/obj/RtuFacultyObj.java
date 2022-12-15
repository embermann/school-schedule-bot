package org.example.obj;

import java.util.List;

public class RtuFacultyObj {
    private String id;
    private String label;
    private List<RtuProgramObj> programObjList;

    public RtuFacultyObj(String id, String label, List<RtuProgramObj> programObjList) {
        this.id = id;
        this.label = label;
        this.programObjList = programObjList;
    }

    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public List<RtuProgramObj> getProgramObjList() {
        return programObjList;
    }

    public static class RtuProgramObj {
        private String value;
        private String label;
        private List<RtuCourseObj> courseObjList;

        public RtuProgramObj(String value, String label) {
            this.value = value;
            this.label = label;
        }

        public String getValue() {
            return value;
        }

        public String getLabel() {
            return label;
        }

        public static class RtuCourseObj {
            private String value;
            private List<RtuGroupObj> groupObjList;

            public RtuCourseObj(String value, List<RtuGroupObj> groupObjList) {
                this.value = value;
                this.groupObjList = groupObjList;
            }

            public String getValue() {
                return value;
            }

            public List<RtuGroupObj> getGroupObjList() {
                return groupObjList;
            }

            public static class RtuGroupObj {
                private String value;
                private List<String> semesterProgramObjList;

                public RtuGroupObj(String value, List<String> semesterProgramObjList) {
                    this.value = value;
                    this.semesterProgramObjList = semesterProgramObjList;
                }

                public String getValue() {
                    return value;
                }

                public List<String> getSemesterProgramObjList() {
                    return semesterProgramObjList;
                }
            }
        }
    }
}
