package entity;

public class Class {
    private String majorId; // 专业编号
    private String gradeNumber; // 年级编号
    private String classId; // 班级编号
    private String counselorId; // 辅导员工号

    public Class() {
    }

    public Class(String majorId, String gradeNumber, String classId, String counselorId) {
        this.majorId = majorId;
        this.gradeNumber = gradeNumber;
        this.classId = classId;
        this.counselorId = counselorId;
    }

    public String getMajorId() {
        return majorId;
    }

    public void setMajorId(String majorId) {
        this.majorId = majorId;
    }

    public String getGradeNumber() {
        return gradeNumber;
    }

    public void setGradeNumber(String gradeNumber) {
        this.gradeNumber = gradeNumber;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getCounselorId() {
        return counselorId;
    }

    public void setCounselorId(String counselorId) {
        this.counselorId = counselorId;
    }

    // 为兼容性保留，组合显示班级信息
    public String getClassName() {
        return classId; // 返回班级编号作为班级名称
    }

    public void setClassName(String className) {
        this.classId = className; // 设置班级编号
    }

    // 获取完整的班级标识（专业+年级+班级）
    public String getFullClassId() {
        return majorId + "-" + gradeNumber + "-" + classId;
    }

    @Override
    public String toString() {
        return "Class{" +
                "majorId='" + majorId + '\'' +
                ", gradeNumber='" + gradeNumber + '\'' +
                ", classId='" + classId + '\'' +
                ", counselorId='" + counselorId + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Class aClass = (Class) o;
        return majorId.equals(aClass.majorId) &&
                gradeNumber.equals(aClass.gradeNumber) &&
                classId.equals(aClass.classId);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(majorId, gradeNumber, classId);
    }
}