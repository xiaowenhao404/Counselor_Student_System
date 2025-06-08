package entity;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Class {
    private final StringProperty majorId = new SimpleStringProperty();
    private final StringProperty majorName = new SimpleStringProperty();
    private final StringProperty gradeNumber = new SimpleStringProperty();
    private final StringProperty classNumber = new SimpleStringProperty();
    private final StringProperty counselorId = new SimpleStringProperty();
    private final StringProperty counselorName = new SimpleStringProperty();
    private final IntegerProperty studentCount = new SimpleIntegerProperty();

    // 构造函数
    public Class() {}

    public Class(String majorId, String majorName, String gradeNumber, String classNumber, 
                 String counselorId, String counselorName, int studentCount) {
        setMajorId(majorId);
        setMajorName(majorName);
        setGradeNumber(gradeNumber);
        setClassNumber(classNumber);
        setCounselorId(counselorId);
        setCounselorName(counselorName);
        setStudentCount(studentCount);
    }

    // 专业编号
    public String getMajorId() { return majorId.get(); }
    public void setMajorId(String majorId) { this.majorId.set(majorId); }
    public StringProperty majorIdProperty() { return majorId; }

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

    // 辅导员工号
    public String getCounselorId() { return counselorId.get(); }
    public void setCounselorId(String counselorId) { this.counselorId.set(counselorId); }
    public StringProperty counselorIdProperty() { return counselorId; }

    // 辅导员姓名
    public String getCounselorName() { return counselorName.get(); }
    public void setCounselorName(String counselorName) { this.counselorName.set(counselorName); }
    public StringProperty counselorNameProperty() { return counselorName; }

    // 学生人数
    public int getStudentCount() { return studentCount.get(); }
    public void setStudentCount(int studentCount) { this.studentCount.set(studentCount); }
    public IntegerProperty studentCountProperty() { return studentCount; }

    // 用于显示的复合信息
    public String getFullClassName() {
        return getMajorName() + " " + getGradeNumber() + "级" + getClassNumber() + "班";
    }

    public String getCounselorDisplayName() {
        return getCounselorName() != null && !getCounselorName().isEmpty() ? getCounselorName() : "未分配";
    }

    @Override
    public String toString() {
        return getFullClassName();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Class that = (Class) obj;
        return getMajorId().equals(that.getMajorId()) &&
               getGradeNumber().equals(that.getGradeNumber()) &&
               getClassNumber().equals(that.getClassNumber());
    }

    @Override
    public int hashCode() {
        return (getMajorId() + getGradeNumber() + getClassNumber()).hashCode();
    }
} 