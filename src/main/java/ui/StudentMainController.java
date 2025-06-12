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
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
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
import ui.ConsultationDetailController;

public class StudentMainController {

    // 定义一个枚举来表示当前筛选器状态
    private enum FilterType {
        ALL,
        FEATURED,
        COLLECTED,
        LEARNING, // 新增：学习类别
        LIFE,     // 新增：生活类别
        OTHER     // 新增：其他类别
    }

    private FilterType currentFilter = FilterType.ALL; // 默认显示全部

    // 咨询状态枚举
    public enum ConsultationStatus {
        UNANSWERED("未答复"),
        UNRESOLVED("仍需解决"),
        RESOLVED("已解决");

        private final String displayName;

        ConsultationStatus(String displayName) {
            this.displayName = displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }

    // 咨询类别枚举
    public enum Category {
        LEARNING("学习"),
        LIFE("生活"),
        OTHER("其他");

        private final String displayName;

        Category(String displayName) {
            this.displayName = displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }

    // 咨询数据模型
    public static class Consultation {
        private final String question;
        private final String time;
        private final String detailContent;
        private final String reply;
        private final ConsultationStatus status;
        private final BooleanProperty isCollected;
        private final BooleanProperty isFeatured;
        private final Category category; // 新增字段：类别

        public Consultation(String question, String time, String detailContent, String reply, ConsultationStatus status, Category category) {
            this.question = question;
            this.time = time;
            this.detailContent = detailContent;
            this.reply = reply;
            this.status = status;
            this.isCollected = new SimpleBooleanProperty(false);
            this.isFeatured = new SimpleBooleanProperty(false);
            this.category = category; // 初始化类别
        }

        public String getQuestion() {
            return question;
        }

        public String getTime() {
            return time;
        }

        public String getDetailContent() {
            return detailContent;
        }

        public String getReply() {
            return reply;
        }

        public ConsultationStatus getStatus() {
            return status;
        }

        public boolean isCollected() {
            return isCollected.get();
        }

        public void setCollected(boolean collected) {
            this.isCollected.set(collected);
        }

        public boolean isFeatured() {
            return isFeatured.get();
        }

        public void setFeatured(boolean featured) {
            this.isFeatured.set(featured);
        }

        public Category getCategory() { // 新增getter
            return category;
        }
    }

    private ObservableList<Consultation> allConsultations; // 存储所有咨询数据

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
    private Button myButton;

    @FXML
    public void initialize() {
        initializeConsultations();
        loadConsultationCards();
        updateConsultationCount();

        // 确保"大厅"按钮默认选中
        hallButton.getStyleClass().add("selected");
        myButton.getStyleClass().remove("selected");

        // 设置顶部导航栏的点击事件
        hallButton.setOnAction(event -> {
            // 已经在当前页面，无需重新加载场景，只需刷新卡片（如果筛选等有变化）
            loadConsultationCards();
        });
        myButton.setOnAction(event -> {
            Main.loadScene("/ui/my_consultations.fxml"); // 加载我的咨询界面
        });

        // 左侧导航栏按钮切换逻辑 (添加筛选功能)
        setupLeftNavButtons();

        // 类别分栏按钮切换逻辑
        // 遍历 categoryBar 的子节点并为按钮设置事件监听器
        for (Node node : categoryBar.getChildren()) {
            if (node instanceof Button) {
                Button categoryButton = (Button) node;
                categoryButton.setOnAction(event -> {
                    // 移除所有类别按钮的选中状态
                    for (Node otherNode : categoryBar.getChildren()) {
                        if (otherNode instanceof Button) {
                            otherNode.getStyleClass().remove("selected");
                        }
                    }
                    // 添加当前点击按钮的选中状态
                    categoryButton.getStyleClass().add("selected");

                    // 根据按钮文本设置筛选类型
                    String buttonText = categoryButton.getText();
                    switch (buttonText) {
                        case "全部":
                            currentFilter = FilterType.ALL; // "全部"类别，对应 ALL 筛选器
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
                    loadConsultationCards(); // 重新加载卡片以应用新的类别筛选
                });
            }
        }

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

                // 窗口关闭后，可以根据需要刷新当前界面（例如，如果新咨询会影响当前列表）
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

        // 搜索框的监听器
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            loadConsultationCards(); // 文本改变时重新加载卡片，loadConsultationCards会处理搜索过滤
        });
    }

    // 单独设置左侧导航按钮，因为它们需要额外逻辑
    private void setupLeftNavButtons() {
        // 假设 leftNavButtons 已经包含了 "全部", "精选", "收藏" 按钮
        for (Node node : leftNavButtons.getChildren()) {
            if (node instanceof Button) {
                Button button = (Button) node;
                button.setOnAction(event -> {
                    // 移除所有按钮的选中状态
                    for (Node otherNode : leftNavButtons.getChildren()) {
                        if (otherNode instanceof Button) {
                            otherNode.getStyleClass().remove("selected");
                        }
                    }
                    button.getStyleClass().add("selected");

                    String buttonText = button.getText();
                    if ("全部".equals(buttonText)) {
                        currentFilter = FilterType.ALL;
                    } else if ("精选".equals(buttonText)) {
                        currentFilter = FilterType.FEATURED;
                    } else if ("收藏".equals(buttonText)) {
                        currentFilter = FilterType.COLLECTED;
                    }
                    // 注意：这里不需要再调用 loadConsultationCards()，因为 searchField 的监听器会触发它
                    // loadConsultationCards(); // 重新加载卡片以应用筛选
                    System.out.println("左侧导航按钮 " + button.getText() + " 被选中，筛选类型: " + currentFilter);
                });
            }
        }
    }

    private void loadConsultationCards() {
        cardsContainer.getChildren().clear(); // 清除现有卡片

        List<Consultation> currentViewConsultations = allConsultations.stream()
            // 先过滤掉所有"未答复"的咨询，因为大厅不显示此状态
            .filter(c -> c.getStatus() != ConsultationStatus.UNANSWERED)
            .collect(Collectors.toList());

        // 应用左侧导航栏的筛选
        currentViewConsultations = currentViewConsultations.stream().filter(c -> {
            if (currentFilter == FilterType.ALL) {
                return true;
            } else if (currentFilter == FilterType.COLLECTED) {
                return c.isCollected();
            } else if (currentFilter == FilterType.FEATURED) {
                return c.isFeatured();
            } else if (currentFilter == FilterType.LEARNING) { // 类别筛选
                return c.getCategory() == Category.LEARNING;
            } else if (currentFilter == FilterType.LIFE) { // 类别筛选
                return c.getCategory() == Category.LIFE;
            } else if (currentFilter == FilterType.OTHER) { // 类别筛选
                return c.getCategory() == Category.OTHER;
            }
            return false; // 默认不显示
        }).collect(Collectors.toList());

        // 应用搜索框的过滤
        String searchText = searchField.getText().toLowerCase();
        if (searchText != null && !searchText.trim().isEmpty()) {
            currentViewConsultations = currentViewConsultations.stream()
                .filter(c -> c.getQuestion().toLowerCase().contains(searchText) ||
                             c.getReply().toLowerCase().contains(searchText) ||
                             (c.getDetailContent() != null && c.getDetailContent().toLowerCase().contains(searchText)))
                .collect(Collectors.toList());
        }

        if (currentViewConsultations.isEmpty()) {
            Label noResultsLabel = new Label("暂无相关咨询");
            noResultsLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #888; -fx-padding: 20px;");
            cardsContainer.getChildren().add(noResultsLabel);
            return;
        }

        // 动态添加过滤后的咨询卡片
        for (Consultation consultation : currentViewConsultations) {
            cardsContainer.getChildren().add(createConsultationCard(consultation));
        }
    }

    // 辅助方法：创建一个咨询卡片，现在接收Consultation对象
    private Node createConsultationCard(Consultation consultation) {
        VBox card = new VBox(10);
        card.getStyleClass().add("consultation-card");
        card.setPadding(new Insets(15));

        // 问题标题
        Label questionLabel = new Label(consultation.getQuestion());
        questionLabel.getStyleClass().add("card-question");
        questionLabel.setWrapText(true);

        // 时间
        Label timeLabel = new Label(consultation.getTime());
        timeLabel.getStyleClass().add("card-time");

        // 回复内容
        Label replyLabel = new Label(consultation.getReply());
        replyLabel.getStyleClass().add("card-reply");
        replyLabel.setWrapText(true);

        // 状态标签 - 大厅只显示"已解决"和"未解决"
        Label statusLabel = new Label();
        statusLabel.getStyleClass().add("card-status-label");
        if (consultation.getStatus() == ConsultationStatus.RESOLVED) {
            statusLabel.setText("已解决");
            statusLabel.getStyleClass().add("status-resolved");
        } else { // 包括 UNANSWERED 和 UNRESOLVED，在大厅都显示为"未解决"
            statusLabel.setText("未解决");
            statusLabel.getStyleClass().add("status-unresolved"); // 使用unresolved样式，表示未解决
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
            boolean wasCollected = consultation.isCollected(); // 记录之前的状态
            consultation.setCollected(!wasCollected);
            updateCollectIcon(collectIcon, consultation.isCollected());
            event.consume();

            // 如果当前筛选器是"收藏"，并且该咨询卡片被取消收藏，则从列表中移除
            if (currentFilter == FilterType.COLLECTED && !consultation.isCollected()) {
                loadConsultationCards(); // 重新加载卡片以移除取消收藏的卡片
            }
        });

        // 精选图标（只显示，不可点击）
        ImageView featuredIcon = new ImageView(new Image(getClass().getResourceAsStream(
            consultation.isFeatured() ? "/images/choosen.png" : "/images/unchoosen.png")));
        featuredIcon.setFitWidth(20);
        featuredIcon.setFitHeight(20);
        featuredIcon.getStyleClass().add("interaction-icon");
        // 不添加点击事件，因为学生端不可操作

        interactionContainer.getChildren().addAll(messageIcon, collectIcon, featuredIcon);

        // 添加所有元素到卡片
        card.getChildren().addAll(questionLabel, timeLabel, replyLabel, statusLabel, interactionContainer);

        // 添加点击事件
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
                consultation.getTime(), // StudentMainController.Consultation 的 time 已经是 String
                consultation.getDetailContent(), // 详细内容
                consultation.getReply(), // 回复内容
                ConsultationDetailController.ConsultationStatus.valueOf(consultation.getStatus().name()),
                consultation.isCollected(),
                consultation.isFeatured() // 传递精选状态
            );
            controller.setConsultation(detailConsultation);

            // 设置当收藏状态改变时通知父窗口刷新的回调
            controller.setOnCollectStatusChanged(() -> {
                // 更新当前咨询的收藏状态（如果它被详情页修改了）
                consultation.setCollected(detailConsultation.isCollected());
                // 重新加载当前筛选下的卡片，以反映收藏状态的变化
                loadConsultationCards();
            });

            Stage detailStage = new Stage();
            detailStage.setTitle("咨询详情");
            detailStage.setScene(new Scene(detailRoot));
            detailStage.initModality(Modality.APPLICATION_MODAL);
            detailStage.initOwner(cardsContainer.getScene().getWindow());
            detailStage.showAndWait();

            // 窗口关闭后，可以根据需要刷新当前界面
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

    private void updateCollectIcon(ImageView collectIcon, boolean isCollected) {
        collectIcon.setImage(new Image(getClass().getResourceAsStream(
            isCollected ? "/images/collected.png" : "/images/uncollected.png")));
    }

    private void initializeConsultations() {
        allConsultations = javafx.collections.FXCollections.observableArrayList(
            new Consultation("请问暑假宿舍的话八月份几号会开放宿舍能回学校啊？", "2025-06-08 18:39", "我有一些关于宿舍开放时间的具体疑问，希望能得到详细的回复。我需要确认八月份具体的开放日期，以便安排返校行程。请问有任何相关的通知或链接可以提供吗？",
                "同学你好，假期住宿安排尚未确定，请在学期结束前关注信息公告相关通知。", ConsultationStatus.RESOLVED, Category.LIFE),
            new Consultation("关于奖学金申请的最新政策是什么？", "2025-06-07 10:30", "我需要了解本年度奖学金申请的最新细则，包括申请条件、所需材料和截止日期。",
                "请查阅学校官网的最新通知，或者咨询学生资助中心。", ConsultationStatus.UNANSWERED, Category.LEARNING),
            new Consultation("如何申请学籍异动？", "2025-06-06 14:00", "我需要办理学籍异动，请问具体的流程是什么？需要准备哪些材料？",
                "请到教务处领取相关表格，并按照要求准备材料。", ConsultationStatus.RESOLVED, Category.LIFE),
            new Consultation("图书馆的开放时间有调整吗？", "2025-06-05 09:15", null, 
                "图书馆的开放时间目前没有调整，具体请关注图书馆公告。", ConsultationStatus.UNRESOLVED, Category.OTHER)
        );

        // 设置一个示例精选咨询
        allConsultations.get(0).setFeatured(true);
        allConsultations.get(1).setFeatured(false); // 明确设置为未精选
        allConsultations.get(2).setFeatured(true);
        allConsultations.get(3).setFeatured(false); // 明确设置为未精选
    }

    private void updateConsultationCount() {
        // 实现更新咨询数量逻辑
    }
} 