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
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Map;
import java.util.ResourceBundle;

public class CounselorFormController implements Initializable {

    @FXML
    private Text formTitle;
    @FXML
    private TextField counselorIdField;
    @FXML
    private TextField nameField;
    @FXML
    private ComboBox<String> genderComboBox;
    @FXML
    private DatePicker birthDatePicker;
    @FXML
    private TextField phoneField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField showPasswordField;
    @FXML
    private ImageView passwordVisibilityIcon;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;

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

        // 姓名验证：不允许输入数字和标点符号
        nameField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.matches(".*[\\d,.?!，。！？、；：‘'（）【】《》——…￥].*")) {
                nameField.setText(oldValue);
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
            birthDatePicker.setValue(counselor.getDateOfBirth());
            phoneField.setText(counselor.getPhoneNumber());

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
            counselor.setDateOfBirth(birthDatePicker.getValue());
            counselor.setPhoneNumber(phoneField.getText().trim());
            String currentPassword = passwordVisible ? showPasswordField.getText() : passwordField.getText();
            counselor.setPassword(currentPassword);

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
        } else if (nameField.getText().matches(".*[\\d,.?!，。！？、；：'（）【】《》——…￥].*")) {
            errors.append("• 姓名不能包含数字或标点符号\n");
            addErrorStyle(nameField);
        } else {
            removeErrorStyle(nameField);
        }

        if (genderComboBox.getValue() == null || genderComboBox.getValue().isEmpty()) {
            errors.append("• 性别不能为空\n");
            addErrorStyle(genderComboBox);
        } else {
            removeErrorStyle(genderComboBox);
        }

        if (birthDatePicker.getValue() == null) {
            errors.append("• 出生日期不能为空\n");
            addErrorStyle(birthDatePicker);
        } else {
            removeErrorStyle(birthDatePicker);
        }

        if (phoneField.getText().trim().isEmpty()) {
            errors.append("• 手机号码不能为空\n");
            addErrorStyle(phoneField);
        } else {
            removeErrorStyle(phoneField);
        }

        if (passwordField.getText().trim().isEmpty() && showPasswordField.getText().trim().isEmpty()) {
            errors.append("• 密码不能为空\n");
            addErrorStyle(passwordField);
            addErrorStyle(showPasswordField);
        } else {
            removeErrorStyle(passwordField);
            removeErrorStyle(showPasswordField);
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
            showError(errors.toString());
            return false;
        }

        return true;
    }

    private void addErrorStyle(Control control) {
        control.getStyleClass().add("error-field");
    }

    private void removeErrorStyle(Control control) {
        control.getStyleClass().remove("error-field");
    }

    private void closeWindow() {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("错误");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("成功");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}