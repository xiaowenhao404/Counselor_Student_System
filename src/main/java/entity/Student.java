package entity;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDate;

public class Student {
    private final StringProperty studentId = new SimpleStringProperty();
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty gender = new SimpleStringProperty();
    private LocalDate birthDate;
    private final StringProperty phoneNumber = new SimpleStringProperty();
    private final StringProperty majorName = new SimpleStringProperty();
    private final StringProperty gradeNumber = new SimpleStringProperty();
    private final StringProperty classNumber = new SimpleStringProperty();
    private final StringProperty counselorName = new SimpleStringProperty();
    private final StringProperty password = new SimpleStringProperty();
    private final StringProperty majorId = new SimpleStringProperty();

    // 构造函数
    public Student() {}

    public Student(String studentId, String name, String gender, LocalDate birthDate, 
                   String phoneNumber, String majorName, String gradeNumber, 
                   String classNumber, String counselorName) {
        setStudentId(studentId);
        setName(name);
        setGender(gender);
        setBirthDate(birthDate);
        setPhoneNumber(phoneNumber);
        setMajorName(majorName);
        setGradeNumber(gradeNumber);
        setClassNumber(classNumber);
        setCounselorName(counselorName);
    }

    // 学生学号
    public String getStudentId() { return studentId.get(); }
    public void setStudentId(String studentId) { this.studentId.set(studentId); }
    public StringProperty studentIdProperty() { return studentId; }

    // 姓名
    public String getName() { return name.get(); }
    public void setName(String name) { this.name.set(name); }
    public StringProperty nameProperty() { return name; }

    // 性别
    public String getGender() { return gender.get(); }
    public void setGender(String gender) { this.gender.set(gender); }
    public StringProperty genderProperty() { return gender; }

    // 出生日期
    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

    // 手机号码
    public String getPhoneNumber() { return phoneNumber.get(); }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber.set(phoneNumber); }
    public StringProperty phoneNumberProperty() { return phoneNumber; }

    // 专业名称
    public String getMajorName() { return majorName.get(); }
    public void setMajorName(String majorName) { this.majorName.set(majorName); }
    public StringProperty majorNameProperty() { return majorName; }

    // 年级编号
    public String getGradeNumber() { return gradeNumber.get(); }
    public void setGradeNumber(String gradeNumber) { this.gradeNumber.set(gradeNumber); }
    public StringProperty gradeNumberProperty() { return gradeNumber; }

    // 班级编号
    public String getClassNumber() { return classNumber.get(); }
    public void setClassNumber(String classNumber) { this.classNumber.set(classNumber); }
    public StringProperty classNumberProperty() { return classNumber; }

    // 辅导员姓名
    public String getCounselorName() { return counselorName.get(); }
    public void setCounselorName(String counselorName) { this.counselorName.set(counselorName); }
    public StringProperty counselorNameProperty() { return counselorName; }

    // 密码（用于添加/编辑时使用，不在表格中显示）
    public String getPassword() { return password.get(); }
    public void setPassword(String password) { this.password.set(password); }
    public StringProperty passwordProperty() { return password; }

    // 专业编号（用于数据库操作，不在表格中显示）
    public String getMajorId() { return majorId.get(); }
    public void setMajorId(String majorId) { this.majorId.set(majorId); }
    public StringProperty majorIdProperty() { return majorId; }

    @Override
    public String toString() {
        return "Student{" +
                "studentId='" + getStudentId() + '\'' +
                ", name='" + getName() + '\'' +
                ", gender='" + getGender() + '\'' +
                ", birthDate=" + birthDate +
                ", phoneNumber='" + getPhoneNumber() + '\'' +
                ", majorName='" + getMajorName() + '\'' +
                ", gradeNumber='" + getGradeNumber() + '\'' +
                ", classNumber='" + getClassNumber() + '\'' +
                ", counselorName='" + getCounselorName() + '\'' +
                '}';
    }
} 