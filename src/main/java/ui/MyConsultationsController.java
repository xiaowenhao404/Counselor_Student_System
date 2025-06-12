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
import javafx.geometry.Pos;
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

// 导入共享的类
import static ui.StudentMainController.Consultation;
import static ui.StudentMainController.ConsultationStatus;

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
                    .filter(c -> c.getStatus().equals(currentStatus.displayName))
                    .filter(c -> c.getQuestion().toLowerCase().contains(newValue.toLowerCase()) ||
                               (c.getReply() != null && c.getReply().toLowerCase().contains(newValue.toLowerCase())))
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
        allConsultations.add(new Consultation("20250611001", "食堂几点关门？", "我有一些关于食堂开放时间的具体疑问，希望能得到详细的回复。我需要确认八月份具体的开放日期，以便安排返校行程。请问有任何相关的通知或链接可以提供吗？", "暂无回复", "2025-06-09 10:00", ConsultationStatus.UNANSWERED.displayName, "生活", false, false));
        allConsultations.add(new Consultation("20250611002", "如何提高英语口语？", "希望了解一些提高英语口语的实用方法和资源，特别是针对大学生。", "多听多说，创造语言环境。", "2025-06-09 11:30", ConsultationStatus.UNRESOLVED.displayName, "学习", false, false));
        allConsultations.add(new Consultation("20250611003", "图书馆开放时间？", null, "每日早8点至晚10点。", "2025-06-09 14:00", ConsultationStatus.RESOLVED.displayName, "其他", true, false));
        allConsultations.add(new Consultation("20250611004", "学校有心理咨询服务吗？", "想了解学校是否提供心理咨询服务，如何预约，以及费用等信息。", "暂无回复", "2025-06-09 16:45", ConsultationStatus.UNANSWERED.displayName, "生活", false, false));
        allConsultations.add(new Consultation("20250611005", "选课有什么注意事项？", "第一次选课，希望能得到一些指导，比如如何避免选到不适合的课程，或者有哪些推荐的通识课。", "注意学分要求和课程冲突。", "2025-06-09 09:15", ConsultationStatus.UNRESOLVED.displayName, "学习", true, false));
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
                .filter(c -> c.getStatus().equals(status.displayName))
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
        Label timeLabel = new Label("提问时间: " + consultation.getTime());
        timeLabel.getStyleClass().add("card-time");

        // 回复行
        Label replyLabel = new Label();
        if (consultation.getReply() == null || consultation.getReply().trim().isEmpty() || consultation.getReply().equals("暂无回复")) {
            replyLabel.setText("暂无回复");
        } else {
            replyLabel.setText(consultation.getReply());
        }
        replyLabel.getStyleClass().add("card-reply");

        // 状态标签
        Label statusLabel = new Label();
        statusLabel.getStyleClass().add("card-status-label");
        if (consultation.getStatus().equals(ConsultationStatus.RESOLVED.displayName)) {
            statusLabel.setText("已解决");
            statusLabel.getStyleClass().add("status-resolved");
        } else if (consultation.getStatus().equals(ConsultationStatus.UNRESOLVED.displayName)) {
            statusLabel.setText("仍需解决");
            statusLabel.getStyleClass().add("status-unresolved");
        } else if (consultation.getStatus().equals(ConsultationStatus.UNANSWERED.displayName)) {
            statusLabel.setText("未答复");
            statusLabel.getStyleClass().add("status-unanswered");
        }

        // 底部交互区域
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
        collectIcon.setPickOnBounds(true);
        collectIcon.setOnMouseClicked(event -> {
            consultation.setCollected(!consultation.isCollected());
            updateCollectIcon(collectIcon, consultation.isCollected());
            event.consume();
        });

        // 精选图标（只显示，不可点击）
        ImageView featuredIcon = new ImageView(new Image(getClass().getResourceAsStream(
            consultation.isFeatured() ? "/images/choosen.png" : "/images/unchoosen.png")));
        featuredIcon.setFitWidth(20);
        featuredIcon.setFitHeight(20);
        featuredIcon.getStyleClass().add("interaction-icon");

        interactionContainer.getChildren().addAll(messageIcon, collectIcon, featuredIcon);

        // 添加所有元素到卡片
        card.getChildren().addAll(questionLine, timeLabel, replyLabel, statusLabel, interactionContainer);

        // 添加点击事件
        card.setOnMouseClicked(event -> {
            openConsultationDetail(consultation);
        });

        return card;
    }

    private void openConsultationDetail(Consultation consultation) {
        System.out.println("尝试打开咨询详情：" + consultation.getId());
        try {
            System.out.println("加载 FXML 文件：/ui/consultation_detail.fxml");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/consultation_detail.fxml"));
            Parent detailRoot = loader.load();
            System.out.println("FXML 文件加载成功。");

            ConsultationDetailController controller = loader.getController();
            controller.setConsultation(consultation);
            controller.setOnConsultationUpdated(() -> {
                refreshCurrentFilter();
            });

            Stage detailStage = new Stage();
            detailStage.setTitle("咨询详情");
            detailStage.setScene(new Scene(detailRoot));
            detailStage.initModality(Modality.APPLICATION_MODAL);
            detailStage.initOwner(cardsContainer.getScene().getWindow());
            detailStage.setWidth(800); // 固定宽度
            detailStage.setHeight(700); // 固定高度
            detailStage.showAndWait();
            System.out.println("咨询详情窗口已关闭。");

            // 窗口关闭后，可以根据需要刷新当前界面
            refreshCurrentFilter();

        } catch (IOException e) {
            System.err.println("加载咨询详情窗口时发生错误：" + e.getMessage());
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("错误");
            alert.setHeaderText(null);
            alert.setContentText("无法加载咨询详情窗口。");
            alert.showAndWait();
        }
    }

    private void updateCollectIcon(ImageView icon, boolean isCollected) {
        icon.setImage(new Image(getClass().getResourceAsStream(
            isCollected ? "/images/collected.png" : "/images/uncollected.png")));
    }

    private Separator createSeparator() {
        Separator separator = new Separator();
        separator.setPadding(new Insets(10, 0, 10, 0));
        return separator;
    }

    private void refreshCurrentFilter() {
        ConsultationStatus currentStatus = getCurrentStatus();
        filterConsultations(currentStatus);
    }

    private void loadStudentMainScene() {
        Main.loadScene("/ui/student_main.fxml");
    }
}
