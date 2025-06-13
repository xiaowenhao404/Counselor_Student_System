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

        // 新增：监听学生表格选中项变化，实时刷新按钮状态
        studentTable.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldSel, newSel) -> updateButtonStates());
        // 新增：监听辅导员表格选中项变化，实时刷新按钮状态
        counselorTable.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldSel, newSel) -> updateButtonStates());
        // 新增：监听班级表格选中项变化，实时刷新按钮状态
        classTable.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldSel, newSel) -> updateButtonStates());
        // 新增：监听咨询表格选中项变化，实时刷新按钮状态
        consultationTable.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldSel, newSel) -> updateButtonStates());
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

        // 显示选中的面板
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
        // 根据当前选中的标签更新按钮状态
        switch (currentTab) {
            case "student":
                addStudentButton.setDisable(false);
                editStudentButton.setDisable(studentTable.getSelectionModel().getSelectedItem() == null);
                deleteStudentButton.setDisable(studentTable.getSelectionModel().getSelectedItem() == null);
                break;
            case "counselor":
                addCounselorButton.setDisable(false);
                editCounselorButton.setDisable(counselorTable.getSelectionModel().getSelectedItem() == null);
                deleteCounselorButton.setDisable(counselorTable.getSelectionModel().getSelectedItem() == null);
                break;
            case "class":
                addClassButton.setDisable(false);
                editClassCounselorButton.setDisable(classTable.getSelectionModel().getSelectedItem() == null);
                deleteClassButton.setDisable(classTable.getSelectionModel().getSelectedItem() == null);
                break;
            case "consultation":
                boolean consultationSelected = consultationTable.getSelectionModel().getSelectedItem() != null;
                deleteConsultationButton.setDisable(!consultationSelected);
                toggleHighlightButton.setDisable(!consultationSelected);
                break;
        }
    }

    @SuppressWarnings("unchecked")
    private void initializeStudentTable() {
        TableColumn<StudentView, String> idColumn = new TableColumn<>("学生学号");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("studentId"));

        TableColumn<StudentView, String> nameColumn = new TableColumn<>("姓名");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("studentName"));

        TableColumn<StudentView, String> genderColumn = new TableColumn<>("性别");
        genderColumn.setCellValueFactory(new PropertyValueFactory<>("gender"));

        TableColumn<StudentView, LocalDate> birthColumn = new TableColumn<>("出生日期");
        birthColumn.setCellValueFactory(new PropertyValueFactory<>("dateOfBirth"));

        TableColumn<StudentView, String> phoneColumn = new TableColumn<>("手机号码");
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));

        TableColumn<StudentView, String> majorColumn = new TableColumn<>("专业");
        majorColumn.setCellValueFactory(new PropertyValueFactory<>("majorName"));

        TableColumn<StudentView, String> gradeColumn = new TableColumn<>("年级");
        gradeColumn.setCellValueFactory(new PropertyValueFactory<>("gradeNumber"));

        TableColumn<StudentView, String> classColumn = new TableColumn<>("班级");
        classColumn.setCellValueFactory(new PropertyValueFactory<>("className"));

        TableColumn<StudentView, String> counselorColumn = new TableColumn<>("辅导员");
        counselorColumn.setCellValueFactory(new PropertyValueFactory<>("counselorName"));

        studentTable.getColumns().setAll(idColumn, nameColumn, genderColumn, birthColumn, phoneColumn, majorColumn,
                gradeColumn, classColumn, counselorColumn);
    }

    private void loadStudentData() {
        try {
            studentData.clear();
            studentData.addAll(studentViewDao.getAllStudentViews());
            studentTable.setItems(studentData);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private void initializeCounselorTable() {
        TableColumn<Counselor, String> idColumn = new TableColumn<>("辅导员工号");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("counselorId"));

        TableColumn<Counselor, String> nameColumn = new TableColumn<>("姓名");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Counselor, String> genderColumn = new TableColumn<>("性别");
        genderColumn.setCellValueFactory(new PropertyValueFactory<>("gender"));

        TableColumn<Counselor, LocalDate> birthColumn = new TableColumn<>("出生日期");
        birthColumn.setCellValueFactory(new PropertyValueFactory<>("dateOfBirth"));

        TableColumn<Counselor, String> phoneColumn = new TableColumn<>("手机号码");
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));

        TableColumn<Counselor, String> classListColumn = new TableColumn<>("负责班级");
        classListColumn.setCellFactory(col -> new TableCell<>() {
            private final Label badge = new Label();
            {
                badge.setStyle(
                        "-fx-background-color: linear-gradient(to right, #6a11cb, #2575fc); -fx-text-fill: white; -fx-padding: 4 10 4 10; -fx-background-radius: 12; -fx-font-size: 13px;");
                badge.setText("班级列表");
                badge.setOnMouseClicked(event -> {
                    Counselor counselor = getTableView().getItems().get(getIndex());
                    showCounselorClassListDialog(counselor);
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : badge);
            }
        });

        counselorTable.getColumns().setAll(idColumn, nameColumn, genderColumn, birthColumn, phoneColumn,
                classListColumn);
    }

    private void loadCounselorData() {
        try {
            counselorData.clear();
            counselorData.addAll(counselorDao.getAllCounselorsList());
            counselorTable.setItems(counselorData);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private void initializeClassTable() {
        TableColumn<ClassView, String> majorColumn = new TableColumn<>("专业名称");
        majorColumn.setCellValueFactory(new PropertyValueFactory<>("majorName"));

        TableColumn<ClassView, String> gradeColumn = new TableColumn<>("年级编号");
        gradeColumn.setCellValueFactory(new PropertyValueFactory<>("gradeNumber"));

        TableColumn<ClassView, String> idColumn = new TableColumn<>("班级编号");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("classId"));

        TableColumn<ClassView, String> counselorColumn = new TableColumn<>("辅导员");
        counselorColumn.setCellValueFactory(new PropertyValueFactory<>("counselorName"));

        TableColumn<ClassView, Integer> studentCountColumn = new TableColumn<>("学生人数");
        studentCountColumn.setCellValueFactory(new PropertyValueFactory<>("studentCount"));

        classTable.getColumns().setAll(majorColumn, gradeColumn, idColumn, counselorColumn, studentCountColumn);
    }

    private void loadClassData() {
        try {
            classData.clear();
            classData.addAll(classViewDao.getAllClassViews());
            classTable.setItems(classData);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadConsultationData() {
        try {
            consultationData.clear();
            consultationData.addAll(consultationDao.getAllConsultations());
            consultationTable.setItems(consultationData);
        } catch (SQLException e) {
            showError("加载咨询数据失败：" + e.getMessage());
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private void initializeConsultationTable() {
        TableColumn<Consultation, String> idColumn = new TableColumn<>("咨询编号");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("qNumber"));

        TableColumn<Consultation, String> studentIdColumn = new TableColumn<>("学号");
        studentIdColumn.setCellValueFactory(new PropertyValueFactory<>("studentId"));

        TableColumn<Consultation, String> studentNameColumn = new TableColumn<>("姓名");
        studentNameColumn.setCellValueFactory(new PropertyValueFactory<>("studentName"));

        TableColumn<Consultation, String> categoryColumn = new TableColumn<>("类别");
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));

        TableColumn<Consultation, String> statusColumn = new TableColumn<>("状态");
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        TableColumn<Consultation, Integer> replyCountColumn = new TableColumn<>("回复数");
        replyCountColumn.setCellValueFactory(new PropertyValueFactory<>("replyCount"));

        TableColumn<Consultation, Integer> followupCountColumn = new TableColumn<>("追问数");
        followupCountColumn.setCellValueFactory(new PropertyValueFactory<>("followupCount"));

        TableColumn<Consultation, Boolean> highlightedColumn = new TableColumn<>("是否精选");
        highlightedColumn.setCellValueFactory(cellData -> cellData.getValue().highlightedProperty());
        highlightedColumn.setCellFactory(CheckBoxTableCell.forTableColumn(highlightedColumn));

        consultationTable.getColumns().setAll(idColumn, studentIdColumn, studentNameColumn, categoryColumn,
                statusColumn, replyCountColumn, followupCountColumn, highlightedColumn);
    }

    @FXML
    private void handleLogout() {
        try {
            // 关闭当前窗口
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.close();

            // 打开登录界面
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/login.fxml"));
            Parent root = loader.load();
            Stage loginStage = new Stage();
            loginStage.setTitle("登录");
            loginStage.setScene(new Scene(root));
            loginStage.show();
        } catch (IOException e) {
            showError("打开登录界面失败：" + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddStudent() {
        try {
            openStudentForm(null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEditStudent() {
        StudentView selectedStudent = studentTable.getSelectionModel().getSelectedItem();
        if (selectedStudent != null) {
            try {
                Student student = studentDao.getStudentById(selectedStudent.getStudentId());
                openStudentForm(student);
            } catch (SQLException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleDeleteStudent() {
        StudentView selectedStudent = studentTable.getSelectionModel().getSelectedItem();
        if (selectedStudent != null) {
            if (showConfirmationDialog("确认删除", "确定要删除学生 " + selectedStudent.getStudentName() + " 吗？")) {
                try {
                    studentDao.deleteStudent(selectedStudent.getStudentId());
                    loadStudentData();
                } catch (SQLException e) {
                    showError("删除学生失败：" + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    private void openStudentForm(Student student) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/student_form.fxml"));
        Parent root = loader.load();
        StudentFormController controller = loader.getController();
        controller.setEditMode(student);
        controller.setCallback(new StudentFormController.StudentFormCallback() {
            @Override
            public void onStudentSaved(Student savedStudent, boolean isEdit) {
                try {
                    boolean success;
                    if (isEdit) {
                        success = studentDao.updateStudent(savedStudent);
                    } else {
                        success = studentDao.addStudent(savedStudent);
                    }
                    if (!success) {
                        showError("学生信息保存失败，请检查数据是否完整或有无重复学号。");
                    }
                    loadStudentData();
                } catch (SQLException e) {
                    showError("学生信息保存异常：" + e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onFormCancelled() {
                // 关闭表单
            }
        });
        Stage stage = new Stage();
        stage.setTitle(student == null ? "添加学生" : "编辑学生");
        stage.setScene(new Scene(root));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    @FXML
    private void handleAddCounselor() {
        try {
            openCounselorForm(null);
        } catch (IOException e) {
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
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleDeleteCounselor() {
        Counselor selectedCounselor = counselorTable.getSelectionModel().getSelectedItem();
        if (selectedCounselor != null) {
            if (showConfirmationDialog("确认删除", "确定要删除辅导员 " + selectedCounselor.getName() + " 吗？")) {
                try {
                    // 删除前校验是否有班级引用
                    List<Class> classList = classDao.getClassesByCounselorId(selectedCounselor.getCounselorId());
                    if (classList != null && !classList.isEmpty()) {
                        showError("该辅导员仍负责班级，不可删除！");
                        return;
                    }
                    counselorDao.deleteCounselor(selectedCounselor.getCounselorId());
                    loadCounselorData();
                } catch (SQLException e) {
                    showError("删除辅导员失败：" + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    private void openCounselorForm(Counselor counselor) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/counselor_form.fxml"));
        Parent root = loader.load();
        CounselorFormController controller = loader.getController();
        controller.setEditMode(counselor);
        controller.setCallback(new CounselorFormController.CounselorFormCallback() {
            @Override
            public void onCounselorSaved(Counselor savedCounselor, boolean isEdit) {
                try {
                    if (isEdit) {
                        counselorDao.updateCounselor(savedCounselor);
                    } else {
                        counselorDao.addCounselor(savedCounselor);
                    }
                    loadCounselorData();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFormCancelled() {
                // 关闭表单
            }
        });
        Stage stage = new Stage();
        stage.setTitle(counselor == null ? "添加辅导员" : "编辑辅导员");
        stage.setScene(new Scene(root));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    @FXML
    private void handleDeleteConsultation() {
        Consultation selectedConsultation = consultationTable.getSelectionModel().getSelectedItem();
        if (selectedConsultation != null) {
            if (showConfirmationDialog("确认删除", "确定要删除咨询 " + selectedConsultation.getQNumber() + " 吗？")) {
                try {
                    consultationDao.deleteConsultation(selectedConsultation.getQNumber());
                    loadConsultationData();
                } catch (SQLException e) {
                    showError("删除咨询失败：" + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    @FXML
    private void handleAddClass() {
        try {
            openClassForm(null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEditClassCounselor() {
        ClassView selectedClass = classTable.getSelectionModel().getSelectedItem();
        if (selectedClass != null) {
            try {
                Class classObj = classDao.getClassByFullKey(selectedClass.getMajorId(), selectedClass.getGradeNumber(),
                        selectedClass.getClassId());
                openClassCounselorForm(classObj);
            } catch (SQLException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleDeleteClass() {
        ClassView selectedClass = classTable.getSelectionModel().getSelectedItem();
        if (selectedClass != null) {
            if (showConfirmationDialog("确认删除", "确定要删除班级 " + selectedClass.getClassName() + " 吗？")) {
                try {
                    classDao.deleteClass(selectedClass.getMajorId(), selectedClass.getGradeNumber(),
                            selectedClass.getClassId());
                    loadClassData();
                } catch (java.sql.SQLIntegrityConstraintViolationException e) {
                    showError("该班级存在学生，不可删除！");
                } catch (SQLException e) {
                    showError("删除班级失败：" + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    @FXML
    private void handleToggleHighlight() {
        Consultation selectedConsultation = consultationTable.getSelectionModel().getSelectedItem();
        if (selectedConsultation != null) {
            boolean wasHighlighted = selectedConsultation.isHighlighted();
            try {
                consultationDao.toggleHighlight(selectedConsultation.getQNumber());
                if (wasHighlighted) {
                    showInfo("操作成功", "取消加精成功");
                } else {
                    showInfo("操作成功", "标记加精成功");
                }
                loadConsultationData();
            } catch (SQLException e) {
                showError("操作失败：" + e.getMessage());
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

    private boolean showConfirmationDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    private void openClassForm(Class classObj) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/class_form.fxml"));
        Parent root = loader.load();
        ClassFormController controller = loader.getController();
        if (classObj != null) {
            controller.setEditMode(classObj);
        }
        controller.setCallback(new ClassFormController.ClassFormCallback() {
            @Override
            public void onClassSaved(entity.Class savedClass, boolean isEdit) {
                try {
                    if (isEdit) {
                        classDao.updateClass(savedClass);
                        showInfo("修改成功", "班级信息已更新！");
                    } else {
                        boolean success = classDao.addClass(savedClass);
                        if (success) {
                            showInfo("添加成功", "新班级已创建！");
                        } else {
                            showError("添加失败：该班级已存在！");
                        }
                    }
                    loadClassData();
                } catch (SQLException e) {
                    showError("操作失败：" + e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onFormCancelled() {
                // 关闭表单
            }
        });
        Stage stage = new Stage();
        stage.setTitle(classObj == null ? "添加班级" : "编辑班级");
        stage.setScene(new Scene(root));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    private void openClassCounselorForm(Class classObj) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/class_counselor_form.fxml"));
        Parent root = loader.load();
        ClassCounselorFormController controller = loader.getController();
        controller.setClassInfo(classObj);
        controller.setCallback(new ClassCounselorFormController.ClassCounselorFormCallback() {
            @Override
            public void onCounselorChanged(Class updatedClass, String newCounselorId, String newCounselorName) {
                try {
                    updatedClass.setCounselorId(newCounselorId);
                    boolean success = classDao.updateClass(updatedClass);
                    if (success) {
                        showInfo("修改成功", "班级辅导员已更新！");
                    } else {
                        showError("修改失败，数据库未更新。");
                    }
                    loadClassData();
                } catch (SQLException e) {
                    showError("修改失败：" + e.getMessage());
                    e.printStackTrace();
                    loadClassData();
                }
            }

            @Override
            public void onFormCancelled() {
                loadClassData();
            }
        });
        Stage stage = new Stage();
        stage.setTitle("编辑班级辅导员");
        stage.setScene(new Scene(root));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    private void showCounselorClassListDialog(Counselor counselor) {
        String classList = counselor.getClassList();
        String content = (classList == null || classList.trim().isEmpty()) ? "暂无" : classList;
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("负责班级列表");
        alert.setHeaderText("辅导员：" + counselor.getName());
        alert.setContentText(content);
        alert.showAndWait();
    }
}