package ui;

import dao.ClassDaoImpl;
import dao.ClassViewDaoImpl;
import dao.CounselorDaoImpl;
import dao.MajorDaoImpl;
import dao.StudentDaoImpl;
import dao.StudentViewDaoImpl;
import dao.ConsultationDaoImpl;
import entity.Student;
import entity.Counselor;
import entity.Consultation;
import entity.Class;
import entity.StudentView;
import entity.ClassView;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.List;

public class AdminMainController implements Initializable {

    // 顶部导航栏
    @FXML
    private Button logoutButton;

    // 底部标签按钮
    @FXML
    private Button studentManagementTab;
    @FXML
    private Button counselorManagementTab;
    @FXML
    private Button classManagementTab;
    @FXML
    private Button consultationManagementTab;

    // 主内容面板
    @FXML
    private VBox studentManagementPanel;
    @FXML
    private VBox counselorManagementPanel;
    @FXML
    private VBox classManagementPanel;
    @FXML
    private VBox consultationManagementPanel;

    // 学生管理相关控件
    @FXML
    private Button addStudentButton;
    @FXML
    private Button editStudentButton;
    @FXML
    private Button deleteStudentButton;
    @FXML
    private TableView<StudentView> studentTable;

    // 辅导员管理相关控件
    @FXML
    private Button addCounselorButton;
    @FXML
    private Button editCounselorButton;
    @FXML
    private Button deleteCounselorButton;
    @FXML
    private TableView<Counselor> counselorTable;

    // 班级管理相关控件
    @FXML
    private Button addClassButton;
    @FXML
    private Button editClassCounselorButton;
    @FXML
    private Button deleteClassButton;
    @FXML
    private TableView<ClassView> classTable;

    // 咨询管理相关控件
    @FXML
    private Button deleteConsultationButton;
    @FXML
    private Button toggleHighlightButton;
    @FXML
    private TableView<Consultation> consultationTable;

    private String currentTab = "student"; // 当前选中的标签

    // 学生数据列表
    private ObservableList<StudentView> studentData = FXCollections.observableArrayList();

    // 辅导员数据列表
    private ObservableList<Counselor> counselorData = FXCollections.observableArrayList();

    // 班级数据列表
    private ObservableList<ClassView> classData = FXCollections.observableArrayList();

    // 咨询数据列表
    private ObservableList<Consultation> consultationData = FXCollections.observableArrayList();

    private StudentDaoImpl studentDao;
    private StudentViewDaoImpl studentViewDao;
    private CounselorDaoImpl counselorDao;
    private MajorDaoImpl majorDao;
    private ClassDaoImpl classDao;
    private ClassViewDaoImpl classViewDao;
    private ConsultationDaoImpl consultationDao;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        studentDao = new StudentDaoImpl();
        studentViewDao = new StudentViewDaoImpl();
        counselorDao = new CounselorDaoImpl();
        majorDao = new MajorDaoImpl();
        classDao = new ClassDaoImpl();
        classViewDao = new ClassViewDaoImpl();
        consultationDao = new ConsultationDaoImpl();

        // 初始化表格列
        initializeStudentTable();
        initializeCounselorTable();
        initializeClassTable();
        initializeConsultationTable();

        // 加载数据
        loadStudentData();
        loadCounselorData();
        loadClassData();
        loadConsultationData();

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
        classManagementTab.getStyleClass().remove("selected");
        consultationManagementTab.getStyleClass().remove("selected");

        // 添加选中样式到点击的标签
        clickedTab.getStyleClass().add("selected");

        // 切换显示的面板
        if (clickedTab == studentManagementTab) {
            showPanel("student");
            currentTab = "student";
            loadStudentData();
        } else if (clickedTab == counselorManagementTab) {
            showPanel("counselor");
            currentTab = "counselor";
            loadCounselorData();
        } else if (clickedTab == classManagementTab) {
            showPanel("class");
            currentTab = "class";
            loadClassData();
        } else if (clickedTab == consultationManagementTab) {
            showPanel("consultation");
            currentTab = "consultation";
            loadConsultationData();
        }

        updateButtonStates();
    }

    private void showPanel(String panelName) {
        // 隐藏所有面板
        studentManagementPanel.setVisible(false);
        counselorManagementPanel.setVisible(false);
        classManagementPanel.setVisible(false);
        consultationManagementPanel.setVisible(false);

        // 显示指定面板
        switch (panelName) {
            case "student":
                studentManagementPanel.setVisible(true);
                break;
            case "counselor":
                counselorManagementPanel.setVisible(true);
                break;
            case "class":
                classManagementPanel.setVisible(true);
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
        boolean classSelected = classTable.getSelectionModel().getSelectedItem() != null;
        boolean consultationSelected = consultationTable.getSelectionModel().getSelectedItem() != null;

        if ("student".equals(currentTab)) {
            editStudentButton.setDisable(!studentSelected);
            deleteStudentButton.setDisable(!studentSelected);
        } else if ("counselor".equals(currentTab)) {
            editCounselorButton.setDisable(!counselorSelected);
            deleteCounselorButton.setDisable(!counselorSelected);
        } else if ("class".equals(currentTab)) {
            editClassCounselorButton.setDisable(!classSelected);
            deleteClassButton.setDisable(!classSelected);
        } else if ("consultation".equals(currentTab)) {
            deleteConsultationButton.setDisable(!consultationSelected);
            toggleHighlightButton.setDisable(!consultationSelected);
        }
    }

    @SuppressWarnings("unchecked")
    private void initializeStudentTable() {
        // 创建表格列
        TableColumn<StudentView, String> studentIdCol = new TableColumn<>("学生学号");
        studentIdCol.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        studentIdCol.setMinWidth(120);

        TableColumn<StudentView, String> nameCol = new TableColumn<>("姓名");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        nameCol.setMinWidth(80);

        TableColumn<StudentView, String> genderCol = new TableColumn<>("性别");
        genderCol.setCellValueFactory(new PropertyValueFactory<>("gender"));
        genderCol.setMinWidth(60);

        TableColumn<StudentView, String> birthDateCol = new TableColumn<>("出生日期");
        birthDateCol.setCellValueFactory(cellData -> {
            LocalDate birthDate = cellData.getValue().getDateOfBirth();
            if (birthDate != null) {
                return new javafx.beans.property.SimpleStringProperty(
                        birthDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            }
            return new javafx.beans.property.SimpleStringProperty("");
        });
        birthDateCol.setMinWidth(100);

        TableColumn<StudentView, String> phoneCol = new TableColumn<>("手机号码");
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        phoneCol.setMinWidth(120);

        TableColumn<StudentView, String> majorCol = new TableColumn<>("专业名称");
        majorCol.setCellValueFactory(new PropertyValueFactory<>("majorName"));
        majorCol.setMinWidth(150);

        TableColumn<StudentView, String> gradeNumberCol = new TableColumn<>("年级编号");
        gradeNumberCol.setCellValueFactory(new PropertyValueFactory<>("gradeNumber"));
        gradeNumberCol.setMinWidth(80);

        TableColumn<StudentView, String> classNumberCol = new TableColumn<>("班级编号");
        classNumberCol.setCellValueFactory(new PropertyValueFactory<>("className"));
        classNumberCol.setMinWidth(80);

        TableColumn<StudentView, String> counselorCol = new TableColumn<>("辅导员姓名");
        counselorCol.setCellValueFactory(new PropertyValueFactory<>("counselorName"));
        counselorCol.setMinWidth(100);

        studentTable.getColumns().setAll(
                studentIdCol, nameCol, genderCol, birthDateCol, phoneCol,
                majorCol, gradeNumberCol, classNumberCol, counselorCol);

        // 设置数据
        studentTable.setItems(studentData);

        // 添加选择监听器
        studentTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            updateButtonStates();
        });
    }

    private void loadStudentData() {
        try {
            studentData.clear();
            studentData.addAll(studentViewDao.getAllStudentViews());
        } catch (SQLException e) {
            showError("加载学生数据失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private void initializeCounselorTable() {
        // 创建辅导员表格列
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
            LocalDate birthDate = cellData.getValue().getDateOfBirth();
            if (birthDate != null) {
                return new SimpleStringProperty(birthDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            }
            return new SimpleStringProperty("");
        });
        birthDateCol.setMinWidth(100);

        TableColumn<Counselor, String> phoneCol = new TableColumn<>("手机号码");
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        phoneCol.setMinWidth(120);

        TableColumn<Counselor, String> departmentCol = new TableColumn<>("院系名称");
        departmentCol.setCellValueFactory(new PropertyValueFactory<>("departmentName"));
        departmentCol.setMinWidth(150);

        TableColumn<Counselor, Void> classListCol = new TableColumn<>("负责班级");
        classListCol.setCellFactory(col -> new TableCell<Counselor, Void>() {
            private final Button btn = new Button("查看负责班级");
            {
                // 设置现代化按钮样式
                btn.setStyle(
                    "-fx-background-color: linear-gradient(to right, #667eea, #764ba2); " +
                    "-fx-text-fill: white; " +
                    "-fx-font-size: 12px; " +
                    "-fx-font-weight: 600; " +
                    "-fx-padding: 6px 12px; " +
                    "-fx-background-radius: 15px; " +
                    "-fx-border-radius: 15px; " +
                    "-fx-cursor: hand; " +
                    "-fx-min-width: 100px; " +
                    "-fx-effect: dropshadow(gaussian, rgba(102, 126, 234, 0.3), 4, 0.2, 0, 2);"
                );
                
                // 添加悬停效果
                btn.setOnMouseEntered(e -> {
                    btn.setStyle(
                        "-fx-background-color: linear-gradient(to right, #5a6fd8, #6a4190); " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 12px; " +
                        "-fx-font-weight: 600; " +
                        "-fx-padding: 6px 12px; " +
                        "-fx-background-radius: 15px; " +
                        "-fx-border-radius: 15px; " +
                        "-fx-cursor: hand; " +
                        "-fx-min-width: 100px; " +
                        "-fx-effect: dropshadow(gaussian, rgba(102, 126, 234, 0.4), 6, 0.3, 0, 3); " +
                        "-fx-scale-x: 1.05; -fx-scale-y: 1.05;"
                    );
                });
                
                btn.setOnMouseExited(e -> {
                    btn.setStyle(
                        "-fx-background-color: linear-gradient(to right, #667eea, #764ba2); " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 12px; " +
                        "-fx-font-weight: 600; " +
                        "-fx-padding: 6px 12px; " +
                        "-fx-background-radius: 15px; " +
                        "-fx-border-radius: 15px; " +
                        "-fx-cursor: hand; " +
                        "-fx-min-width: 100px; " +
                        "-fx-effect: dropshadow(gaussian, rgba(102, 126, 234, 0.3), 4, 0.2, 0, 2); " +
                        "-fx-scale-x: 1.0; -fx-scale-y: 1.0;"
                    );
                });
                
                btn.setOnAction(event -> {
                    Counselor counselor = getTableView().getItems().get(getIndex());
                    showCounselorClassListDialog(counselor);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btn);
                }
            }
        });
        classListCol.setMinWidth(120);

        counselorTable.getColumns().setAll(
                counselorIdCol, nameCol, genderCol, birthDateCol, phoneCol, departmentCol, classListCol);

        // 设置数据
        counselorTable.setItems(counselorData);

        // 添加选择监听器
        counselorTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            updateButtonStates();
        });
    }

    private void loadCounselorData() {
        try {
            counselorData.clear();
            counselorData.addAll(counselorDao.getAllCounselorsList());
        } catch (SQLException e) {
            showError("加载辅导员数据失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private void initializeClassTable() {
        // 创建班级表格列
        TableColumn<ClassView, String> majorIdCol = new TableColumn<>("专业编号");
        majorIdCol.setCellValueFactory(new PropertyValueFactory<>("majorId"));
        majorIdCol.setMinWidth(100);

        TableColumn<ClassView, String> majorNameCol = new TableColumn<>("专业名称");
        majorNameCol.setCellValueFactory(new PropertyValueFactory<>("majorName"));
        majorNameCol.setMinWidth(150);

        TableColumn<ClassView, String> gradeNumberCol = new TableColumn<>("年级编号");
        gradeNumberCol.setCellValueFactory(new PropertyValueFactory<>("gradeNumber"));
        gradeNumberCol.setMinWidth(80);

        TableColumn<ClassView, String> classIdCol = new TableColumn<>("班级编号");
        classIdCol.setCellValueFactory(new PropertyValueFactory<>("classId"));
        classIdCol.setMinWidth(80);

        TableColumn<ClassView, String> classNameCol = new TableColumn<>("班级名称");
        classNameCol.setCellValueFactory(new PropertyValueFactory<>("className"));
        classNameCol.setMinWidth(100);

        TableColumn<ClassView, String> counselorIdCol = new TableColumn<>("辅导员工号");
        counselorIdCol.setCellValueFactory(new PropertyValueFactory<>("counselorId"));
        counselorIdCol.setMinWidth(100);

        TableColumn<ClassView, String> counselorNameCol = new TableColumn<>("辅导员姓名");
        counselorNameCol.setCellValueFactory(new PropertyValueFactory<>("counselorName"));
        counselorNameCol.setMinWidth(100);

        classTable.getColumns().setAll(
                majorIdCol, majorNameCol, gradeNumberCol, classIdCol, classNameCol, counselorIdCol, counselorNameCol);

        // 设置数据
        classTable.setItems(classData);

        // 添加选择监听器
        classTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            updateButtonStates();
        });
    }

    private void loadClassData() {
        try {
            classData.clear();
            classData.addAll(classViewDao.getAllClassViews());
        } catch (SQLException e) {
            showError("加载班级数据失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadConsultationData() {
        try {
            consultationData.clear();
            consultationData.addAll(consultationDao.getAllConsultations());
        } catch (SQLException e) {
            showError("加载咨询数据失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private void initializeConsultationTable() {
        TableColumn<Consultation, String> qNumberCol = new TableColumn<>("Q编号");
        qNumberCol.setCellValueFactory(new PropertyValueFactory<>("qNumber"));

        TableColumn<Consultation, String> studentIdCol = new TableColumn<>("学生学号");
        studentIdCol.setCellValueFactory(new PropertyValueFactory<>("studentId"));

        TableColumn<Consultation, String> studentNameCol = new TableColumn<>("学生姓名");
        studentNameCol.setCellValueFactory(new PropertyValueFactory<>("studentName"));

        TableColumn<Consultation, String> categoryCol = new TableColumn<>("类别");
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));

        TableColumn<Consultation, String> statusCol = new TableColumn<>("状态");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        TableColumn<Consultation, java.time.LocalDateTime> questionTimeCol = new TableColumn<>("提问时间");
        questionTimeCol.setCellValueFactory(new PropertyValueFactory<>("questionTime"));
        questionTimeCol.setCellFactory(column -> new TableCell<Consultation, java.time.LocalDateTime>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            @Override
            protected void updateItem(java.time.LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(formatter.format(item));
                }
            }
        });

        TableColumn<Consultation, Integer> replyCountCol = new TableColumn<>("回复次数");
        replyCountCol.setCellValueFactory(new PropertyValueFactory<>("replyCount"));

        TableColumn<Consultation, Integer> followupCountCol = new TableColumn<>("追问次数");
        followupCountCol.setCellValueFactory(new PropertyValueFactory<>("followupCount"));

        TableColumn<Consultation, Boolean> highlightCol = new TableColumn<>("是否加精");
        highlightCol.setCellValueFactory(cellData -> {
            Consultation consultation = cellData.getValue();
            return new javafx.beans.property.SimpleBooleanProperty(consultation.isHighlighted());
        });
        
        // 使用自定义的复选框单元格工厂
        highlightCol.setCellFactory(column -> {
            CheckBoxTableCell<Consultation, Boolean> checkBoxCell = new CheckBoxTableCell<Consultation, Boolean>() {
                @Override
                public void updateItem(Boolean item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!empty && getTableRow() != null && getTableRow().getItem() != null) {
                        Consultation consultation = (Consultation) getTableRow().getItem();
                        CheckBox checkBox = (CheckBox) getGraphic();
                        if (checkBox != null) {
                            // 移除之前的监听器以避免重复触发
                            checkBox.setOnAction(null);
                            // 设置复选框状态
                            checkBox.setSelected(consultation.isHighlighted());
                            // 添加点击事件监听器
                            checkBox.setOnAction(event -> {
                                boolean newValue = checkBox.isSelected();
                                try {
                                    boolean success = consultationDao.updateHighlight(consultation.getQNumber(), newValue);
                                    if (success) {
                                        consultation.setHighlighted(newValue);
                                        String status = newValue ? "已加精" : "已取消加精";
                                        showInfo("成功", "咨询" + status + "。");
                                    } else {
                                        // 如果更新失败，恢复原值
                                        consultation.setHighlighted(!newValue);
                                        checkBox.setSelected(!newValue);
                                        showError("更新咨询加精状态失败。");
                                    }
                                } catch (SQLException e) {
                                    // 如果发生异常，恢复原值
                                    consultation.setHighlighted(!newValue);
                                    checkBox.setSelected(!newValue);
                                    showError("数据库操作失败: " + e.getMessage());
                                    e.printStackTrace();
                                }
                            });
                        }
                    }
                }
            };
            return checkBoxCell;
        });
        highlightCol.setEditable(false); // 不使用编辑模式，使用点击事件

        consultationTable.getColumns().setAll(
                qNumberCol, studentIdCol, studentNameCol, categoryCol, statusCol,
                questionTimeCol, replyCountCol, followupCountCol, highlightCol);
        consultationTable.setEditable(true);
        consultationTable.setItems(consultationData);

        // 设置表格行双击事件
        consultationTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                // 双击事件，这里暂时不处理，如果需要可以添加编辑咨询的逻辑
            }
        });

        consultationTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> updateButtonStates());
    }

    @FXML
    private void handleLogout() {
        try {
            // 关闭当前窗口
            Stage currentStage = (Stage) logoutButton.getScene().getWindow();
            currentStage.close();

            // 打开登录界面
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/login.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("学生辅导系统 - 登录");
            stage.show();
        } catch (IOException e) {
            showError("退出登录失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 学生管理操作
    @FXML
    private void handleAddStudent() {
        try {
            openStudentForm(null); // 传递 null 表示新增
        } catch (IOException e) {
            showError("打开学生添加表单失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEditStudent() {
        StudentView selectedStudentView = studentTable.getSelectionModel().getSelectedItem();
        if (selectedStudentView != null) {
            try {
                // 从数据库获取完整的 Student 实体
                Student student = studentDao.getStudentById(selectedStudentView.getStudentId());
                if (student != null) {
                    openStudentForm(student);
                } else {
                    showError("未能找到学生信息进行编辑。");
                }
            } catch (IOException | SQLException e) {
                showError("打开学生编辑表单失败: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleDeleteStudent() {
        StudentView selectedStudentView = studentTable.getSelectionModel().getSelectedItem();
        if (selectedStudentView != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("确认删除");
            alert.setHeaderText("删除学生");
            alert.setContentText("您确定要删除学生 " + selectedStudentView.getStudentName() + " (学号: "
                    + selectedStudentView.getStudentId() + ") 吗？");

            if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                try {
                    boolean success = studentDao.deleteStudent(selectedStudentView.getStudentId());
                    if (success) {
                        loadStudentData();
                        showInfo("成功", "学生已删除。");
                    } else {
                        showError("删除学生失败。");
                    }
                } catch (SQLException e) {
                    showError("删除操作失败: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    // 学生管理辅助方法
    private void openStudentForm(Student student) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/student_form.fxml"));
        Parent root = loader.load();
        StudentFormController controller = loader.getController();
        controller.setCallback(new StudentFormController.StudentFormCallback() {
            @Override
            public void onStudentSaved(Student savedStudent, boolean isEdit) {
                try {
                    boolean success;
                    if (isEdit) {
                        success = studentDao.updateStudent(savedStudent);
                        if (success) {
                            loadStudentData();
                            showInfo("成功", "学生信息已更新。");
                        } else {
                            showError("更新学生信息失败。");
                        }
                    } else {
                        success = studentDao.addStudent(savedStudent);
                        if (success) {
                            loadStudentData();
                            showInfo("成功", "新学生已添加。");
                        } else {
                            showError("添加学生失败。");
                        }
                    }
                } catch (SQLException e) {
                    showError("数据库操作失败: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onFormCancelled() {
                // 表单取消，不做任何操作
            }
        });
        if (student != null) {
            controller.setEditMode(student);
        }

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(student != null ? "编辑学生信息" : "添加新学生");
        stage.setScene(new Scene(root));
        stage.showAndWait();
    }

    // 辅导员管理操作
    @FXML
    private void handleAddCounselor() {
        try {
            openCounselorForm(null); // 传递 null 表示新增
        } catch (IOException e) {
            showError("打开辅导员添加表单失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEditCounselor() {
        Counselor selectedCounselor = counselorTable.getSelectionModel().getSelectedItem();
        if (selectedCounselor != null) {
            try {
                openCounselorForm(selectedCounselor);
            } catch (IOException e) {
                showError("打开辅导员编辑表单失败: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleDeleteCounselor() {
        Counselor selectedCounselor = counselorTable.getSelectionModel().getSelectedItem();
        if (selectedCounselor != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("确认删除");
            alert.setHeaderText("删除辅导员");
            alert.setContentText("您确定要删除辅导员 " + selectedCounselor.getName() + " (工号: "
                    + selectedCounselor.getCounselorId() + ") 吗？");

            if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                try {
                    boolean success = counselorDao.deleteCounselor(selectedCounselor.getCounselorId());
                    if (success) {
                        loadCounselorData();
                        showInfo("成功", "辅导员已删除。");
                    } else {
                        showError("删除辅导员失败。");
                    }
                } catch (SQLException e) {
                    showError("删除操作失败: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    // 辅导员管理辅助方法
    private void openCounselorForm(Counselor counselor) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/counselor_form.fxml"));
        Parent root = loader.load();
        CounselorFormController controller = loader.getController();
        controller.setCallback(new CounselorFormController.CounselorFormCallback() {
            @Override
            public void onCounselorSaved(Counselor savedCounselor, boolean isEdit) {
                try {
                    boolean success;
                    if (isEdit) {
                        success = counselorDao.updateCounselor(savedCounselor);
                        if (success) {
                            loadCounselorData();
                            showInfo("成功", "辅导员信息已更新。");
                        } else {
                            showError("更新辅导员信息失败。");
                        }
                    } else {
                        success = counselorDao.addCounselor(savedCounselor);
                        if (success) {
                            loadCounselorData();
                            showInfo("成功", "新辅导员已添加。");
                        } else {
                            showError("添加辅导员失败。");
                        }
                    }
                } catch (SQLException e) {
                    showError("数据库操作失败: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onFormCancelled() {
                // 表单取消，不做任何操作
            }
        });
        if (counselor != null) {
            controller.setEditMode(counselor);
        }

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(counselor != null ? "编辑辅导员信息" : "添加新辅导员");
        stage.setScene(new Scene(root));
        stage.showAndWait();
    }

    // 咨询管理操作
    @FXML
    private void handleDeleteConsultation() {
        Consultation selectedConsultation = consultationTable.getSelectionModel().getSelectedItem();
        if (selectedConsultation != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("确认删除");
            alert.setHeaderText("删除咨询");
            alert.setContentText("您确定要删除咨询 " + selectedConsultation.getQNumber() + " (学生: "
                    + selectedConsultation.getStudentId() + ") 吗？");

            if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                try {
                    boolean success = consultationDao.deleteConsultation(selectedConsultation.getQNumber());
                    if (success) {
                        loadConsultationData();
                        showInfo("成功", "咨询已删除。");
                    } else {
                        showError("删除咨询失败。");
                    }
                } catch (SQLException e) {
                    showError("删除操作失败: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    @FXML
    private void handleAddClass() {
        try {
            openClassForm(null); // 传递 null 表示新增
        } catch (IOException e) {
            showError("打开班级添加表单失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEditClassCounselor() {
        ClassView selectedClassView = classTable.getSelectionModel().getSelectedItem();
        if (selectedClassView != null) {
            try {
                // 使用完整的联合主键获取Class对象
                Class classObj = classDao.getClassByFullKey(
                        selectedClassView.getMajorId(),
                        selectedClassView.getGradeNumber(),
                        selectedClassView.getClassId());
                if (classObj != null) {
                    openClassCounselorForm(classObj);
                } else {
                    showError("未找到班级信息。");
                }
            } catch (IOException | SQLException e) {
                showError("加载班级辅导员编辑界面失败: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            showError("请选择一个班级进行编辑。");
        }
    }

    @FXML
    private void handleDeleteClass() {
        ClassView selectedClassView = classTable.getSelectionModel().getSelectedItem();
        if (selectedClassView != null) {
            // TODO: 在这里添加检查学生人数的逻辑
            // if (selectedClassView.getStudentCount() > 0) {
            // showError("班级中仍有学生，无法删除！");
            // return;
            // }

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("确认删除");
            alert.setHeaderText("删除班级");
            alert.setContentText(
                    "您确定要删除班级 " + selectedClassView.getMajorName() + selectedClassView.getGradeNumber() + "级"
                            + selectedClassView.getClassId() + "班 吗？");

            if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                try {
                    boolean success = classDao.deleteClass(
                        selectedClassView.getMajorId(),
                        selectedClassView.getGradeNumber(),
                        selectedClassView.getClassId()
                    );
                    if (success) {
                        loadClassData();
                        showInfo("成功", "班级已删除。");
                    } else {
                        showError("删除班级失败。");
                    }
                } catch (SQLException e) {
                    showError("删除操作失败: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    @FXML
    private void handleToggleHighlight() {
        Consultation selectedConsultation = consultationTable.getSelectionModel().getSelectedItem();
        if (selectedConsultation != null) {
            try {
                boolean success = consultationDao.toggleHighlight(selectedConsultation.getQNumber());
                if (success) {
                    // 更新选中项的加精状态
                    selectedConsultation.setHighlighted(!selectedConsultation.isHighlighted());
                    
                    // 刷新表格显示
                    consultationTable.refresh();
                    
                    String status = selectedConsultation.isHighlighted() ? "已加精" : "已取消加精";
                    showInfo("成功", "咨询" + status + "。");
                } else {
                    showError("切换咨询加精状态失败。");
                }
            } catch (SQLException e) {
                showError("数据库操作失败: " + e.getMessage());
                e.printStackTrace();
            }
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
        alert.showAndWait();
    }

    // 班级管理辅助方法
    private void openClassForm(Class classObj) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/class_form.fxml"));
        Parent root = loader.load();
        ClassFormController controller = loader.getController();
        controller.setCallback(new ClassFormController.ClassFormCallback() {
            @Override
            public void onClassSaved(entity.Class savedClass, boolean isEdit) {
                try {
                    boolean success;
                    if (isEdit) {
                        success = classDao.updateClass(savedClass);
                        if (success) {
                            loadClassData();
                            showInfo("成功", "班级信息已更新。");
                        } else {
                            showError("更新班级信息失败。");
                        }
                    } else {
                        success = classDao.addClass(savedClass);
                        if (success) {
                            loadClassData();
                            showInfo("成功", "新班级已添加。");
                        } else {
                            showError("添加班级失败。");
                        }
                    }
                } catch (SQLException e) {
                    showError("数据库操作失败: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onFormCancelled() {
                // 表单取消，不做任何操作
            }
        });
        if (classObj != null) {
            controller.setEditMode(classObj);
        }

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(classObj != null ? "编辑班级信息" : "添加新班级");
        stage.setScene(new Scene(root));
        stage.showAndWait();
    }

    private void openClassCounselorForm(Class classObj) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/class_counselor_form.fxml"));
        Parent root = loader.load();
        ClassCounselorFormController controller = loader.getController();
        controller.setCallback(new ClassCounselorFormController.ClassCounselorFormCallback() {
            @Override
            public void onCounselorChanged(Class updatedClass, String newCounselorId, String newCounselorName) {
                try {
                    boolean success = classDao.updateClassCounselor(
                        updatedClass.getMajorId(), 
                        updatedClass.getGradeNumber(), 
                        updatedClass.getClassId(), 
                        newCounselorId
                    );
                    if (success) {
                        loadClassData();
                        showInfo("成功", "班级辅导员已更新。");
                    } else {
                        showError("更新班级辅导员失败。");
                    }
                } catch (SQLException e) {
                    showError("数据库操作失败: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onFormCancelled() {
                // 表单取消，不做任何操作
            }
        });
        if (classObj != null) {
            controller.setClassInfo(classObj);
        }

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("修改班级辅导员");
        stage.setScene(new Scene(root));
        stage.showAndWait();
    }

    private void showCounselorClassListDialog(Counselor counselor) {
        try {
            // 从数据库直接查询该辅导员负责的班级，包含年级信息
            List<ClassView> counselorClasses = classViewDao.getClassViewsByCounselorId(counselor.getCounselorId());
            String message;
            if (counselorClasses == null || counselorClasses.isEmpty()) {
                message = "该辅导员当前未分配任何班级。";
            } else {
                StringBuilder sb = new StringBuilder();
                for (ClassView classView : counselorClasses) {
                    sb.append(classView.getMajorName())
                      .append(" ")
                      .append(classView.getGradeNumber())
                      .append("级 ")
                      .append(classView.getClassId())
                      .append("班\n");
                }
                message = sb.toString();
            }
            showInfo("辅导员负责班级", message);
        } catch (SQLException e) {
            showError("获取班级信息失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}