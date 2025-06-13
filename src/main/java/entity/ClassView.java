package entity;

public class ClassView {
    private String majorId;
    private String majorName;
    private String gradeNumber;
    private String classId;
    private String counselorId;
    private String counselorName;
    private String className;
    private int studentCount;

    public ClassView() {
    }

    public ClassView(String majorId, String majorName, String gradeNumber, String classId, String className,
            String counselorId,
            String counselorName, int studentCount) {
        this.majorId = majorId;
        this.majorName = majorName;
        this.gradeNumber = gradeNumber;
        this.classId = classId;
        this.className = className;
        this.counselorId = counselorId;
        this.counselorName = counselorName;
        this.studentCount = studentCount;
    }

    // Getters and Setters
    public String getMajorId() {
        return majorId;
    }

    public void setMajorId(String majorId) {
        this.majorId = majorId;
    }

    public String getMajorName() {
        return majorName;
    }

    public void setMajorName(String majorName) {
        this.majorName = majorName;
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

    public String getCounselorName() {
        return counselorName;
    }

    public void setCounselorName(String counselorName) {
        this.counselorName = counselorName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public int getStudentCount() {
        return studentCount;
    }

    public void setStudentCount(int studentCount) {
        this.studentCount = studentCount;
    }

    @Override
    public String toString() {
        return "ClassView{" +
                "majorId='" + majorId + '\'' +
                ", majorName='" + majorName + '\'' +
                ", gradeNumber='" + gradeNumber + '\'' +
                ", classId='" + classId + '\'' +
                ", className='" + className + '\'' +
                ", counselorId='" + counselorId + '\'' +
                ", counselorName='" + counselorName + '\'' +
                ", studentCount=" + studentCount +
                '}';
    }
}