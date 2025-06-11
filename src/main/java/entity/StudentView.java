package entity;

import java.time.LocalDate;

public class StudentView {
    private String studentId; // 学生学号
    private String studentName; // 姓名
    private String gender; // 性别
    private LocalDate dateOfBirth; // 出生日期
    private String phoneNumber; // 手机号码
    private String majorName; // 专业名称 (视图中有)
    private String gradeNumber; // 年级编号
    private String className; // 班级编号 (在视图中，用作班级名称)
    private String counselorName; // 辅导员姓名 (视图中有)

    // 注意：以下字段在学生视图中不存在，但为了兼容性保留
    private String majorId; // 专业编号 (视图中没有，但可能在业务逻辑中需要)
    private String counselorId; // 辅导员工号 (视图中没有，但可能在业务逻辑中需要)

    public StudentView() {
    }

    public StudentView(String studentId, String studentName, String gender, LocalDate dateOfBirth, String phoneNumber,
            String majorName, String gradeNumber, String className, String counselorName) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.phoneNumber = phoneNumber;
        this.majorName = majorName;
        this.gradeNumber = gradeNumber;
        this.className = className;
        this.counselorName = counselorName;
    }

    // Getters and Setters
    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

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

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
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

    @Override
    public String toString() {
        return "StudentView{" +
                "studentId='" + studentId + '\'' +
                ", studentName='" + studentName + '\'' +
                ", gender='" + gender + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", majorId='" + majorId + '\'' +
                ", majorName='" + majorName + '\'' +
                ", gradeNumber='" + gradeNumber + '\'' +
                ", className='" + className + '\'' +
                ", counselorId='" + counselorId + '\'' +
                ", counselorName='" + counselorName + '\'' +
                '}';
    }
}