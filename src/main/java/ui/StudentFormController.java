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
    }

    private void setupDependentFields() {
        // 当专业或年级改变时，更新班级选项
        majorComboBox.valueProperty().addListener((obs, oldVal, newVal) -> updateClassOptions());
        gradeField.textProperty().addListener((obs, oldVal, newVal) -> updateClassOptions());
        
        // 当班级改变时，自动更新辅导员信息
        classComboBox.valueProperty().addListener((obs, oldVal, newVal) -> updateCounselorInfo());
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
        // String gradeId = gradeField.getText(); // 年级现在不再是筛选班级的必要条件

        if (selectedMajorName != null && !selectedMajorName.isEmpty()) {
            String majorId = majorNameToIdMap.get(selectedMajorName);
            if (majorId != null) {
                try {
                    // 根据专业编号获取班级列表
                    List<Class> classes = classDao.getClassesByMajorId(majorId);
                    // 将班级名称添加到下拉框
                    List<String> classNames = FXCollections.observableArrayList();
                    for (Class clazz : classes) {
                        classNames.add(clazz.getClassName());
                    }
                    classComboBox.setItems(FXCollections.observableArrayList(classNames));
                } catch (SQLException e) {
                    showError("加载班级信息失败: " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                classComboBox.setItems(FXCollections.observableArrayList());
            }
        } else {
            classComboBox.setItems(FXCollections.observableArrayList());
        }
        classComboBox.setValue(null);
    }

    private void updateCounselorInfo() {
        String selectedClassName = classComboBox.getValue();
        if (selectedClassName != null && !selectedClassName.isEmpty()) {
            try {
                // 根据班级名称获取对应的辅导员信息
                String selectedMajorId = majorNameToIdMap.get(majorComboBox.getValue());
                if (selectedMajorId != null) {
                    List<Class> classes = classDao.getClassesByMajorId(selectedMajorId);
                    for (Class clazz : classes) {
                        if (clazz.getClassName().equals(selectedClassName)) {
                            String counselorId = clazz.getCounselorId();
                            if (counselorId != null && !counselorId.isEmpty()) {
                                // 从数据库直接查询辅导员信息
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
                            break;
                        }
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
            // 在更新班级选项后，根据学生的班级ID找到对应的班级名称并设置
            try {
                Class studentClass = classDao.getClassById(student.getClassId());
                if (studentClass != null) {
                    classComboBox.setValue(studentClass.getClassName());
                    // 在设置班级后，手动触发辅导员信息更新
                    updateCounselorInfo();
                } else {
                    classComboBox.setValue(null);
                }
            } catch (SQLException e) {
                showError("加载学生班级信息失败: " + e.getMessage());
                e.printStackTrace();
                classComboBox.setValue(null); // Fallback
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
            String selectedClassName = classComboBox.getValue();
            if (selectedClassName != null && !selectedClassName.isEmpty()) {
                try {
                    // 通过班级名称获取班级ID，这里需要从数据库查询班级信息
                    // 考虑到性能，如果班级数量不多，可以考虑在初始化时构建班级名称到ID的映射
                    // 目前直接查询数据库，确保精确匹配
                    List<Class> classesInMajor = classDao
                            .getClassesByMajorId(majorNameToIdMap.get(majorComboBox.getValue()));
                    String classIdToSet = null;
                    for (Class cls : classesInMajor) {
                        if (cls.getClassName().equals(selectedClassName)) {
                            classIdToSet = cls.getClassId();
                            break;
                        }
                    }
                    student.setClassId(classIdToSet);
                } catch (SQLException e) {
                    showError("获取班级ID失败: " + e.getMessage());
                    e.printStackTrace();
                    student.setClassId(null); // 设置为null或报错
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

        if (classComboBox.getValue() == null || classComboBox.getValue().isEmpty()) {
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