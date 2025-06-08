package ui;

import entity.Counselor;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class CounselorFormController implements Initializable {

    @FXML private Text formTitle;
    @FXML private TextField counselorIdField;
    @FXML private TextField nameField;
    @FXML private ComboBox<String> genderComboBox;
    @FXML private DatePicker birthDatePicker;
    @FXML private TextField phoneField;
    @FXML private PasswordField passwordField;
    @FXML private TextField showPasswordField;
    @FXML private ImageView passwordVisibilityIcon;
    @FXML private ComboBox<String> departmentComboBox;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    private boolean isEditMode = false;
    private Counselor currentCounselor;
    private CounselorFormCallback callback;
    private boolean passwordVisible = false;

    // 回调接口，用于通知父窗口操作结果
    public interface CounselorFormCallback {
        void onCounselorSaved(Counselor counselor, boolean isEdit);
        void onFormCancelled();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeComboBoxes();
        setupFieldValidation();
        setupPasswordFields();
    }

    private void initializeComboBoxes() {
        // 初始化性别选择
        genderComboBox.setItems(FXCollections.observableArrayList("男", "女"));
        
        // 初始化院系选择（模拟数据，实际应从数据库获取）
        departmentComboBox.setItems(FXCollections.observableArrayList(
            "计算机科学与工程学院",
            "电子信息工程学院", 
            "机械工程学院",
            "土木工程学院",
            "经济管理学院",
            "外国语学院",
            "数学与统计学院",
            "物理科学与技术学院"
        ));
    }

    private void setupFieldValidation() {
        // 工号验证：只允许数字和字母
        counselorIdField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.matches("[a-zA-Z0-9]*")) {
                counselorIdField.setText(oldValue);
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

    public void setEditMode(Counselor counselor) {
        if (counselor != null) {
            isEditMode = true;
            currentCounselor = counselor;
            formTitle.setText("编辑辅导员");
            
            // 填充表单数据
            counselorIdField.setText(counselor.getCounselorId());
            nameField.setText(counselor.getName());
            genderComboBox.setValue(counselor.getGender());
            birthDatePicker.setValue(counselor.getBirthDate());
            phoneField.setText(counselor.getPhoneNumber());
            departmentComboBox.setValue(counselor.getDepartmentName());
            
            // 编辑模式下工号不可修改
            counselorIdField.setDisable(true);
        }
    }

    public void setCallback(CounselorFormCallback callback) {
        this.callback = callback;
    }

    @FXML
    private void handleSave() {
        if (validateForm()) {
            Counselor counselor = isEditMode ? currentCounselor : new Counselor();
            
            // 设置辅导员信息
            counselor.setCounselorId(counselorIdField.getText().trim());
            counselor.setName(nameField.getText().trim());
            counselor.setGender(genderComboBox.getValue());
            counselor.setBirthDate(birthDatePicker.getValue());
            counselor.setPhoneNumber(phoneField.getText().trim());
            String currentPassword = passwordVisible ? showPasswordField.getText() : passwordField.getText();
            counselor.setPassword(currentPassword);
            counselor.setDepartmentName(departmentComboBox.getValue());
            
            // 通知父窗口
            if (callback != null) {
                callback.onCounselorSaved(counselor, isEditMode);
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
        if (counselorIdField.getText().trim().isEmpty()) {
            errors.append("• 辅导员工号不能为空\n");
            addErrorStyle(counselorIdField);
        } else {
            removeErrorStyle(counselorIdField);
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
        
        if (departmentComboBox.getValue() == null) {
            errors.append("• 请选择院系\n");
            addErrorStyle(departmentComboBox);
        } else {
            removeErrorStyle(departmentComboBox);
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