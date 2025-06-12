package ui;

import dao.CounselorDaoImpl;
import dao.MajorDaoImpl;
import entity.Class;
import entity.Counselor;
import entity.Major;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class ClassFormController implements Initializable {

    @FXML
    private Text formTitle;
    @FXML
    private ComboBox<String> majorComboBox;
    @FXML
    private TextField gradeField;
    @FXML
    private TextField classNumberField;
    @FXML
    private ComboBox<String> counselorComboBox;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;

    private Class currentClass; // 当前编辑的班级（编辑模式使用）
    private boolean isEditMode = false;
    private ClassFormCallback callback;

    private MajorDaoImpl majorDao;
    private CounselorDaoImpl counselorDao;

    private Map<String, String> majorNameToIdMap; // 专业名称 -> 专业编号
    private Map<String, String> majorIdToNameMap; // 专业编号 -> 专业名称
    private Map<String, String> counselorNameToIdMap; // 辅导员姓名 -> 工号
    private Map<String, String> counselorIdToNameMap; // 辅导员工号 -> 姓名

    // 回调接口
    public interface ClassFormCallback {
        void onClassSaved(entity.Class savedClass, boolean isEdit);

        void onFormCancelled();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        majorDao = new MajorDaoImpl();
        counselorDao = new CounselorDaoImpl();

        initializeMajorComboBox();
        initializeCounselorComboBox();
    }

    private void initializeMajorComboBox() {
        // 初始化专业下拉框
        try {
            majorNameToIdMap = majorDao.getAllMajors();
            majorIdToNameMap = new HashMap<>();
            majorNameToIdMap.forEach((name, id) -> majorIdToNameMap.put(id, name));
            majorComboBox.setItems(FXCollections.observableArrayList(majorNameToIdMap.keySet()));
        } catch (SQLException e) {
            showError("加载专业信息失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void initializeCounselorComboBox() {
        // 初始化辅导员下拉框
        try {
            counselorNameToIdMap = counselorDao.getAllCounselors();
            counselorIdToNameMap = new HashMap<>();
            counselorNameToIdMap.forEach((name, id) -> counselorIdToNameMap.put(id, name));
            counselorComboBox.setItems(FXCollections.observableArrayList(counselorNameToIdMap.keySet()));
        } catch (SQLException e) {
            showError("加载辅导员信息失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void setCallback(ClassFormCallback callback) {
        this.callback = callback;
    }

    public void setEditMode(Class classObj) {
        this.currentClass = classObj;
        this.isEditMode = true;

        // 更新标题
        formTitle.setText("编辑班级");
        saveButton.setText("保存修改");

        // 填充表单数据
        // 设置专业选择
        majorComboBox.setValue(majorIdToNameMap.get(classObj.getMajorId()));
        gradeField.setText(classObj.getGradeNumber()); // 显示年级
        classNumberField.setText(classObj.getClassName()); // 显示班级名称

        // 设置辅导员选择
        if (classObj.getCounselorId() != null && !classObj.getCounselorId().isEmpty()) {
            counselorComboBox.setValue(counselorIdToNameMap.get(classObj.getCounselorId()));
        } else {
            counselorComboBox.setValue(null);
        }

        // 在编辑模式下，专业、年级和班级编号不可修改（作为主键的一部分）
        majorComboBox.setDisable(true);
        gradeField.setEditable(false);
        gradeField.getStyleClass().add("readonly-field");
        classNumberField.setEditable(false);
        classNumberField.getStyleClass().add("readonly-field");
    }

    @FXML
    private void handleSave() {
        if (validateForm()) {
            try {
                Class classToSave;

                if (isEditMode) {
                    // 编辑模式：更新现有班级
                    classToSave = currentClass;
                    // 只允许修改辅导员
                    String selectedCounselorName = counselorComboBox.getValue();
                    String newCounselorId = (selectedCounselorName != null && !selectedCounselorName.isEmpty())
                            ? counselorNameToIdMap.get(selectedCounselorName)
                            : null;
                    classToSave.setCounselorId(newCounselorId);
                } else {
                    // 新增模式：创建新班级
                    classToSave = new Class(); // 使用无参构造函数
                    String selectedMajorName = majorComboBox.getValue();
                    String majorId = majorNameToIdMap.get(selectedMajorName);
                    String gradeNumber = gradeField.getText().trim();
                    String selectedCounselorName = counselorComboBox.getValue();
                    String counselorId = (selectedCounselorName != null && !selectedCounselorName.isEmpty())
                            ? counselorNameToIdMap.get(selectedCounselorName)
                            : null;

                    classToSave.setMajorId(majorId);
                    classToSave.setGradeNumber(gradeNumber); // 设置年级编号
                    classToSave.setClassId(classNumberField.getText().trim()); // 设置班级编号
                    classToSave.setCounselorId(counselorId);
                }

                // 调用回调
                if (callback != null) {
                    callback.onClassSaved(classToSave, isEditMode);
                }

                // 关闭窗口
                closeWindow();

            } catch (Exception e) {
                showError("保存失败：" + e.getMessage());
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
        // 清除之前的错误样式
        clearErrorStyles();

        boolean isValid = true;

        // 验证必填字段
        if (majorComboBox.getValue() == null || majorComboBox.getValue().isEmpty()) {
            majorComboBox.getStyleClass().add("error-field");
            isValid = false;
        } else {
            majorComboBox.getStyleClass().remove("error-field");
        }

        if (gradeField.getText().trim().isEmpty()) {
            gradeField.getStyleClass().add("error-field");
            isValid = false;
        } else {
            gradeField.getStyleClass().remove("error-field");
        }

        if (classNumberField.getText().trim().isEmpty()) {
            classNumberField.getStyleClass().add("error-field");
            isValid = false;
        } else {
            classNumberField.getStyleClass().remove("error-field");
        }

        // 辅导员不是必填字段
        if (counselorComboBox.getValue() != null && !counselorComboBox.getValue().isEmpty()) {
            counselorComboBox.getStyleClass().remove("error-field");
        }

        String classId = classNumberField.getText().trim();
        if (!classId.matches("^[1-9]\\d*$")) {
            showError("班级编号只能为1、2、3等正整数，不能有前导0！");
            classNumberField.getStyleClass().add("error-field");
            isValid = false;
        }

        return isValid;
    }

    private void clearErrorStyles() {
        majorComboBox.getStyleClass().remove("error-field");
        gradeField.getStyleClass().remove("error-field");
        classNumberField.getStyleClass().remove("error-field");
        counselorComboBox.getStyleClass().remove("error-field");
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("错误");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void closeWindow() {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }
}