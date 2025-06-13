package ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.geometry.Insets;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import javafx.geometry.Pos;
import java.util.List;
import java.util.stream.Collectors;
import javafx.stage.Modality;
import javafx.scene.control.Alert;
import javafx.collections.FXCollections;
import java.time.format.DateTimeFormatter;
import dao.ConsultationDaoImpl;
import entity.Consultation;
import dao.CollectDaoImpl;

public class StudentMainController {
    private enum FilterType {
        ALL, FEATURED, COLLECTED, LEARNING, LIFE, OTHER
    }

    private FilterType currentFilter = FilterType.ALL;

    private ObservableList<Consultation> allConsultations = FXCollections.observableArrayList();
    private ConsultationDaoImpl consultationDao = new ConsultationDaoImpl();
    private String currentStudentId = Main.getCurrentStudentId();
    private CollectDaoImpl collectDao = new CollectDaoImpl();

    @FXML
    private HBox navButtons;
    @FXML
    private TextField searchField;
    @FXML
    private VBox leftNavButtons;
    @FXML
    private Button newConsultationButton;
    @FXML
    private HBox categoryBar;
    @FXML
    private VBox cardsContainer;
    @FXML
    private HBox topNavigationBar;
    @FXML
    private Button hallButton;
    @FXML
    private Button myConsultationsButton;
    @FXML
    private Button allCategoriesButton;
    @FXML
    private Button studyButton;
    @FXML
    private Button lifeButton;
    @FXML
    private Button otherButton;
    @FXML
    private Button featuredButton;
    @FXML
    private Button collectedButton;
    @FXML
    private Button allButton;
    @FXML
    private Button logoutButton;

    private boolean isMyMode = false;

    public StudentMainController() {
        System.out.println("StudentMainController 构造方法被调用");
    }

    @FXML
    public void initialize() {
        System.out.println("StudentMainController.initialize() 被调用");
        System.out.println("当前登录学生ID: " + currentStudentId);
        if (myConsultationsButton == null)
            throw new RuntimeException("myConsultationsButton 注入失败！");
        isMyMode = false;
        hallButton.getStyleClass().add("selected");
        myConsultationsButton.getStyleClass().remove("selected");
        myConsultationsButton.setOnAction(event -> {
            isMyMode = true;
            clearTopNavSelected();
            myConsultationsButton.getStyleClass().add("selected");
            switchToMyMode();
            highlightMyDefault();
            refreshConsultations();
        });
        hallButton.setOnAction(event -> {
            isMyMode = false;
            clearTopNavSelected();
            hallButton.getStyleClass().add("selected");
            switchToHallMode();
            highlightHallDefault();
            refreshConsultations();
        });
        setupLeftNavButtons();
        for (Node node : categoryBar.getChildren()) {
            if (node instanceof Button) {
                Button categoryButton = (Button) node;
                categoryButton.setOnAction(event -> {
                    for (Node otherNode : categoryBar.getChildren()) {
                        if (otherNode instanceof Button) {
                            otherNode.getStyleClass().remove("selected");
                        }
                    }
                    categoryButton.getStyleClass().add("selected");
                    String buttonText = categoryButton.getText();
                    switch (buttonText) {
                        case "全部":
                            currentFilter = FilterType.ALL;
                            break;
                        case "学习":
                            currentFilter = FilterType.LEARNING;
                            break;
                        case "生活":
                            currentFilter = FilterType.LIFE;
                            break;
                        case "其他":
                            currentFilter = FilterType.OTHER;
                            break;
                    }
                    refreshConsultations();
                });
            }
        }
        newConsultationButton.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/new_consultation.fxml"));
                Parent newConsultationRoot = loader.load();
                Stage newConsultationStage = new Stage();
                newConsultationStage.setTitle("发起新咨询");
                newConsultationStage.setScene(new Scene(newConsultationRoot));
                newConsultationStage.initModality(Modality.APPLICATION_MODAL);
                newConsultationStage.initOwner(((Node) event.getSource()).getScene().getWindow());
                newConsultationStage.showAndWait();
                refreshConsultations();
            } catch (IOException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("错误");
                alert.setHeaderText(null);
                alert.setContentText("无法加载新咨询窗口。");
                alert.showAndWait();
            }
        });
        searchField.textProperty().addListener((observable, oldValue, newValue) -> refreshConsultations());
        
        // 设置退出登录按钮事件
        logoutButton.setOnAction(event -> handleLogout());
        
        if (allCategoriesButton != null)
            allCategoriesButton.getStyleClass().add("selected");
        updateConsultationCount();
        refreshConsultations();
    }

    private void setupLeftNavButtons() {
        for (Node node : leftNavButtons.getChildren()) {
            if (node instanceof Button) {
                Button button = (Button) node;
                button.setOnAction(event -> {
                    clearLeftNavSelected();
                    button.getStyleClass().add("selected");
                    String buttonText = button.getText();
                    if ("全部".equals(buttonText) || "已解决".equals(buttonText))
                        currentFilter = FilterType.ALL;
                    else if ("精选".equals(buttonText) || "仍需解决".equals(buttonText))
                        currentFilter = FilterType.FEATURED;
                    else if ("收藏".equals(buttonText) || "未回复".equals(buttonText))
                        currentFilter = FilterType.COLLECTED;
                    refreshConsultations();
                });
            }
        }
    }

    private void refreshConsultations() {
        if (isMyMode) {
            loadMyConsultations();
        } else {
            loadAllConsultations();
        }
    }

    private void initializeConsultations() {
        try {
            List<Consultation> list = consultationDao.getAllConsultations();
            // 查询每条咨询是否被当前学生收藏（每次都查数据库）
            for (Consultation c : list) {
                c.setCollected(collectDao.isCollected(c.getQNumber(), currentStudentId));
            }
            allConsultations.setAll(list);
        } catch (Exception e) {
            e.printStackTrace();
            allConsultations.clear();
        }
    }

    private void loadConsultationCards() {
        cardsContainer.getChildren().clear();
        String searchText = searchField.getText().toLowerCase();
        List<Consultation> filteredConsultations = allConsultations.stream()
                .filter(c -> c.getStatus().equals("已解决") || c.getStatus().equals("仍需解决") || c.getStatus().equals("未回复"))
                .filter(c -> {
                    // "我的"模式下按状态筛选
                    if ("已解决".equals(allButton.getText())) {
                        if (currentFilter == FilterType.ALL)
                            return c.getStatus().equals("已解决");
                        if (currentFilter == FilterType.FEATURED)
                            return c.getStatus().equals("仍需解决");
                        if (currentFilter == FilterType.COLLECTED)
                            return c.getStatus().equals("未回复");
                    } else {
                        // 大厅模式
                        switch (currentFilter) {
                            case ALL:
                                return true;
                            case FEATURED:
                                return c.isHighlighted();
                            case COLLECTED:
                                // 这里每次都查数据库，保证收藏状态准确
                                try {
                                    return collectDao.isCollected(c.getQNumber(), currentStudentId);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    return false;
                                }
                            case LEARNING:
                                return "学习".equals(c.getCategory());
                            case LIFE:
                                return "生活".equals(c.getCategory());
                            case OTHER:
                                return "其他".equals(c.getCategory());
                        }
                    }
                    return true;
                })
                .filter(c -> c.getQuestionTitle().toLowerCase().contains(searchText))
                .collect(Collectors.toList());
        for (Consultation consultation : filteredConsultations) {
            cardsContainer.getChildren().add(createConsultationCard(consultation));
        }
        updateConsultationCount();
    }

    private Node createConsultationCard(Consultation consultation) {
        VBox card = new VBox();
        card.getStyleClass().add("consultation-card");
        card.setPadding(new Insets(15));
        // 问题标题（加粗）
        Label questionLabel = new Label(consultation.getQuestionTitle());
        questionLabel.getStyleClass().add("card-question");
        questionLabel.setWrapText(true);
        // 时间
        String timeStr = consultation.getQuestionTime() != null
                ? consultation.getQuestionTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                : "";
        Label timeLabel = new Label(timeStr);
        timeLabel.getStyleClass().add("card-time");
        // 回复内容
        String replyContent = consultation.getQuestionContent();
        Label replyLabel = new Label(replyContent != null && !replyContent.isEmpty() ? replyContent : "暂无回复");
        replyLabel.getStyleClass().add("card-reply");
        replyLabel.setWrapText(true);
        // 状态标签
        Label statusLabel = new Label();
        statusLabel.getStyleClass().add("card-status-label");
        if ("已解决".equals(consultation.getStatus())) {
            statusLabel.setText("已解决");
            statusLabel.getStyleClass().add("status-resolved");
        } else if ("仍需解决".equals(consultation.getStatus())) {
            statusLabel.setText("仍需解决");
            statusLabel.getStyleClass().add("status-unresolved");
        } else if ("未回复".equals(consultation.getStatus())) {
            statusLabel.setText("未回复");
            statusLabel.getStyleClass().add("status-unanswered");
        }
        // 咨询类型标签（蓝底白字）
        Label categoryLabel = new Label(consultation.getCategory());
        categoryLabel.getStyleClass().add("category-tag");
        // 新增：状态标签和类别标签同一行
        HBox statusCategoryBox = new HBox(8, statusLabel, categoryLabel);
        statusCategoryBox.setAlignment(Pos.CENTER_LEFT);
        // 交互区域
        HBox interactionContainer = new HBox(15);
        interactionContainer.setAlignment(Pos.CENTER_LEFT);
        // 留言图标
        ImageView messageIcon = new ImageView(new Image(getClass().getResourceAsStream("/images/message.png")));
        messageIcon.setFitWidth(20);
        messageIcon.setFitHeight(20);
        messageIcon.getStyleClass().add("interaction-icon");
        // 收藏图标
        boolean isCollected = false;
        try {
            isCollected = collectDao.isCollected(consultation.getQNumber(), currentStudentId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ImageView collectIcon = new ImageView(new Image(getClass().getResourceAsStream(
                isCollected ? "/images/collected.png" : "/images/uncollected.png")));
        collectIcon.setFitWidth(20);
        collectIcon.setFitHeight(20);
        collectIcon.getStyleClass().add("interaction-icon");
        // 收藏点击事件
        collectIcon.setOnMouseClicked(event -> {
            event.consume();
            try {
                boolean nowCollected = collectDao.isCollected(consultation.getQNumber(), currentStudentId);
                if (nowCollected) {
                    collectDao.removeCollect(consultation.getQNumber(), currentStudentId);
                } else {
                    collectDao.addCollect(consultation.getQNumber(), currentStudentId);
                }
                refreshConsultations();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        // 精选图标
        ImageView featuredIcon = new ImageView(new Image(getClass().getResourceAsStream(
                consultation.isHighlighted() ? "/images/choosen.png" : "/images/unchoosen.png")));
        featuredIcon.setFitWidth(20);
        featuredIcon.setFitHeight(20);
        featuredIcon.getStyleClass().add("interaction-icon");
        interactionContainer.getChildren().addAll(messageIcon, collectIcon, featuredIcon);
        // 添加所有元素到卡片
        card.getChildren().addAll(questionLabel, timeLabel, replyLabel, statusCategoryBox, interactionContainer);
        // 点击事件
        card.setOnMouseClicked(event -> openConsultationDetail(consultation));
        return card;
    }

    private void openConsultationDetail(Consultation consultation) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/consultation_detail.fxml"));
            Parent detailRoot = loader.load();
            ConsultationDetailController controller = loader.getController();
            controller.setConsultation(consultation);
            controller.setOnConsultationUpdated(() -> {
                refreshConsultations();
            });
            Stage detailStage = new Stage();
            detailStage.setTitle("咨询详情");
            detailStage.setScene(new Scene(detailRoot));
            detailStage.initModality(Modality.APPLICATION_MODAL);
            detailStage.initOwner(cardsContainer.getScene().getWindow());
            detailStage.setWidth(800);
            detailStage.setHeight(700);
            detailStage.showAndWait();
            refreshConsultations();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("错误");
            alert.setHeaderText(null);
            alert.setContentText("无法加载咨询详情窗口。");
            alert.showAndWait();
        }
    }

    private void updateConsultationCount() {
        // 可选：实现更新咨询数量逻辑
    }

    private void resetToInitialState() {
        // 左侧"全部"按钮高亮
        for (Node node : leftNavButtons.getChildren()) {
            if (node instanceof Button) {
                node.getStyleClass().remove("selected");
                if ("全部".equals(((Button) node).getText())) {
                    node.getStyleClass().add("selected");
                }
            }
        }
        // 上方"全部"标签高亮
        for (Node node : categoryBar.getChildren()) {
            if (node instanceof Button) {
                node.getStyleClass().remove("selected");
                if ("全部".equals(((Button) node).getText())) {
                    node.getStyleClass().add("selected");
                }
            }
        }
        // 搜索框清空
        searchField.setText("");
        // 当前筛选器重置
        currentFilter = FilterType.ALL;
        // 刷新卡片
        loadConsultationCards();
    }

    // 新增：只显示当前学生咨询的方法
    private void loadMyConsultations() {
        try {
            List<Consultation> list = consultationDao.getAllConsultations();
            List<Consultation> myList = list.stream()
                    .filter(c -> c.getStudentId().equals(currentStudentId))
                    .collect(java.util.stream.Collectors.toList());
            allConsultations.setAll(myList);
            loadConsultationCards();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 大厅显示全部咨询
    private void loadAllConsultations() {
        try {
            List<Consultation> list = consultationDao.getAllConsultations();
            allConsultations.setAll(list);
            loadConsultationCards();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void switchToMyMode() {
        allButton.setText("已解决");
        featuredButton.setText("仍需解决");
        collectedButton.setText("未回复");
        clearLeftNavSelected();
        allButton.getStyleClass().add("selected");
        currentFilter = FilterType.ALL;
        refreshConsultations();
    }

    private void switchToHallMode() {
        allButton.setText("全部");
        featuredButton.setText("精选");
        collectedButton.setText("收藏");
        clearLeftNavSelected();
        allButton.getStyleClass().add("selected");
        currentFilter = FilterType.ALL;
        loadConsultationCards();
    }

    private void clearTopNavSelected() {
        hallButton.getStyleClass().remove("selected");
        myConsultationsButton.getStyleClass().remove("selected");
    }

    private void clearLeftNavSelected() {
        for (Node node : leftNavButtons.getChildren()) {
            if (node instanceof Button) {
                node.getStyleClass().remove("selected");
            }
        }
    }

    // 大厅界面初始化高亮
    private void highlightHallDefault() {
        clearLeftNavSelected();
        allButton.getStyleClass().add("selected");
        for (Node node : categoryBar.getChildren()) {
            if (node instanceof Button) {
                Button btn = (Button) node;
                if ("全部".equals(btn.getText())) {
                    btn.getStyleClass().add("selected");
                } else {
                    btn.getStyleClass().remove("selected");
                }
            }
        }
    }

    // 我的界面初始化高亮
    private void highlightMyDefault() {
        clearLeftNavSelected();
        allButton.getStyleClass().add("selected");
        for (Node node : categoryBar.getChildren()) {
            if (node instanceof Button) {
                Button btn = (Button) node;
                if ("全部".equals(btn.getText())) {
                    btn.getStyleClass().add("selected");
                } else {
                    btn.getStyleClass().remove("selected");
                }
            }
        }
    }

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
            System.err.println("打开登录界面失败：" + e.getMessage());
            e.printStackTrace();
        }
    }
}