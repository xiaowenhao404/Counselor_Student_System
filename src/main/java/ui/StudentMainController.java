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

    public StudentMainController() {
        System.out.println("StudentMainController 构造方法被调用");
    }

    @FXML
    public void initialize() {
        System.out.println("StudentMainController.initialize() 被调用");
        System.out.println("当前登录学生ID: " + currentStudentId);
        if (myConsultationsButton == null)
            throw new RuntimeException("myConsultationsButton 注入失败！");
        initializeConsultations();
        hallButton.getStyleClass().add("selected");
        myConsultationsButton.getStyleClass().remove("selected");
        hallButton.setOnAction(event -> {
            resetToInitialState();
        });
        myConsultationsButton.setOnAction(event -> {
            Main.loadScene("/ui/my_consultations.fxml");
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
                    loadConsultationCards();
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
                initializeConsultations();
                loadConsultationCards();
            } catch (IOException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("错误");
                alert.setHeaderText(null);
                alert.setContentText("无法加载新咨询窗口。");
                alert.showAndWait();
            }
        });
        searchField.textProperty().addListener((observable, oldValue, newValue) -> loadConsultationCards());
        if (allCategoriesButton != null)
            allCategoriesButton.getStyleClass().add("selected");
        updateConsultationCount();
        loadConsultationCards();
    }

    private void setupLeftNavButtons() {
        for (Node node : leftNavButtons.getChildren()) {
            if (node instanceof Button) {
                Button button = (Button) node;
                button.setOnAction(event -> {
                    for (Node otherNode : leftNavButtons.getChildren()) {
                        if (otherNode instanceof Button) {
                            otherNode.getStyleClass().remove("selected");
                        }
                    }
                    button.getStyleClass().add("selected");
                    String buttonText = button.getText();
                    if ("全部".equals(buttonText))
                        currentFilter = FilterType.ALL;
                    else if ("精选".equals(buttonText))
                        currentFilter = FilterType.FEATURED;
                    else if ("收藏".equals(buttonText))
                        currentFilter = FilterType.COLLECTED;
                    loadConsultationCards();
                });
            }
        }
    }

    private void initializeConsultations() {
        try {
            List<Consultation> list = consultationDao.getAllConsultations();
            // 查询每条咨询是否被当前学生收藏
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
                    switch (currentFilter) {
                        case ALL:
                            return true;
                        case FEATURED:
                            return c.isHighlighted();
                        case COLLECTED:
                            return c.isCollected();
                        case LEARNING:
                            return "学习".equals(c.getCategory());
                        case LIFE:
                            return "生活".equals(c.getCategory());
                        case OTHER:
                            return "其他".equals(c.getCategory());
                        default:
                            return true;
                    }
                })
                .filter(c -> searchText.isEmpty() ||
                        (c.getQuestionTitle() != null && c.getQuestionTitle().toLowerCase().contains(searchText)) ||
                        (c.getStatus() != null && c.getStatus().toLowerCase().contains(searchText)))
                .collect(Collectors.toList());
        for (Consultation consultation : filteredConsultations) {
            cardsContainer.getChildren().add(createConsultationCard(consultation));
        }
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
        ImageView collectIcon = new ImageView(new Image(getClass().getResourceAsStream(
                consultation.isCollected() ? "/images/collected.png" : "/images/uncollected.png")));
        collectIcon.setFitWidth(20);
        collectIcon.setFitHeight(20);
        collectIcon.getStyleClass().add("interaction-icon");
        // 收藏点击事件
        collectIcon.setOnMouseClicked(event -> {
            event.consume(); // 阻止冒泡到卡片点击
            try {
                if (consultation.isCollected()) {
                    collectDao.removeCollect(consultation.getQNumber(), currentStudentId);
                    consultation.setCollected(false);
                } else {
                    collectDao.addCollect(consultation.getQNumber(), currentStudentId);
                    consultation.setCollected(true);
                }
                // 刷新卡片UI
                loadConsultationCards();
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
                initializeConsultations();
                loadConsultationCards();
            });
            Stage detailStage = new Stage();
            detailStage.setTitle("咨询详情");
            detailStage.setScene(new Scene(detailRoot));
            detailStage.initModality(Modality.APPLICATION_MODAL);
            detailStage.initOwner(cardsContainer.getScene().getWindow());
            detailStage.setWidth(800);
            detailStage.setHeight(700);
            detailStage.showAndWait();
            loadConsultationCards();
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
}