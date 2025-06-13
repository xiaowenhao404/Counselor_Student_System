package ui;

import dao.ClassDaoImpl;
import dao.CounselorDaoImpl;
import dao.MajorDaoImpl;
import entity.Class;
import entity.Counselor;
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
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.List;

public class StudentFormController implements Initializable {

    @FXML
    private Text formTitle;
    @FXML
    private TextField studentIdField;
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
    private ComboBox<String> majorComboBox;
    @FXML
    private TextField gradeField;
    @FXML
    private ComboBox<String> classComboBox;
    @FXML
    private ComboBox<String> counselorComboBox;
    @FXML
    private VBox counselorDisplayContainer;
    @FXML
    private TextField counselorDisplayField;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;

    private boolean isEditMode = false;
    private Student currentStudent;
    private StudentFormCallback callback;
    private boolean passwordVisible = false;

    private MajorDaoImpl majorDao;
    private ClassDaoImpl classDao;
    private CounselorDaoImpl counselorDao;

    private Map<String, String> majorNameToIdMap;
    private Map<String, String> majorIdToNameMap;
    private Map<String, String> counselorNameToIdMap;
    private Map<String, String> counselorIdToNameMap;

    // 回调接口，用于通知父窗口操作结果
    public interface StudentFormCallback {
        void onStudentSaved(Student student, boolean isEdit);

        void onFormCancelled();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        majorDao = new MajorDaoImpl();
        classDao = new ClassDaoImpl();
        counselorDao = new CounselorDaoImpl();

        initializeComboBoxes();
        setupFieldValidation();
        setupDependentFields();
        setupPasswordFields();

        // 默认隐藏辅导员选择下拉框，显示只读辅导员信息
        counselorComboBox.setVisible(false);
        counselorDisplayContainer.setVisible(true);
    }

    private void initializeComboBoxes() {
        // 初始化性别下拉框
        genderComboBox.setItems(FXCollections.observableArrayList("男", "女"));

        // 初始化专业下拉框
        try {
            majorNameToIdMap = majorDao.getAllMajors();
            majorIdToNameMap = new java.util.HashMap<>();
            majorNameToIdMap.forEach((name, id) -> majorIdToNameMap.put(id, name));
            majorComboBox.setItems(FXCollections.observableArrayList(majorNameToIdMap.keySet()));
        } catch (SQLException e) {
            showError("加载专业信息失败: " + e.getMessage());
            e.printStackTrace();
        }

        // 初始化辅导员下拉框
        try {
            counselorNameToIdMap = counselorDao.getAllCounselors();
            counselorIdToNameMap = new java.util.HashMap<>();
            counselorNameToIdMap.forEach((name, id) -> counselorIdToNameMap.put(id, name));
            counselorComboBox.setItems(FXCollections.observableArrayList(counselorNameToIdMap.keySet()));
        } catch (SQLException e) {
            showError("加载辅导员信息失败: " + e.getMessage());
            e.printStackTrace();
        }

        // 初始化年级下拉框数据（当专业改变时会动态更新班级）
        gradeField.setText(""); // 清空默认年级
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

        // 姓名验证：不允许输入数字和标点符号
        nameField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.matches(".*[\\d,.?!，。！？、；：‘’（）【】《》——…￥].*")) {
                nameField.setText(oldValue);
            }
        });
    }

    private void setupDependentFields() {
        // 当专业或年级改变时，更新班级选项
        majorComboBox.valueProperty().addListener((obs, oldVal, newVal) -> updateClassOptions());
        gradeField.textProperty().addListener((obs, oldVal, newVal) -> updateClassOptions());

        // 当班级选择改变时，自动更新辅导员信息
        classComboBox.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldVal, newVal) -> updateCounselorInfo());
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
        String selectedMajorName = majorComboBox.getValue();
        String gradeNumber = gradeField.getText().trim();

        if (selectedMajorName != null && !selectedMajorName.isEmpty() &&
                gradeNumber != null && !gradeNumber.isEmpty()) {
            String majorId = majorNameToIdMap.get(selectedMajorName);
            if (majorId != null) {
                try {
                    // 根据专业编号获取所有班级列表
                    List<Class> allClasses = classDao.getClassesByMajorId(majorId);
                    // 筛选出符合年级的班级
                    List<String> classNames = FXCollections.observableArrayList();
                    for (Class clazz : allClasses) {
                        if (gradeNumber.equals(clazz.getGradeNumber())) {
                            classNames.add(clazz.getClassId()); // 使用班级编号作为显示名称
                        }
                    }
                    classComboBox.setItems(FXCollections.observableArrayList(classNames));
                    classComboBox.setVisibleRowCount(classNames.size() > 0 ? classNames.size() : 1);
                } catch (SQLException e) {
                    showError("加载班级信息失败: " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                classComboBox.setItems(FXCollections.observableArrayList());
                classComboBox.setVisibleRowCount(1);
            }
        } else {
            classComboBox.setItems(FXCollections.observableArrayList());
            classComboBox.setVisibleRowCount(1);
        }
        classComboBox.setValue(null);
        // 清空辅导员信息
        counselorDisplayField.setText("");
    }

    private void updateCounselorInfo() {
        String selectedClassName = classComboBox.getSelectionModel().getSelectedItem();
        String selectedMajorName = majorComboBox.getValue();
        String gradeNumber = gradeField.getText().trim();

        if (selectedClassName != null && !selectedClassName.isEmpty() &&
                selectedMajorName != null && !selectedMajorName.isEmpty() &&
                gradeNumber != null && !gradeNumber.isEmpty()) {
            try {
                String selectedMajorId = majorNameToIdMap.get(selectedMajorName);
                if (selectedMajorId != null) {
                    // 根据专业+年级+班级编号精确查找班级
                    Class targetClass = classDao.getClassByFullKey(selectedMajorId, gradeNumber, selectedClassName);
                    if (targetClass != null) {
                        String counselorId = targetClass.getCounselorId();
                        if (counselorId != null && !counselorId.isEmpty()) {
                            try {
                                Counselor counselor = counselorDao.getCounselorById(counselorId);
                                if (counselor != null) {
                                    counselorDisplayField.setText(counselor.getName());
                                } else {
                                    counselorDisplayField.setText("暂无辅导员");
                                }
                            } catch (SQLException ex) {
                                counselorDisplayField.setText("获取辅导员失败");
                                ex.printStackTrace();
                            }
                        } else {
                            counselorDisplayField.setText("暂无辅导员");
                        }
                    } else {
                        counselorDisplayField.setText("班级信息不存在");
                    }
                }
            } catch (SQLException e) {
                counselorDisplayField.setText("获取辅导员信息失败");
                e.printStackTrace();
            }
        } else {
            counselorDisplayField.setText("");
        }
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
            birthDatePicker.setValue(student.getDateOfBirth());
            phoneField.setText(student.getPhoneNumber());
            gradeField.setText(student.getGradeNumber());

            // 使用 ID 到名称的映射来设置 ComboBox 值
            majorComboBox.setValue(majorIdToNameMap.get(student.getMajorId()));
            student.setGradeNumber(gradeField.getText().trim());

            // 更新班级选项后设置班级值
            updateClassOptions();
            // 在更新班级选项后，根据学生的班级ID找到对应的班级编号并设置
            try {
                Class studentClass = classDao.getClassByFullKey(student.getMajorId(), student.getGradeNumber(),
                        student.getClassId());
                if (studentClass != null) {
                    // 设置选中的班级
                    classComboBox.getSelectionModel().select(studentClass.getClassId());
                    // 在设置班级后，手动触发辅导员信息更新
                    updateCounselorInfo();
                }
            } catch (SQLException e) {
                showError("加载学生班级信息失败: " + e.getMessage());
                e.printStackTrace();
                classComboBox.getSelectionModel().clearSelection(); // Fallback
            }

            // 编辑模式下：隐藏辅导员下拉框，显示只读辅导员信息
            counselorComboBox.setVisible(false);
            counselorDisplayContainer.setVisible(true);

            // 辅导员信息已经通过updateCounselorInfo()方法设置，无需重复处理
            studentIdField.setDisable(true); // 学号在编辑模式下不可修改
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
            student.setDateOfBirth(birthDatePicker.getValue());
            student.setPhoneNumber(phoneField.getText().trim());
            String currentPassword = passwordVisible ? showPasswordField.getText() : passwordField.getText();
            student.setPassword(currentPassword);

            // 使用名称到 ID 的映射来设置 ID
            student.setMajorId(majorNameToIdMap.get(majorComboBox.getValue()));
            student.setGradeNumber(gradeField.getText().trim());

            // 获取班级名称对应的班级ID
            String selectedClassName = classComboBox.getSelectionModel().getSelectedItem();
            if (selectedClassName != null && !selectedClassName.isEmpty()) {
                try {
                    // 根据专业+年级+班级编号精确获取班级ID
                    String majorId = majorNameToIdMap.get(majorComboBox.getValue());
                    String gradeNumber = gradeField.getText().trim();

                    if (majorId != null && gradeNumber != null && !gradeNumber.isEmpty()) {
                        Class targetClass = classDao.getClassByFullKey(majorId, gradeNumber, selectedClassName);
                        if (targetClass != null) {
                            student.setClassId(targetClass.getClassId());
                        } else {
                            showError("选择的班级不存在，请重新选择。");
                            return;
                        }
                    } else {
                        showError("专业或年级信息不完整，无法确定班级。");
                        return;
                    }
                } catch (SQLException e) {
                    showError("获取班级ID失败: " + e.getMessage());
                    e.printStackTrace();
                    return;
                }
            } else {
                student.setClassId(null); // 如果未选择班级，则设置为null
            }

            // 辅导员信息处理：学生实体不直接包含辅导员信息，此部分逻辑移除
            // 辅导员的关联由班级管理，如果需要修改班级的辅导员，应通过班级管理界面进行

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
        } else if (nameField.getText().matches(".*[\\d,.?!，。！？、；：‘’（）【】《》——…￥].*")) {
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

        // 手机号格式校验
        String phone = phoneField.getText().trim();
        if (phone.isEmpty()) {
            errors.append("• 手机号码不能为空\n");
            addErrorStyle(phoneField);
        } else if (!phone.matches("1[3-9]\\d{9}")) {
            errors.append("• 手机号格式不正确\n");
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

        if (majorComboBox.getValue() == null || majorComboBox.getValue().isEmpty()) {
            errors.append("• 专业不能为空\n");
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

        if (classComboBox.getSelectionModel().getSelectedItem() == null) {
            errors.append("• 班级不能为空\n");
            addErrorStyle(classComboBox);
        } else {
            removeErrorStyle(classComboBox);
        }

        // 辅导员信息现在通过班级自动确定，不需要验证
        removeErrorStyle(counselorComboBox);

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