package ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.geometry.Insets;
import javafx.scene.control.Separator;
import javafx.scene.control.Alert;
import javafx.scene.Node;
import ui.ConsultationDetailController;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MyConsultationsController {

    @FXML
    private Button hallButton;
    @FXML
    private Button myButton;
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
    private List<Consultation> filteredConsultations;

    // 咨询状态枚举
    public enum ConsultationStatus {
        UNANSWERED, // 未答复 (红色)
        UNRESOLVED, // 仍需解决 (黄色)
        RESOLVED    // 已解决 (绿色)
    }

    // 咨询数据模型
    public static class Consultation {
        private String id;
        private String question;
        private String detailContent;
        private LocalDateTime time;
        private String reply;
        private ConsultationStatus status;
        private boolean isCollected;

        public Consultation(String id, String question, String detailContent, LocalDateTime time, String reply, ConsultationStatus status, boolean isCollected) {
            this.id = id;
            this.question = question;
            this.detailContent = detailContent;
            this.time = time;
            this.reply = reply;
            this.status = status;
            this.isCollected = isCollected;
        }

        public String getId() { return id; }
        public String getQuestion() { return question; }
        public String getDetailContent() { return detailContent; }
        public LocalDateTime getTime() { return time; }
        public String getReply() { return reply; }
        public ConsultationStatus getStatus() { return status; }
        public void setStatus(ConsultationStatus status) { this.status = status; }
        public boolean isCollected() { return isCollected; }
        public void setCollected(boolean collected) { isCollected = collected; }
    }

    @FXML
    public void initialize() {
        initializeConsultations();
        loadConsultationCards(allConsultations);

        // 确保"我的"按钮默认选中，并移除"大厅"按钮的选中状态
        myButton.getStyleClass().add("selected");
        hallButton.getStyleClass().remove("selected");

        // 设置顶部导航栏的点击事件
        hallButton.setOnAction(event -> {
            Main.loadScene("/ui/student_main.fxml"); // 加载大厅界面
        });
        myButton.setOnAction(event -> {
            // 已经在当前页面，无需重新加载场景，只需刷新卡片（如果筛选等有变化）
            refreshCurrentFilter();
        });

        // 设置搜索框的监听器
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.trim().isEmpty()) {
                // 如果搜索框为空，显示当前筛选下的所有咨询
                refreshCurrentFilter();
            } else {
                // 在当前筛选的咨询中搜索
                ConsultationStatus currentStatus = getCurrentStatus();
                List<Consultation> filteredList = allConsultations.stream()
                    .filter(c -> c.getStatus() == currentStatus)
                    .filter(c -> c.getQuestion().toLowerCase().contains(newValue.toLowerCase()) ||
                               c.getReply().toLowerCase().contains(newValue.toLowerCase()))
                    .collect(Collectors.toList());
                loadConsultationCards(filteredList);
            }
        });

        // 设置左侧导航按钮的点击事件
        unansweredButton.setOnAction(event -> filterConsultations(ConsultationStatus.UNANSWERED));
        unresolvedButton.setOnAction(event -> filterConsultations(ConsultationStatus.UNRESOLVED));
        resolvedButton.setOnAction(event -> filterConsultations(ConsultationStatus.RESOLVED));

        // 为 "我要咨询" 按钮添加点击事件
        newConsultationButton.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/new_consultation.fxml"));
                Parent newConsultationRoot = loader.load();

                Stage newConsultationStage = new Stage();
                newConsultationStage.setTitle("发起新咨询");
                newConsultationStage.setScene(new Scene(newConsultationRoot));
                newConsultationStage.initModality(Modality.APPLICATION_MODAL); // 设置为模态窗口
                newConsultationStage.initOwner(((Node)event.getSource()).getScene().getWindow()); // 设置父窗口
                newConsultationStage.showAndWait(); // 显示并等待关闭

                // 窗口关闭后，可以根据需要刷新当前界面
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

        // 默认显示未答复的咨询
        filterConsultations(ConsultationStatus.UNANSWERED);
    }

    private void initializeConsultations() {
        // 初始化测试数据
        allConsultations = new ArrayList<>();
        allConsultations.add(new Consultation("20250611001", "食堂几点关门？", "我有一些关于食堂开放时间的具体疑问，希望能得到详细的回复。我需要确认八月份具体的开放日期，以便安排返校行程。请问有任何相关的通知或链接可以提供吗？", LocalDateTime.of(2025, 6, 9, 10, 0), "暂无回复", ConsultationStatus.UNANSWERED, false));
        allConsultations.add(new Consultation("20250611002", "如何提高英语口语？", "希望了解一些提高英语口语的实用方法和资源，特别是针对大学生。", LocalDateTime.of(2025, 6, 9, 11, 30), "多听多说，创造语言环境。", ConsultationStatus.UNRESOLVED, false));
        allConsultations.add(new Consultation("20250611003", "图书馆开放时间？", null, LocalDateTime.of(2025, 6, 9, 14, 0), "每日早8点至晚10点。", ConsultationStatus.RESOLVED, true));
        allConsultations.add(new Consultation("20250611004", "学校有心理咨询服务吗？", "想了解学校是否提供心理咨询服务，如何预约，以及费用等信息。", LocalDateTime.of(2025, 6, 9, 16, 45), "暂无回复", ConsultationStatus.UNANSWERED, false));
        allConsultations.add(new Consultation("20250611005", "选课有什么注意事项？", "第一次选课，希望能得到一些指导，比如如何避免选到不适合的课程，或者有哪些推荐的通识课。", LocalDateTime.of(2025, 6, 9, 9, 15), "注意学分要求和课程冲突。", ConsultationStatus.UNRESOLVED, true));
    }

    private ConsultationStatus getCurrentStatus() {
        if (unansweredButton.getStyleClass().contains("selected")) {
            return ConsultationStatus.UNANSWERED;
        } else if (unresolvedButton.getStyleClass().contains("selected")) {
            return ConsultationStatus.UNRESOLVED;
        } else if (resolvedButton.getStyleClass().contains("selected")) {
            return ConsultationStatus.RESOLVED;
        }
        return ConsultationStatus.UNANSWERED;
    }

    private void filterConsultations(ConsultationStatus status) {
        unansweredButton.getStyleClass().removeAll("selected");
        unresolvedButton.getStyleClass().removeAll("selected");
        resolvedButton.getStyleClass().removeAll("selected");

        List<Consultation> filteredList = allConsultations.stream()
                .filter(c -> c.getStatus() == status)
                .collect(Collectors.toList());
        loadConsultationCards(filteredList);

        // 设置当前选中按钮的样式
        switch (status) {
            case UNANSWERED:
                unansweredButton.getStyleClass().add("selected");
                break;
            case UNRESOLVED:
                unresolvedButton.getStyleClass().add("selected");
                break;
            case RESOLVED:
                resolvedButton.getStyleClass().add("selected");
                break;
        }
    }

    private void loadConsultationCards(List<Consultation> consultations) {
        cardsContainer.getChildren().clear(); // 清除现有卡片

        if (consultations.isEmpty()) {
            Label noResultsLabel = new Label("暂无相关咨询");
            noResultsLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #888; -fx-padding: 20px;");
            cardsContainer.getChildren().add(noResultsLabel);
            return;
        }

        for (Consultation consultation : consultations) {
            cardsContainer.getChildren().add(createConsultationCard(consultation));
            cardsContainer.getChildren().add(createSeparator()); // 添加分割线
        }
        // 移除最后一个分割线
        if (!cardsContainer.getChildren().isEmpty()) {
            cardsContainer.getChildren().remove(cardsContainer.getChildren().size() - 1);
        }
    }

    private VBox createConsultationCard(Consultation consultation) {
        VBox card = new VBox();
        card.getStyleClass().add("consultation-card");
        card.setSpacing(5);

        // 问题行
        HBox questionLine = new HBox();
        Label questionLabel = new Label("问题: " + consultation.getQuestion());
        questionLabel.getStyleClass().add("card-question");
        HBox.setHgrow(questionLabel, Priority.ALWAYS);
        Label idLabel = new Label("编号: " + consultation.getId());
        idLabel.getStyleClass().add("card-id");
        questionLine.getChildren().addAll(questionLabel, idLabel);

        // 提问时间行
        Label timeLabel = new Label("提问时间: " + consultation.getTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        timeLabel.getStyleClass().add("card-time");

        // 回复行
        Label replyLabel = new Label("回复: " + consultation.getReply());
        replyLabel.getStyleClass().add("card-reply");

        // 状态和勾选框行
        HBox statusAndCheckboxLine = new HBox();
        statusAndCheckboxLine.setSpacing(10);
        statusAndCheckboxLine.setPrefHeight(20);

        Label statusLabel = new Label();
        statusLabel.getStyleClass().add("card-status-label");
        switch (consultation.getStatus()) {
            case UNANSWERED:
                statusLabel.setText("未答复");
                statusLabel.getStyleClass().add("status-unanswered");
                break;
            case UNRESOLVED:
                statusLabel.setText("仍需解决");
                statusLabel.getStyleClass().add("status-unresolved");
                break;
            case RESOLVED:
                statusLabel.setText("已解决");
                statusLabel.getStyleClass().add("status-resolved");
                break;
        }
        statusLabel.setPadding(new Insets(2, 5, 2, 5));

        // 是否解决勾选框
        CheckBox solvedCheckbox = new CheckBox("是否解决?");
        solvedCheckbox.getStyleClass().add("solved-checkbox");
        // 根据咨询的当前状态设置勾选框的初始状态
        solvedCheckbox.setSelected(consultation.getStatus() == ConsultationStatus.RESOLVED);

        // 监听勾选框状态变化
        solvedCheckbox.setOnAction(event -> {
            if (solvedCheckbox.isSelected()) {
                // 如果勾选，且当前不是已解决状态，则设置为已解决
                if (consultation.getStatus() != ConsultationStatus.RESOLVED) {
                    consultation.setStatus(ConsultationStatus.RESOLVED);
                    // 刷新当前筛选下的列表，以便该咨询从原类别消失
                    refreshCurrentFilter();
                }
            } else {
                // 如果取消勾选，且当前是已解决状态，则设置为仍需解决（假设已回复）
                // 如果没有回复，则可能需要回到未答复状态，这里简化处理，假设取消勾选就回到仍需解决
                if (consultation.getStatus() == ConsultationStatus.RESOLVED) {
                    consultation.setStatus(ConsultationStatus.UNRESOLVED);
                    refreshCurrentFilter();
                }
            }
        });

        statusAndCheckboxLine.getChildren().addAll(statusLabel, solvedCheckbox);
        HBox.setHgrow(solvedCheckbox, Priority.ALWAYS);

        // 留言、收藏图标行
        HBox interactionContainer = new HBox();
        interactionContainer.getStyleClass().add("interaction-container");
        interactionContainer.setSpacing(5);
        
        ImageView messageIcon = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/message.png"))));
        messageIcon.setFitWidth(16);
        messageIcon.setFitHeight(16);

        // 收藏图标
        ImageView collectIcon = new ImageView(new Image(getClass().getResourceAsStream(
            consultation.isCollected() ? "/images/collected.png" : "/images/uncollected.png")));
        collectIcon.setFitWidth(20);
        collectIcon.setFitHeight(20);
        collectIcon.getStyleClass().add("interaction-icon");
        collectIcon.setPickOnBounds(true);
        collectIcon.setOnMouseClicked(event -> {
            consultation.setCollected(!consultation.isCollected());
            updateCollectIcon(collectIcon, consultation.isCollected());
            event.consume();
        });

        interactionContainer.getChildren().addAll(messageIcon, collectIcon);

        card.getChildren().addAll(questionLine, timeLabel, replyLabel, statusAndCheckboxLine, interactionContainer);
        card.setOnMouseClicked(event -> {
            openConsultationDetail(consultation);
        });

        return card;
    }

    private void openConsultationDetail(Consultation consultation) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/consultation_detail.fxml"));
            Parent detailRoot = loader.load();

            ConsultationDetailController controller = loader.getController();
            // 转换数据，因为 ConsultationDetailController.Consultation 可能有不同结构
            ConsultationDetailController.Consultation detailConsultation = new ConsultationDetailController.Consultation(
                consultation.getQuestion(),
                consultation.getTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                consultation.getDetailContent(),
                consultation.getReply(),
                ConsultationDetailController.ConsultationStatus.valueOf(consultation.getStatus().name()),
                consultation.isCollected(),
                false // 我的咨询界面不涉及精选，默认为false
            );
            controller.setConsultation(detailConsultation);

            // 设置当收藏状态改变时通知父窗口刷新的回调
            controller.setOnCollectStatusChanged(() -> {
                // 更新当前咨询的收藏状态（如果它被详情页修改了）
                consultation.setCollected(detailConsultation.isCollected());
                // 重新加载当前筛选下的卡片，以反映收藏状态的变化
                refreshCurrentFilter();
            });

            Stage detailStage = new Stage();
            detailStage.setTitle("咨询详情");
            detailStage.setScene(new Scene(detailRoot));
            detailStage.initModality(Modality.APPLICATION_MODAL);
            detailStage.initOwner(cardsContainer.getScene().getWindow());
            detailStage.showAndWait();

            // 窗口关闭后，可以根据需要刷新当前界面
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

    private void updateCollectIcon(ImageView icon, boolean isCollected) {
        String imagePath = isCollected ? "/images/collected.png" : "/images/uncollected.png";
        icon.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagePath))));
    }

    private Separator createSeparator() {
        Separator separator = new Separator();
        separator.getStyleClass().add("card-separator");
        return separator;
    }

    private void refreshCurrentFilter() {
        // 获取当前选中的左侧导航按钮状态
        ConsultationStatus currentStatus = getCurrentStatus();
        filterConsultations(currentStatus);
    }

    private void loadStudentMainScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/student_main.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) hallButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("辅导员学生交流系统——学生端");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
