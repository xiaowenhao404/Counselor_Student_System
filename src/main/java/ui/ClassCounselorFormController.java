package ui;

import entity.Class;
import entity.Counselor;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class ClassCounselorFormController implements Initializable {

    @FXML private Text formTitle;
    
    // 班级信息字段（只读）
    @FXML private TextField majorNameField;
    @FXML private TextField gradeField;
    @FXML private TextField classNumberField;
    @FXML private TextField studentCountField;
    
    // 辅导员修改字段
    @FXML private TextField currentCounselorField;
    @FXML private ComboBox<String> newCounselorComboBox;
    
    // 按钮
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    private Class currentClass;
    private ClassCounselorFormCallback callback;

    // 回调接口，用于通知父窗口操作结果
    public interface ClassCounselorFormCallback {
        void onCounselorChanged(Class classObj, String newCounselorId, String newCounselorName);
        void onFormCancelled();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeComboBox();
        setupFieldValidation();
    }

    private void initializeComboBox() {
        // 初始化辅导员下拉框（模拟数据，后续可从数据库获取）
        newCounselorComboBox.setItems(FXCollections.observableArrayList(
            "李老师", "王老师", "张老师", "刘老师", "陈老师", "赵老师"
        ));
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
            majorNameField.setText(classObj.getMajorName());
            gradeField.setText(classObj.getGradeNumber() + "级");
            classNumberField.setText(classObj.getClassNumber() + "班");
            studentCountField.setText(classObj.getStudentCount() + " 人");
            
            // 填充当前辅导员信息
            String currentCounselor = classObj.getCounselorName();
            currentCounselorField.setText(currentCounselor != null && !currentCounselor.isEmpty() ? 
                                        currentCounselor : "未分配");
            
            // 设置新辅导员下拉框的默认值为当前辅导员（如果有的话）
            if (currentCounselor != null && !currentCounselor.isEmpty()) {
                newCounselorComboBox.setValue(currentCounselor);
            }
            
            // 如果当前没有辅导员，修改标题
            if (currentCounselor == null || currentCounselor.isEmpty()) {
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
            
            // 检查是否真的有变化
            String currentCounselor = currentClass.getCounselorName();
            if ((currentCounselor == null || currentCounselor.isEmpty()) && newCounselorName != null) {
                // 从未分配到分配辅导员
                performCounselorChange(newCounselorName);
            } else if (currentCounselor != null && !currentCounselor.equals(newCounselorName)) {
                // 更换辅导员
                performCounselorChange(newCounselorName);
            } else {
                // 没有变化
                showInfo("提示", "辅导员没有变更");
                return;
            }
        }
    }

    private void performCounselorChange(String newCounselorName) {
        // 模拟辅导员ID（实际应该从数据库查询）
        String newCounselorId = getCounselorIdByName(newCounselorName);
        
        // 更新班级对象
        currentClass.setCounselorId(newCounselorId);
        currentClass.setCounselorName(newCounselorName);
        
        // 通知父窗口
        if (callback != null) {
            callback.onCounselorChanged(currentClass, newCounselorId, newCounselorName);
        }
        
        // 关闭窗口
        closeWindow();
    }

    private String getCounselorIdByName(String counselorName) {
        // 模拟根据姓名获取辅导员ID的逻辑
        switch (counselorName) {
            case "李老师": return "T001";
            case "王老师": return "T002";
            case "张老师": return "T003";
            case "刘老师": return "T004";
            case "陈老师": return "T005";
            case "赵老师": return "T006";
            default: return null;
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
        if (newCounselorComboBox.getValue() == null) {
            errors.append("• 请选择新辅导员\n");
            addErrorStyle(newCounselorComboBox);
        } else {
            removeErrorStyle(newCounselorComboBox);
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