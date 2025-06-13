package ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Separator;
import javafx.scene.control.Alert;
import javafx.scene.Node;
import dao.ConsultationDaoImpl;
import entity.Consultation;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class MyConsultationsController {

    @FXML
    private Button hallButton;
    @FXML
    private Button myConsultationsButton;
    @FXML
    private TextField searchField;
    @FXML
    private Button unansweredButton;
    @FXML
    private Button unresolvedButton;
    @FXML
    private Button resolvedButton;
    @FXML
    private Button newConsultationButton;
    @FXML
    private VBox cardsContainer;

    private List<Consultation> allConsultations;
    private ConsultationDaoImpl consultationDao = new ConsultationDaoImpl();
    private String currentStudentId = Main.getCurrentStudentId(); // 使用Main中存储的学生ID

    public MyConsultationsController() {
        System.out.println("MyConsultationsController 构造方法被调用");
    }

    @FXML
    public void initialize() {
        System.out.println("MyConsultationsController.initialize() 被调用");
        System.out.println("当前登录学生ID: " + currentStudentId); // 添加日志
        System.out.println("hallButton is " + hallButton);
        System.out.println("myConsultationsButton is " + myConsultationsButton);
        System.out.println("cardsContainer is " + cardsContainer);
        if (hallButton == null || myConsultationsButton == null || cardsContainer == null)
            throw new RuntimeException("MyConsultationsController 关键控件注入失败！");
        // 顶部按钮高亮
        myConsultationsButton.getStyleClass().add("selected");
        hallButton.getStyleClass().remove("selected");
        hallButton.setOnAction(event -> {
            System.out.println("逗逗你的呀");
            Main.loadScene("/ui/student_main.fxml");
        });
        myConsultationsButton.setOnAction(event -> {
            refreshCurrentFilter();
        });
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.trim().isEmpty()) {
                refreshCurrentFilter();
            } else {
                String status = getCurrentStatus();
                List<Consultation> filteredList = allConsultations.stream()
                        .filter(c -> c.getStatus().equals(status))
                        .filter(c -> c.getQuestionTitle().toLowerCase().contains(newValue.toLowerCase()))
                        .collect(Collectors.toList());
                loadConsultationCards(filteredList);
            }
        });
        unansweredButton.setOnAction(event -> filterConsultations("未回复"));
        unresolvedButton.setOnAction(event -> filterConsultations("仍需解决"));
        resolvedButton.setOnAction(event -> filterConsultations("已解决"));
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
                refreshCurrentFilter();
            } catch (IOException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("错误");
                alert.setHeaderText(null);
                alert.setContentText("无法加载新咨询窗口。");
                alert.showAndWait();
            }
        });
        initializeConsultations();
        filterConsultations("未回复");
    }

    private void initializeConsultations() {
        try {
            List<Consultation> list = consultationDao.getAllConsultations();
            // 只保留当前学生的咨询
            allConsultations = list.stream()
                    .filter(c -> c.getStudentId().equals(currentStudentId))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            allConsultations = java.util.Collections.emptyList();
        }
    }

    private String getCurrentStatus() {
        if (unansweredButton.getStyleClass().contains("selected")) {
            return "未回复";
        } else if (unresolvedButton.getStyleClass().contains("selected")) {
            return "仍需解决";
        } else if (resolvedButton.getStyleClass().contains("selected")) {
            return "已解决";
        }
        return "未回复";
    }

    private void filterConsultations(String status) {
        unansweredButton.getStyleClass().removeAll("selected");
        unresolvedButton.getStyleClass().removeAll("selected");
        resolvedButton.getStyleClass().removeAll("selected");
        List<Consultation> filteredList = allConsultations.stream()
                .filter(c -> c.getStatus().equals(status))
                .collect(Collectors.toList());
        loadConsultationCards(filteredList);
        if ("未回复".equals(status)) {
            unansweredButton.getStyleClass().add("selected");
        } else if ("仍需解决".equals(status)) {
            unresolvedButton.getStyleClass().add("selected");
        } else if ("已解决".equals(status)) {
            resolvedButton.getStyleClass().add("selected");
        }
    }

    private void loadConsultationCards(List<Consultation> consultations) {
        System.out.println(
                "loadConsultationCards 被调用, consultations.size=" + (consultations == null ? 0 : consultations.size()));
        cardsContainer.getChildren().clear();
        if (consultations.isEmpty()) {
            Label noResultsLabel = new Label("暂无相关咨询");
            noResultsLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #888; -fx-padding: 20px;");
            cardsContainer.getChildren().add(noResultsLabel);
            System.out.println("UI显示：暂无相关咨询");
            return;
        }
        int cardCount = 0;
        for (Consultation consultation : consultations) {
            cardsContainer.getChildren().add(createConsultationCard(consultation));
            cardsContainer.getChildren().add(createSeparator());
            cardCount++;
        }
        if (!cardsContainer.getChildren().isEmpty()) {
            cardsContainer.getChildren().remove(cardsContainer.getChildren().size() - 1);
        }
        System.out.println("实际渲染卡片数量: " + cardCount);
    }

    private VBox createConsultationCard(Consultation consultation) {
        VBox card = new VBox();
        card.getStyleClass().add("consultation-card");
        card.setPadding(new Insets(15));
        // 问题标题
        Label questionLabel = new Label(consultation.getQuestionTitle());
        questionLabel.getStyleClass().add("card-question");
        questionLabel.setWrapText(true);
        // 时间
        String timeStr = consultation.getQuestionTime() != null
                ? consultation.getQuestionTime()
                        .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
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
        // 咨询类型标签
        Label categoryLabel = new Label(consultation.getCategory());
        categoryLabel.getStyleClass().add("category-tag");
        HBox statusCategoryBox = new HBox(8, statusLabel, categoryLabel);
        statusCategoryBox.setAlignment(Pos.CENTER_LEFT);
        // 交互区域
        HBox interactionContainer = new HBox(15);
        interactionContainer.setAlignment(Pos.CENTER_LEFT);
        ImageView messageIcon = new ImageView(new Image(getClass().getResourceAsStream("/images/message.png")));
        messageIcon.setFitWidth(20);
        messageIcon.setFitHeight(20);
        messageIcon.getStyleClass().add("interaction-icon");
        // 收藏图标（如有收藏功能）
        // ImageView collectIcon = ... // 可参考大厅实现
        ImageView featuredIcon = new ImageView(new Image(getClass().getResourceAsStream(
                consultation.isHighlighted() ? "/images/choosen.png" : "/images/unchoosen.png")));
        featuredIcon.setFitWidth(20);
        featuredIcon.setFitHeight(20);
        featuredIcon.getStyleClass().add("interaction-icon");
        interactionContainer.getChildren().addAll(messageIcon, featuredIcon);
        // 添加所有元素到卡片
        card.getChildren().addAll(questionLabel, timeLabel, replyLabel, statusCategoryBox, interactionContainer);
        card.setOnMouseClicked(event -> openConsultationDetail(consultation));
        return card;
    }

    private void openConsultationDetail(Consultation consultation) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/consultation_detail.fxml"));
            Parent detailRoot = loader.load();
            ConsultationDetailController controller = loader.getController();
            controller.setConsultation(consultation);
            controller.setOnConsultationUpdated(this::refreshCurrentFilter);
            Stage detailStage = new Stage();
            detailStage.setTitle("咨询详情");
            detailStage.setScene(new Scene(detailRoot));
            detailStage.initModality(Modality.APPLICATION_MODAL);
            detailStage.initOwner(cardsContainer.getScene().getWindow());
            detailStage.setWidth(800);
            detailStage.setHeight(700);
            detailStage.showAndWait();
            refreshCurrentFilter();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("错误");
            alert.setHeaderText(null);
            alert.setContentText("无法加载咨询详情窗口。");
            alert.showAndWait();
        }
    }

    private Separator createSeparator() {
        Separator separator = new Separator();
        separator.setPadding(new Insets(5, 0, 5, 0));
        return separator;
    }

    private void refreshCurrentFilter() {
        filterConsultations(getCurrentStatus());
    }
}
