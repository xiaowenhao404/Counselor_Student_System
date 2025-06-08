package ui;

import entity.Student;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class StudentFormController implements Initializable {

    @FXML private Text formTitle;
    @FXML private TextField studentIdField;
    @FXML private TextField nameField;
    @FXML private ComboBox<String> genderComboBox;
    @FXML private DatePicker birthDatePicker;
    @FXML private TextField phoneField;
    @FXML private PasswordField passwordField;
    @FXML private TextField showPasswordField;
    @FXML private ImageView passwordVisibilityIcon;
    @FXML private ComboBox<String> majorComboBox;
    @FXML private TextField gradeField;
    @FXML private ComboBox<String> classComboBox;
    @FXML private ComboBox<String> counselorComboBox;
    @FXML private VBox counselorDisplayContainer;
    @FXML private TextField counselorDisplayField;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    private boolean isEditMode = false;
    private Student currentStudent;
    private StudentFormCallback callback;
    private boolean passwordVisible = false;

    // 回调接口，用于通知父窗口操作结果
    public interface StudentFormCallback {
        void onStudentSaved(Student student, boolean isEdit);
        void onFormCancelled();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeComboBoxes();
        setupFieldValidation();
        setupDependentFields();
        setupPasswordFields();
    }

    private void initializeComboBoxes() {
        // 初始化性别下拉框
        genderComboBox.setItems(FXCollections.observableArrayList("男", "女"));
        
        // 初始化专业下拉框（这里使用模拟数据，后续可从数据库获取）
        majorComboBox.setItems(FXCollections.observableArrayList(
            "计算机科学与技术", "软件工程", "网络工程", "信息安全", 
            "数据科学与大数据技术", "人工智能"
        ));
        
        // 初始化年级下拉框数据（当专业改变时会动态更新班级）
        gradeField.setText("2021"); // 默认年级
        
        // 初始化辅导员下拉框（模拟数据）
        counselorComboBox.setItems(FXCollections.observableArrayList(
            "李老师", "王老师", "张老师", "刘老师", "陈老师"
        ));
    }

    private void setupFieldValidation() {
        // 学号验证：只允许数字和字母
        studentIdField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.matches("[a-zA-Z0-9]*")) {
                studentIdField.setText(oldValue);
            }
        });
        
        // 年级验证：只允许数字
        gradeField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.matches("\\d*")) {
                gradeField.setText(oldValue);
            }
        });
        
        // 手机号验证：只允许数字，限制长度
        phoneField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (!newValue.matches("\\d*")) {
                    phoneField.setText(oldValue);
                } else if (newValue.length() > 11) {
                    phoneField.setText(newValue.substring(0, 11));
                }
            }
        });
    }

    private void setupDependentFields() {
        // 当专业或年级改变时，更新班级选项
        majorComboBox.valueProperty().addListener((obs, oldVal, newVal) -> updateClassOptions());
        gradeField.textProperty().addListener((obs, oldVal, newVal) -> updateClassOptions());
    }
    
    private void setupPasswordFields() {
        // 设置双向绑定，确保两个密码框内容同步
        passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (passwordVisible) {
                showPasswordField.setText(newValue);
            }
        });
        
        showPasswordField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (passwordVisible) {
                passwordField.setText(newValue);
            }
        });
    }
    
    @FXML
    private void togglePasswordVisibility(MouseEvent event) {
        passwordVisible = !passwordVisible;
        
        if (passwordVisible) {
            // 显示密码
            showPasswordField.setText(passwordField.getText());
            showPasswordField.setVisible(true);
            passwordField.setVisible(false);
            passwordVisibilityIcon.setImage(new Image(getClass().getResourceAsStream("/images/visible.png")));
        } else {
            // 隐藏密码
            passwordField.setText(showPasswordField.getText());
            passwordField.setVisible(true);
            showPasswordField.setVisible(false);
            passwordVisibilityIcon.setImage(new Image(getClass().getResourceAsStream("/images/invisible.png")));
        }
    }

    private void updateClassOptions() {
        String major = majorComboBox.getValue();
        String grade = gradeField.getText();
        
        if (major != null && !major.isEmpty() && grade != null && !grade.isEmpty()) {
            // 根据专业和年级动态生成班级选项（这里使用模拟数据）
            classComboBox.setItems(FXCollections.observableArrayList(
                "01", "02", "03", "04", "05"
            ));
        } else {
            classComboBox.setItems(FXCollections.observableArrayList());
        }
        classComboBox.setValue(null);
    }

    public void setEditMode(Student student) {
        if (student != null) {
            isEditMode = true;
            currentStudent = student;
            formTitle.setText("编辑学生");
            
            // 填充表单数据
            studentIdField.setText(student.getStudentId());
            nameField.setText(student.getName());
            genderComboBox.setValue(student.getGender());
            birthDatePicker.setValue(student.getBirthDate());
            phoneField.setText(student.getPhoneNumber());
            majorComboBox.setValue(student.getMajorName());
            gradeField.setText(student.getGradeNumber());
            
            // 更新班级选项后设置班级值
            updateClassOptions();
            classComboBox.setValue(student.getClassNumber());
            
            // 编辑模式下：隐藏辅导员下拉框，显示只读辅导员信息
            counselorComboBox.setVisible(false);
            counselorDisplayField.setText(student.getCounselorName());
            counselorDisplayContainer.setVisible(true);
            
            // 编辑模式下学号不可修改
            studentIdField.setDisable(true);
        }
    }

    public void setCallback(StudentFormCallback callback) {
        this.callback = callback;
    }

    @FXML
    private void handleSave() {
        if (validateForm()) {
            Student student = isEditMode ? currentStudent : new Student();
            
            // 设置学生信息
            student.setStudentId(studentIdField.getText().trim());
            student.setName(nameField.getText().trim());
            student.setGender(genderComboBox.getValue());
            student.setBirthDate(birthDatePicker.getValue());
            student.setPhoneNumber(phoneField.getText().trim());
            String currentPassword = passwordVisible ? showPasswordField.getText() : passwordField.getText();
            student.setPassword(currentPassword);
            student.setMajorName(majorComboBox.getValue());
            student.setGradeNumber(gradeField.getText().trim());
            student.setClassNumber(classComboBox.getValue());
            
            // 辅导员信息处理：编辑模式下不允许修改，新增模式下从下拉框获取
            if (isEditMode) {
                // 编辑模式：保持原有辅导员不变
                // student.getCounselorName() 已经是当前的辅导员，无需设置
            } else {
                // 新增模式：从下拉框获取选中的辅导员
                student.setCounselorName(counselorComboBox.getValue());
            }
            
            // 通知父窗口
            if (callback != null) {
                callback.onStudentSaved(student, isEditMode);
            }
            
            // 关闭窗口
            closeWindow();
        }
    }

    @FXML
    private void handleCancel() {
        if (callback != null) {
            callback.onFormCancelled();
        }
        closeWindow();
    }

    private boolean validateForm() {
        StringBuilder errors = new StringBuilder();
        
        // 验证必填字段
        if (studentIdField.getText().trim().isEmpty()) {
            errors.append("• 学生学号不能为空\n");
            addErrorStyle(studentIdField);
        } else {
            removeErrorStyle(studentIdField);
        }
        
        if (nameField.getText().trim().isEmpty()) {
            errors.append("• 姓名不能为空\n");
            addErrorStyle(nameField);
        } else {
            removeErrorStyle(nameField);
        }
        
        if (genderComboBox.getValue() == null) {
            errors.append("• 请选择性别\n");
            addErrorStyle(genderComboBox);
        } else {
            removeErrorStyle(genderComboBox);
        }
        
        String currentPassword = passwordVisible ? showPasswordField.getText().trim() : passwordField.getText().trim();
        if (currentPassword.isEmpty()) {
            errors.append("• 密码不能为空\n");
            if (passwordVisible) {
                addErrorStyle(showPasswordField);
            } else {
                addErrorStyle(passwordField);
            }
        } else {
            removeErrorStyle(passwordField);
            removeErrorStyle(showPasswordField);
        }
        
        if (majorComboBox.getValue() == null) {
            errors.append("• 请选择专业\n");
            addErrorStyle(majorComboBox);
        } else {
            removeErrorStyle(majorComboBox);
        }
        
        if (gradeField.getText().trim().isEmpty()) {
            errors.append("• 年级不能为空\n");
            addErrorStyle(gradeField);
        } else {
            removeErrorStyle(gradeField);
        }
        
        if (classComboBox.getValue() == null) {
            errors.append("• 请选择班级\n");
            addErrorStyle(classComboBox);
        } else {
            removeErrorStyle(classComboBox);
        }
        
        // 验证手机号格式
        String phone = phoneField.getText().trim();
        if (!phone.isEmpty() && !phone.matches("1[3-9]\\d{9}")) {
            errors.append("• 手机号格式不正确\n");
            addErrorStyle(phoneField);
        } else {
            removeErrorStyle(phoneField);
        }
        
        if (errors.length() > 0) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("表单验证");
            alert.setHeaderText("请修正以下错误：");
            alert.setContentText(errors.toString());
            alert.showAndWait();
            return false;
        }
        
        return true;
    }

    private void addErrorStyle(Control control) {
        if (!control.getStyleClass().contains("error")) {
            control.getStyleClass().add("error");
        }
    }

    private void removeErrorStyle(Control control) {
        control.getStyleClass().remove("error");
    }

    private void closeWindow() {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }
} 