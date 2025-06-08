package entity;

import javafx.beans.property.*;
import java.time.LocalDate;

public class Counselor {
    private final StringProperty counselorId = new SimpleStringProperty();
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty gender = new SimpleStringProperty();
    private final ObjectProperty<LocalDate> birthDate = new SimpleObjectProperty<>();
    private final StringProperty phoneNumber = new SimpleStringProperty();
    private final StringProperty departmentName = new SimpleStringProperty();
    private final StringProperty password = new SimpleStringProperty();
    private final IntegerProperty departmentId = new SimpleIntegerProperty();

    // 构造函数
    public Counselor() {}

    public Counselor(String counselorId, String name, String gender, LocalDate birthDate, 
                    String phoneNumber, String departmentName, String password, int departmentId) {
        setCounselorId(counselorId);
        setName(name);
        setGender(gender);
        setBirthDate(birthDate);
        setPhoneNumber(phoneNumber);
        setDepartmentName(departmentName);
        setPassword(password);
        setDepartmentId(departmentId);
    }

    // 辅导员工号
    public String getCounselorId() { return counselorId.get(); }
    public void setCounselorId(String counselorId) { this.counselorId.set(counselorId); }
    public StringProperty counselorIdProperty() { return counselorId; }

    // 姓名
    public String getName() { return name.get(); }
    public void setName(String name) { this.name.set(name); }
    public StringProperty nameProperty() { return name; }

    // 性别
    public String getGender() { return gender.get(); }
    public void setGender(String gender) { this.gender.set(gender); }
    public StringProperty genderProperty() { return gender; }

    // 出生日期
    public LocalDate getBirthDate() { return birthDate.get(); }
    public void setBirthDate(LocalDate birthDate) { this.birthDate.set(birthDate); }
    public ObjectProperty<LocalDate> birthDateProperty() { return birthDate; }

    // 手机号
    public String getPhoneNumber() { return phoneNumber.get(); }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber.set(phoneNumber); }
    public StringProperty phoneNumberProperty() { return phoneNumber; }

    // 院系名称
    public String getDepartmentName() { return departmentName.get(); }
    public void setDepartmentName(String departmentName) { this.departmentName.set(departmentName); }
    public StringProperty departmentNameProperty() { return departmentName; }

    // 密码
    public String getPassword() { return password.get(); }
    public void setPassword(String password) { this.password.set(password); }
    public StringProperty passwordProperty() { return password; }

    // 院系ID
    public int getDepartmentId() { return departmentId.get(); }
    public void setDepartmentId(int departmentId) { this.departmentId.set(departmentId); }
    public IntegerProperty departmentIdProperty() { return departmentId; }

    @Override
    public String toString() {
        return "Counselor{" +
                "counselorId='" + getCounselorId() + '\'' +
                ", name='" + getName() + '\'' +
                ", gender='" + getGender() + '\'' +
                ", birthDate=" + getBirthDate() +
                ", phoneNumber='" + getPhoneNumber() + '\'' +
                ", departmentName='" + getDepartmentName() + '\'' +
                '}';
    }
} 