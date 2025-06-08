package ui;

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
import java.time.LocalDate;
import java.util.ResourceBundle;

public class ClassFormController implements Initializable {
    
    @FXML private Text formTitle;
    @FXML private ComboBox<Major> majorComboBox;
    @FXML private TextField gradeField;
    @FXML private TextField classNumberField;
    @FXML private ComboBox<Counselor> counselorComboBox;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;
    
    private Class currentClass; // 当前编辑的班级（编辑模式使用）
    private boolean isEditMode = false;
    private ClassFormCallback callback;
    
    // 辅导员数据
    private ObservableList<Counselor> counselorData = FXCollections.observableArrayList();
    
    // 专业数据
    private ObservableList<Major> majorData = FXCollections.observableArrayList();
    
    // 回调接口
    public interface ClassFormCallback {
        void onClassSaved(entity.Class savedClass, boolean isEdit);
        void onFormCancelled();
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeMajorComboBox();
        initializeCounselorComboBox();
        loadMajorData();
        loadCounselorData();
    }
    
    private void initializeMajorComboBox() {
        // 设置ComboBox的显示格式
        majorComboBox.setConverter(new StringConverter<Major>() {
            @Override
            public String toString(Major major) {
                if (major == null) {
                    return null;
                }
                return major.getMajorName() + " (" + major.getMajorId() + ")";
            }
            
            @Override
            public Major fromString(String string) {
                return null;
            }
        });
        
        // 设置数据
        majorComboBox.setItems(majorData);
    }
    
    private void initializeCounselorComboBox() {
        // 设置ComboBox的显示格式
        counselorComboBox.setConverter(new StringConverter<Counselor>() {
            @Override
            public String toString(Counselor counselor) {
                if (counselor == null) {
                    return null;
                }
                return counselor.getName() + " (" + counselor.getCounselorId() + ")";
            }
            
            @Override
            public Counselor fromString(String string) {
                return null;
            }
        });
        
        // 设置数据
        counselorComboBox.setItems(counselorData);
    }
    
    private void loadMajorData() {
        // 添加测试数据（实际项目中从数据库加载）
        majorData.add(new Major("CS001", "计算机科学与技术"));
        majorData.add(new Major("SE001", "软件工程"));
        majorData.add(new Major("EE001", "电子信息工程"));
        majorData.add(new Major("ME001", "机械工程"));
        majorData.add(new Major("AI001", "人工智能"));
        majorData.add(new Major("DS001", "数据科学与大数据技术"));
    }
    
    private void loadCounselorData() {
        // 添加测试数据（实际项目中从数据库加载）
        counselorData.add(new Counselor("T001", "张老师", "女", LocalDate.of(1985, 3, 15), 
                "13812345678", "计算机科学与工程学院", "123456", 1));
        counselorData.add(new Counselor("T002", "李老师", "男", LocalDate.of(1980, 7, 22), 
                "13998765432", "电子信息工程学院", "123456", 2));
        counselorData.add(new Counselor("T003", "王老师", "女", LocalDate.of(1988, 11, 8), 
                "13765432109", "机械工程学院", "123456", 3));
        counselorData.add(new Counselor("T004", "刘老师", "男", LocalDate.of(1982, 5, 12), 
                "13656789012", "软件工程学院", "123456", 4));
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
        for (Major major : majorData) {
            if (major.getMajorId().equals(classObj.getMajorId())) {
                majorComboBox.setValue(major);
                break;
            }
        }
        gradeField.setText(classObj.getGradeNumber());
        classNumberField.setText(classObj.getClassNumber());
        
        // 设置辅导员选择
        if (classObj.getCounselorId() != null) {
            for (Counselor counselor : counselorData) {
                if (counselor.getCounselorId().equals(classObj.getCounselorId())) {
                    counselorComboBox.setValue(counselor);
                    break;
                }
            }
        }
        
        // 在编辑模式下，专业和班级信息不可修改（作为主键的一部分）
        majorComboBox.setDisable(true);
        gradeField.setEditable(false);
        classNumberField.setEditable(false);
        gradeField.getStyleClass().add("readonly-field");
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
                    Counselor selectedCounselor = counselorComboBox.getValue();
                    if (selectedCounselor != null) {
                        classToSave.setCounselorId(selectedCounselor.getCounselorId());
                        classToSave.setCounselorName(selectedCounselor.getName());
                    } else {
                        classToSave.setCounselorId(null);
                        classToSave.setCounselorName(null);
                    }
                } else {
                    // 新增模式：创建新班级
                    Major selectedMajor = majorComboBox.getValue();
                    Counselor selectedCounselor = counselorComboBox.getValue();
                    String counselorId = selectedCounselor != null ? selectedCounselor.getCounselorId() : null;
                    String counselorName = selectedCounselor != null ? selectedCounselor.getName() : null;
                    
                    classToSave = new Class(
                        selectedMajor.getMajorId(),
                        selectedMajor.getMajorName(),
                        gradeField.getText().trim(),
                        classNumberField.getText().trim(),
                        counselorId,
                        counselorName,
                        0 // 新班级初始学生数为0
                    );
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
        if (majorComboBox.getValue() == null) {
            majorComboBox.getStyleClass().add("error");
            isValid = false;
        }
        
        if (gradeField.getText().trim().isEmpty()) {
            gradeField.getStyleClass().add("error");
            isValid = false;
        }
        
        if (classNumberField.getText().trim().isEmpty()) {
            classNumberField.getStyleClass().add("error");
            isValid = false;
        }
        
        // 验证年级格式（应该是4位数字）
        if (!gradeField.getText().trim().isEmpty()) {
            try {
                int grade = Integer.parseInt(gradeField.getText().trim());
                if (grade < 1900 || grade > 2100) {
                    gradeField.getStyleClass().add("error");
                    showError("年级应该是1900-2100之间的有效年份");
                    return false;
                }
            } catch (NumberFormatException e) {
                gradeField.getStyleClass().add("error");
                showError("年级应该是数字格式");
                return false;
            }
        }
        
        // 验证班级编号格式（应该是两位数字）
        if (!classNumberField.getText().trim().isEmpty()) {
            String classNumber = classNumberField.getText().trim();
            if (!classNumber.matches("\\d{2}")) {
                classNumberField.getStyleClass().add("error");
                showError("班级编号应该是两位数字格式（如：01、02）");
                return false;
            }
        }
        
        if (!isValid) {
            showError("请填写所有必填字段");
        }
        
        return isValid;
    }
    
    private void clearErrorStyles() {
        majorComboBox.getStyleClass().remove("error");
        gradeField.getStyleClass().remove("error");
        classNumberField.getStyleClass().remove("error");
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("输入错误");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void closeWindow() {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }
} 