package ui;

import entity.Student;
import entity.Counselor;
import entity.Consultation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class AdminMainController implements Initializable {
    
    // 顶部导航栏
    @FXML private Button logoutButton;
    
    // 底部标签按钮
    @FXML private Button studentManagementTab;
    @FXML private Button counselorManagementTab;
    @FXML private Button consultationManagementTab;
    
    // 主内容面板
    @FXML private VBox studentManagementPanel;
    @FXML private VBox counselorManagementPanel;
    @FXML private VBox consultationManagementPanel;
    
    // 学生管理相关控件
    @FXML private Button addStudentButton;
    @FXML private Button editStudentButton;
    @FXML private Button deleteStudentButton;
    @FXML private TableView<Student> studentTable;
    
    // 辅导员管理相关控件
    @FXML private Button addCounselorButton;
    @FXML private Button editCounselorButton;
    @FXML private Button deleteCounselorButton;
    @FXML private TableView<Counselor> counselorTable;
    
    // 咨询管理相关控件
    @FXML private Button deleteConsultationButton;
    @FXML private Button toggleHighlightButton;
    @FXML private TableView<Consultation> consultationTable;
    
    private String currentTab = "student"; // 当前选中的标签
    
    // 学生数据列表
    private ObservableList<Student> studentData = FXCollections.observableArrayList();
    
    // 辅导员数据列表
    private ObservableList<Counselor> counselorData = FXCollections.observableArrayList();
    
    // 咨询数据列表
    private ObservableList<Consultation> consultationData = FXCollections.observableArrayList();
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 初始化表格列
        initializeStudentTable();
        initializeCounselorTable();
        initializeConsultationTable();
        
        // 设置初始状态
        showPanel("student");
        
        // 设置学生管理标签为初始选中状态
        studentManagementTab.getStyleClass().add("selected");
        
        // 设置按钮初始状态
        updateButtonStates();
    }
    
    @FXML
    private void handleTabSwitch(ActionEvent event) {
        Button clickedTab = (Button) event.getSource();
        
        // 移除所有标签的选中样式
        studentManagementTab.getStyleClass().remove("selected");
        counselorManagementTab.getStyleClass().remove("selected");
        consultationManagementTab.getStyleClass().remove("selected");
        
        // 添加选中样式到点击的标签
        clickedTab.getStyleClass().add("selected");
        
        // 切换显示的面板
        if (clickedTab == studentManagementTab) {
            showPanel("student");
            currentTab = "student";
        } else if (clickedTab == counselorManagementTab) {
            showPanel("counselor");
            currentTab = "counselor";
        } else if (clickedTab == consultationManagementTab) {
            showPanel("consultation");
            currentTab = "consultation";
        }
        
        updateButtonStates();
    }
    
    private void showPanel(String panelName) {
        // 隐藏所有面板
        studentManagementPanel.setVisible(false);
        counselorManagementPanel.setVisible(false);
        consultationManagementPanel.setVisible(false);
        
        // 显示指定面板
        switch (panelName) {
            case "student":
                studentManagementPanel.setVisible(true);
                break;
            case "counselor":
                counselorManagementPanel.setVisible(true);
                break;
            case "consultation":
                consultationManagementPanel.setVisible(true);
                break;
        }
    }
    
    private void updateButtonStates() {
        // 根据表格选择状态更新按钮可用性
        boolean studentSelected = studentTable.getSelectionModel().getSelectedItem() != null;
        boolean counselorSelected = counselorTable.getSelectionModel().getSelectedItem() != null;
        boolean consultationSelected = consultationTable.getSelectionModel().getSelectedItem() != null;
        
        if ("student".equals(currentTab)) {
            editStudentButton.setDisable(!studentSelected);
            deleteStudentButton.setDisable(!studentSelected);
        } else if ("counselor".equals(currentTab)) {
            editCounselorButton.setDisable(!counselorSelected);
            deleteCounselorButton.setDisable(!counselorSelected);
        } else if ("consultation".equals(currentTab)) {
            deleteConsultationButton.setDisable(!consultationSelected);
            toggleHighlightButton.setDisable(!consultationSelected);
        }
    }
    
    @SuppressWarnings("unchecked")
    private void initializeStudentTable() {
        // 创建表格列
        TableColumn<Student, String> studentIdCol = new TableColumn<>("学生学号");
        studentIdCol.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        studentIdCol.setMinWidth(120);
        
        TableColumn<Student, String> nameCol = new TableColumn<>("姓名");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setMinWidth(80);
        
        TableColumn<Student, String> genderCol = new TableColumn<>("性别");
        genderCol.setCellValueFactory(new PropertyValueFactory<>("gender"));
        genderCol.setMinWidth(60);
        
        TableColumn<Student, String> birthDateCol = new TableColumn<>("出生日期");
        birthDateCol.setCellValueFactory(cellData -> {
            LocalDate birthDate = cellData.getValue().getBirthDate();
            if (birthDate != null) {
                return new javafx.beans.property.SimpleStringProperty(
                    birthDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                );
            }
            return new javafx.beans.property.SimpleStringProperty("");
        });
        birthDateCol.setMinWidth(100);
        
        TableColumn<Student, String> phoneCol = new TableColumn<>("手机号码");
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        phoneCol.setMinWidth(120);
        
        TableColumn<Student, String> majorCol = new TableColumn<>("专业名称");
        majorCol.setCellValueFactory(new PropertyValueFactory<>("majorName"));
        majorCol.setMinWidth(150);
        
        TableColumn<Student, String> gradeCol = new TableColumn<>("年级");
        gradeCol.setCellValueFactory(new PropertyValueFactory<>("gradeNumber"));
        gradeCol.setMinWidth(80);
        
        TableColumn<Student, String> classCol = new TableColumn<>("班级");
        classCol.setCellValueFactory(new PropertyValueFactory<>("classNumber"));
        classCol.setMinWidth(80);
        
        TableColumn<Student, String> counselorCol = new TableColumn<>("辅导员");
        counselorCol.setCellValueFactory(new PropertyValueFactory<>("counselorName"));
        counselorCol.setMinWidth(100);
        
        // 添加列到表格
        studentTable.getColumns().addAll(
            studentIdCol, nameCol, genderCol, birthDateCol, phoneCol,
            majorCol, gradeCol, classCol, counselorCol
        );
        
        // 设置数据
        studentTable.setItems(studentData);
        
        // 添加测试数据
        addTestStudentData();
        
        // 添加选择监听器
        studentTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            updateButtonStates();
        });
    }
    
    private void addTestStudentData() {
        // 添加一些测试数据
        studentData.add(new Student("2021001", "张三", "男", 
            LocalDate.of(2003, 5, 15), "13800138001", 
            "计算机科学与技术", "2021", "01", "李老师"));
        studentData.add(new Student("2021002", "李四", "女", 
            LocalDate.of(2003, 8, 20), "13800138002", 
            "软件工程", "2021", "01", "王老师"));
        studentData.add(new Student("2021003", "王五", "男", 
            LocalDate.of(2003, 12, 10), "13800138003", 
            "计算机科学与技术", "2021", "02", "李老师"));
    }
    
    @SuppressWarnings("unchecked")
    private void initializeCounselorTable() {
        // 创建表格列
        TableColumn<Counselor, String> counselorIdCol = new TableColumn<>("辅导员工号");
        counselorIdCol.setCellValueFactory(new PropertyValueFactory<>("counselorId"));
        counselorIdCol.setMinWidth(120);
        
        TableColumn<Counselor, String> nameCol = new TableColumn<>("姓名");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setMinWidth(80);
        
        TableColumn<Counselor, String> genderCol = new TableColumn<>("性别");
        genderCol.setCellValueFactory(new PropertyValueFactory<>("gender"));
        genderCol.setMinWidth(60);
        
        TableColumn<Counselor, String> birthDateCol = new TableColumn<>("出生日期");
        birthDateCol.setCellValueFactory(cellData -> {
            LocalDate birthDate = cellData.getValue().getBirthDate();
            if (birthDate != null) {
                return new javafx.beans.property.SimpleStringProperty(
                    birthDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                );
            }
            return new javafx.beans.property.SimpleStringProperty("");
        });
        birthDateCol.setMinWidth(100);
        
        TableColumn<Counselor, String> phoneCol = new TableColumn<>("手机号码");
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        phoneCol.setMinWidth(120);
        
        TableColumn<Counselor, String> departmentCol = new TableColumn<>("院系名称");
        departmentCol.setCellValueFactory(new PropertyValueFactory<>("departmentName"));
        departmentCol.setMinWidth(150);
        
        // 添加列到表格
        counselorTable.getColumns().addAll(
            counselorIdCol, nameCol, genderCol, birthDateCol, phoneCol, departmentCol
        );
        
        // 设置数据
        counselorTable.setItems(counselorData);
        
        // 添加测试数据
        addTestCounselorData();
        
        // 添加选择监听器
        counselorTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            updateButtonStates();
        });
        
        // 设置表格选中行样式
        counselorTable.setRowFactory(tv -> {
            TableRow<Counselor> row = new TableRow<>();
            row.itemProperty().addListener((obs, oldItem, newItem) -> {
                if (newItem != null) {
                    row.setOnMouseClicked(event -> {
                        if (!row.isEmpty()) {
                            counselorTable.getSelectionModel().select(row.getIndex());
                        }
                    });
                }
            });
            return row;
        });
    }
    
    private void addTestCounselorData() {
        counselorData.add(new Counselor("T001", "张老师", "女", LocalDate.of(1985, 3, 15), 
                "13812345678", "计算机科学与工程学院", "123456", 1));
        counselorData.add(new Counselor("T002", "李老师", "男", LocalDate.of(1980, 7, 22), 
                "13998765432", "电子信息工程学院", "123456", 2));
        counselorData.add(new Counselor("T003", "王老师", "女", LocalDate.of(1988, 11, 8), 
                "13765432109", "机械工程学院", "123456", 3));
    }
    
    @SuppressWarnings("unchecked")
    private void initializeConsultationTable() {
        // 创建表格列
        TableColumn<Consultation, String> consultationIdCol = new TableColumn<>("咨询编号");
        consultationIdCol.setCellValueFactory(new PropertyValueFactory<>("consultationId"));
        consultationIdCol.setMinWidth(100);
        
        TableColumn<Consultation, String> studentIdCol = new TableColumn<>("学生学号");
        studentIdCol.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        studentIdCol.setMinWidth(100);
        
        TableColumn<Consultation, String> studentNameCol = new TableColumn<>("学生姓名");
        studentNameCol.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        studentNameCol.setMinWidth(80);
        
        TableColumn<Consultation, String> categoryCol = new TableColumn<>("类别");
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        categoryCol.setMinWidth(80);
        
        TableColumn<Consultation, String> statusCol = new TableColumn<>("状态");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setMinWidth(100);
        
        TableColumn<Consultation, String> questionTimeCol = new TableColumn<>("提问时间");
        questionTimeCol.setCellValueFactory(cellData -> {
            LocalDateTime questionTime = cellData.getValue().getQuestionTime();
            if (questionTime != null) {
                return new javafx.beans.property.SimpleStringProperty(
                    questionTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                );
            }
            return new javafx.beans.property.SimpleStringProperty("");
        });
        questionTimeCol.setMinWidth(150);
        
        TableColumn<Consultation, Integer> replyCountCol = new TableColumn<>("回复次数");
        replyCountCol.setCellValueFactory(new PropertyValueFactory<>("replyCount"));
        replyCountCol.setMinWidth(80);
        
        TableColumn<Consultation, Integer> followUpCountCol = new TableColumn<>("追问次数");
        followUpCountCol.setCellValueFactory(new PropertyValueFactory<>("followUpCount"));
        followUpCountCol.setMinWidth(80);
        
        // 是否加精列（复选框）
        TableColumn<Consultation, Boolean> highlightCol = new TableColumn<>("是否加精");
        highlightCol.setCellValueFactory(new PropertyValueFactory<>("isHighlighted"));
        highlightCol.setCellFactory(column -> new TableCell<Consultation, Boolean>() {
            private final CheckBox checkBox = new CheckBox();
            
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    Consultation consultation = getTableRow().getItem();
                    checkBox.setSelected(consultation.getIsHighlighted());
                    checkBox.setOnAction(e -> {
                        consultation.setIsHighlighted(checkBox.isSelected());
                        showInfo("操作成功", "咨询加精状态已" + (checkBox.isSelected() ? "启用" : "取消"));
                    });
                    setGraphic(checkBox);
                }
            }
        });
        highlightCol.setMinWidth(80);
        
        // 添加列到表格
        consultationTable.getColumns().addAll(
            consultationIdCol, studentIdCol, studentNameCol, categoryCol, statusCol,
            questionTimeCol, replyCountCol, followUpCountCol, highlightCol
        );
        
        // 设置数据
        consultationTable.setItems(consultationData);
        
        // 添加测试数据
        addTestConsultationData();
        
        // 添加选择监听器
        consultationTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            updateButtonStates();
        });
        
        // 设置表格选中行样式
        consultationTable.setRowFactory(tv -> {
            TableRow<Consultation> row = new TableRow<>();
            row.itemProperty().addListener((obs, oldItem, newItem) -> {
                if (newItem != null) {
                    row.setOnMouseClicked(event -> {
                        if (!row.isEmpty()) {
                            consultationTable.getSelectionModel().select(row.getIndex());
                        }
                    });
                }
            });
            return row;
        });
    }
    
    private void addTestConsultationData() {
        consultationData.add(new Consultation("R001", "2021001", "张三", "生活", "未回复", 
                LocalDateTime.of(2024, 12, 7, 14, 30), 0, 0, false, 
                "宿舍热水器出现故障，请问如何报修？"));
        consultationData.add(new Consultation("R002", "2021002", "李四", "教学", "已解决", 
                LocalDateTime.of(2024, 12, 6, 10, 15), 2, 1, true, 
                "关于数据库课程设计的具体要求和评分标准"));
        consultationData.add(new Consultation("R003", "2021003", "王五", "其他", "仍需解决", 
                LocalDateTime.of(2024, 12, 5, 16, 45), 1, 2, false, 
                "学生证丢失后如何补办，需要哪些材料？"));
        consultationData.add(new Consultation("R004", "2021001", "张三", "教学", "已解决", 
                LocalDateTime.of(2024, 12, 4, 9, 20), 1, 0, true, 
                "期末考试时间安排和考试范围咨询"));
        consultationData.add(new Consultation("R005", "2021002", "李四", "生活", "未回复", 
                LocalDateTime.of(2024, 12, 8, 11, 30), 0, 0, false, 
                "食堂饭卡充值遇到问题，无法正常使用"));
    }
    
    @FXML
    private void handleLogout() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("确认退出");
        alert.setHeaderText(null);
        alert.setContentText("确定要退出登录吗？");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // TODO: 返回登录界面
                Stage stage = (Stage) logoutButton.getScene().getWindow();
                stage.close();
                // 这里需要重新打开登录窗口
            }
        });
    }
    
    // 学生管理事件处理
    @FXML
    private void handleAddStudent() {
        try {
            openStudentForm(null);
        } catch (IOException e) {
            showError("打开添加学生窗口失败：" + e.getMessage());
        }
    }
    
    @FXML
    private void handleEditStudent() {
        Student selectedStudent = studentTable.getSelectionModel().getSelectedItem();
        if (selectedStudent != null) {
            try {
                openStudentForm(selectedStudent);
            } catch (IOException e) {
                showError("打开编辑学生窗口失败：" + e.getMessage());
            }
        } else {
            showInfo("提示", "请先选择要编辑的学生");
        }
    }
    
    @FXML
    private void handleDeleteStudent() {
        Student selectedStudent = studentTable.getSelectionModel().getSelectedItem();
        if (selectedStudent != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("确认删除");
            alert.setHeaderText("删除学生");
            alert.setContentText("确定要删除学生 \"" + selectedStudent.getName() + "\" 吗？\n此操作将同时删除该学生的所有咨询记录。");
            
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    // 从表格中删除学生
                    studentData.remove(selectedStudent);
                    showInfo("删除成功", "学生 \"" + selectedStudent.getName() + "\" 已被删除");
                }
            });
        } else {
            showInfo("提示", "请先选择要删除的学生");
        }
    }
    
    private void openStudentForm(Student student) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/student_form.fxml"));
        Parent root = loader.load();
        
        StudentFormController controller = loader.getController();
        
        // 设置回调
        controller.setCallback(new StudentFormController.StudentFormCallback() {
            @Override
            public void onStudentSaved(Student savedStudent, boolean isEdit) {
                if (isEdit) {
                    // 编辑模式：更新现有学生信息
                    int index = studentData.indexOf(student);
                    if (index >= 0) {
                        studentData.set(index, savedStudent);
                    }
                    showInfo("编辑成功", "学生信息已更新");
                } else {
                    // 添加模式：添加新学生
                    studentData.add(savedStudent);
                    showInfo("添加成功", "学生 \"" + savedStudent.getName() + "\" 已添加");
                }
                // 刷新表格选择状态
                updateButtonStates();
            }
            
            @Override
            public void onFormCancelled() {
                // 表单被取消，不需要特殊处理
            }
        });
        
        // 如果是编辑模式，设置学生信息
        if (student != null) {
            controller.setEditMode(student);
        }
        
        // 创建并显示对话框
        Stage dialogStage = new Stage();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/ui/student_form.css").toExternalForm());
        
        dialogStage.setTitle(student == null ? "添加学生" : "编辑学生");
        dialogStage.setScene(scene);
        dialogStage.setResizable(false);
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(addStudentButton.getScene().getWindow());
        
        dialogStage.showAndWait();
    }
    
    // 辅导员管理事件处理
    @FXML
    private void handleAddCounselor() {
        try {
            openCounselorForm(null);
        } catch (IOException e) {
            showError("打开添加辅导员窗口失败：" + e.getMessage());
        }
    }
    
    @FXML
    private void handleEditCounselor() {
        Counselor selectedCounselor = counselorTable.getSelectionModel().getSelectedItem();
        if (selectedCounselor != null) {
            try {
                openCounselorForm(selectedCounselor);
            } catch (IOException e) {
                showError("打开编辑辅导员窗口失败：" + e.getMessage());
            }
        } else {
            showInfo("提示", "请先选择要编辑的辅导员");
        }
    }
    
    @FXML
    private void handleDeleteCounselor() {
        Counselor selectedCounselor = counselorTable.getSelectionModel().getSelectedItem();
        if (selectedCounselor != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("确认删除");
            alert.setHeaderText("删除辅导员");
            alert.setContentText("确定要删除辅导员 \"" + selectedCounselor.getName() + "\" 吗？\n此操作将同时删除该辅导员管理的班级信息。");
            
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    // 从表格中删除辅导员
                    counselorData.remove(selectedCounselor);
                    showInfo("删除成功", "辅导员 \"" + selectedCounselor.getName() + "\" 已被删除");
                }
            });
        } else {
            showInfo("提示", "请先选择要删除的辅导员");
        }
    }
    
    private void openCounselorForm(Counselor counselor) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/counselor_form.fxml"));
        Parent root = loader.load();
        
        CounselorFormController controller = loader.getController();
        
        // 设置回调
        controller.setCallback(new CounselorFormController.CounselorFormCallback() {
            @Override
            public void onCounselorSaved(Counselor savedCounselor, boolean isEdit) {
                if (isEdit) {
                    // 编辑模式：更新现有辅导员信息
                    int index = counselorData.indexOf(counselor);
                    if (index >= 0) {
                        counselorData.set(index, savedCounselor);
                    }
                    showInfo("编辑成功", "辅导员信息已更新");
                } else {
                    // 添加模式：添加新辅导员
                    counselorData.add(savedCounselor);
                    showInfo("添加成功", "辅导员 \"" + savedCounselor.getName() + "\" 已添加");
                }
                // 刷新表格选择状态
                updateButtonStates();
            }
            
            @Override
            public void onFormCancelled() {
                // 表单被取消，不需要特殊处理
            }
        });
        
        // 如果是编辑模式，设置辅导员信息
        if (counselor != null) {
            controller.setEditMode(counselor);
        }
        
        // 创建并显示对话框
        Stage dialogStage = new Stage();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/ui/counselor_form.css").toExternalForm());
        
        dialogStage.setTitle(counselor == null ? "添加辅导员" : "编辑辅导员");
        dialogStage.setScene(scene);
        dialogStage.setResizable(false);
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(addCounselorButton.getScene().getWindow());
        
        dialogStage.showAndWait();
    }
    
    // 咨询管理事件处理
    @FXML
    private void handleDeleteConsultation() {
        Consultation selectedConsultation = consultationTable.getSelectionModel().getSelectedItem();
        if (selectedConsultation != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("确认删除");
            alert.setHeaderText("删除咨询");
            alert.setContentText("确定要删除咨询 \"" + selectedConsultation.getConsultationId() + "\" 吗？\n" +
                    "此操作将同时删除相关的回复、追问和收藏记录。");
            
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    // 从表格中删除咨询
                    consultationData.remove(selectedConsultation);
                    showInfo("删除成功", "咨询 \"" + selectedConsultation.getConsultationId() + "\" 已被删除");
                }
            });
        } else {
            showInfo("提示", "请先选择要删除的咨询");
        }
    }
    
    @FXML
    private void handleToggleHighlight() {
        Consultation selectedConsultation = consultationTable.getSelectionModel().getSelectedItem();
        if (selectedConsultation != null) {
            boolean newHighlightState = !selectedConsultation.getIsHighlighted();
            selectedConsultation.setIsHighlighted(newHighlightState);
            
            // 刷新表格显示
            consultationTable.refresh();
            
            showInfo("操作成功", "咨询 \"" + selectedConsultation.getConsultationId() + "\" 已" + 
                    (newHighlightState ? "标记为加精" : "取消加精"));
        } else {
            showInfo("提示", "请先选择要标记的咨询");
        }
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
    
    private void showConfirmationDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                showInfo("操作结果", "删除操作将在数据库连接实现后完成");
            }
        });
    }
} 