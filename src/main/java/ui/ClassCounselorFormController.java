package ui;

import dao.CounselorDaoImpl;
import dao.MajorDaoImpl;
import entity.Class;
import entity.Counselor;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.SQLException;
import java.util.Map;
import java.util.ResourceBundle;

public class ClassCounselorFormController implements Initializable {

    @FXML
    private Text formTitle;

    // 班级信息字段（只读）
    @FXML
    private TextField majorNameField;
    @FXML
    private TextField gradeField;
    @FXML
    private TextField classNumberField;
    // 学生人数字段已从界面中移除

    // 辅导员修改字段
    @FXML
    private TextField currentCounselorField;
    @FXML
    private ComboBox<String> newCounselorComboBox;

    // 按钮
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;

    private Class currentClass;
    private ClassCounselorFormCallback callback;

    private MajorDaoImpl majorDao;
    private CounselorDaoImpl counselorDao;

    private Map<String, String> majorIdToNameMap; // 用于根据专业ID获取专业名称
    private Map<String, String> counselorNameToIdMap; // 用于根据辅导员姓名获取工号
    private Map<String, String> counselorIdToNameMap; // 用于根据辅导员工号获取姓名

    // 回调接口，用于通知父窗口操作结果
    public interface ClassCounselorFormCallback {
        void onCounselorChanged(Class classObj, String newCounselorId, String newCounselorName);

        void onFormCancelled();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        majorDao = new MajorDaoImpl();
        counselorDao = new CounselorDaoImpl();
        initializeComboBox();
        setupFieldValidation();
    }

    private void initializeComboBox() {
        // 初始化性别选择
        // genderComboBox.setItems(FXCollections.observableArrayList("男", "女")); //
        // 此处应无性别选择

        // 初始化专业ID到名称的映射
        try {
            Map<String, String> allMajors = majorDao.getAllMajors();
            majorIdToNameMap = new java.util.HashMap<>();
            allMajors.forEach((name, id) -> majorIdToNameMap.put(id, name));
        } catch (SQLException e) {
            showError("加载专业信息失败: " + e.getMessage());
            e.printStackTrace();
        }

        // 初始化辅导员下拉框
        try {
            counselorNameToIdMap = counselorDao.getAllCounselors();
            counselorIdToNameMap = new java.util.HashMap<>();
            counselorNameToIdMap.forEach((name, id) -> counselorIdToNameMap.put(id, name));
            newCounselorComboBox.setItems(FXCollections.observableArrayList(counselorNameToIdMap.keySet()));
        } catch (SQLException e) {
            showError("加载辅导员信息失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupFieldValidation() {
        // 监听新辅导员选择，移除错误样式
        newCounselorComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            removeErrorStyle(newCounselorComboBox);
        });
    }

    public void setClassInfo(Class classObj) {
        if (classObj != null) {
            this.currentClass = classObj;

            // 填充班级信息（只读）
            majorNameField.setText(majorIdToNameMap.getOrDefault(classObj.getMajorId(), "未知专业")); // 从ID获取名称
            gradeField.setText(classObj.getGradeNumber()); // 显示年级编号
            classNumberField.setText(classObj.getClassName()); // 使用className作为班级显示名称

            // 填充当前辅导员信息
            String currentCounselorName = "未分配";
            if (classObj.getCounselorId() != null && !classObj.getCounselorId().isEmpty()) {
                currentCounselorName = counselorIdToNameMap.getOrDefault(classObj.getCounselorId(), "未知辅导员");
            }
            currentCounselorField.setText(currentCounselorName);

            // 设置新辅导员下拉框的默认值为当前辅导员（如果有的话）
            if (!"未分配".equals(currentCounselorName) && !"未知辅导员".equals(currentCounselorName)) {
                newCounselorComboBox.setValue(currentCounselorName);
            }

            // 如果当前没有辅导员，修改标题
            if ("未分配".equals(currentCounselorName) || "未知辅导员".equals(currentCounselorName)) {
                formTitle.setText("分配班级辅导员");
            }
        }
    }

    public void setCallback(ClassCounselorFormCallback callback) {
        this.callback = callback;
    }

    @FXML
    private void handleSave() {
        if (validateForm()) {
            String newCounselorName = newCounselorComboBox.getValue();
            String newCounselorId = counselorNameToIdMap.get(newCounselorName);

            // 检查是否真的有变化
            String currentCounselorId = currentClass.getCounselorId();
            String currentCounselorName = (currentCounselorId != null && !currentCounselorId.isEmpty())
                    ? counselorIdToNameMap.getOrDefault(currentCounselorId, "")
                    : "";

            if (!newCounselorName.equals(currentCounselorName)) {
                // 通知父窗口
                if (callback != null) {
                    callback.onCounselorChanged(currentClass, newCounselorId, newCounselorName);
                }
                // 关闭窗口
                closeWindow();
            } else {
                // 没有变化
                showInfo("提示", "辅导员没有变更");
            }
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
        if (newCounselorComboBox.getValue() == null || newCounselorComboBox.getValue().isEmpty()) {
            errors.append("• 请选择新辅导员\n");
            addErrorStyle(newCounselorComboBox);
        } else {
            removeErrorStyle(newCounselorComboBox);
        }

        if (errors.length() > 0) {
            showError(errors.toString()); // 使用 showError 辅助方法
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

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("错误");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void closeWindow() {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }
}