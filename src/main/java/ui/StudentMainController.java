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
import javafx.collections.FXCollections;
import java.util.ArrayList;
import java.util.Collections;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

        public final String displayName;

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

    // 历史记录条目数据模型
    public static class HistoryEntry implements Comparable<HistoryEntry> {
        public enum EntryType {
            QUESTION, REPLY
        }
        private EntryType type;
        private String content;
        private String unit; // 回复单位 (仅回复类型有)
        private String timestamp; // 存储为 String，但用于排序时会转换为 LocalDateTime

        public HistoryEntry(EntryType type, String content, String unit, String timestamp) {
            this.type = type;
            this.content = content;
            this.unit = unit;
            this.timestamp = timestamp;
        }

        public EntryType getType() { return type; }
        public String getContent() { return content; }
        public String getUnit() { return unit; }
        public String getTimestamp() { return timestamp; }

        @Override
        public int compareTo(HistoryEntry other) {
            // 实现按时间倒序排序
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime thisTime = LocalDateTime.parse(this.timestamp, formatter);
            LocalDateTime otherTime = LocalDateTime.parse(other.timestamp, formatter);
            return otherTime.compareTo(thisTime); // 倒序
        }
    }

    // 咨询数据模型
    public static class Consultation {
        private String id;
        private String question;
        private String time;
        private String status;
        private String category;
        private boolean isFeatured;
        private boolean isCollected;
        private List<HistoryEntry> history; // 新增：存储对话历史

        public Consultation(String id, String question, String initialDetailContent, String initialReply, String time, String status, String category, boolean isFeatured, boolean isCollected) {
            this.id = id;
            this.question = question;
            this.time = time;
            this.status = status;
            this.category = category;
            this.isFeatured = isFeatured;
            this.isCollected = isCollected;
            this.history = new ArrayList<>();

            // 根据传入的初始详细内容和回复构建历史记录
            if (initialDetailContent != null && !initialDetailContent.trim().isEmpty()) {
                this.history.add(new HistoryEntry(HistoryEntry.EntryType.QUESTION, initialDetailContent, null, time));
            } else {
                // 如果没有详细内容，则将问题本身作为第一个提问记录
                this.history.add(new HistoryEntry(HistoryEntry.EntryType.QUESTION, question, null, time));
            }

            if (initialReply != null && !initialReply.trim().isEmpty() && !initialReply.equals("暂无回复")) {
                // 假设初始回复的单位是"辅导员A"
                this.history.add(new HistoryEntry(HistoryEntry.EntryType.REPLY, initialReply, "辅导员A", time));
            }
        }

        // 现有的getter方法
        public String getId() { return id; }
        public String getQuestion() { return question; }
        // 注意：getDetailContent() 和 getReply() 不再直接从 Consultation 获取，而是从 history 列表获取
        // 为了兼容旧代码，暂时保留，但后续会移除或修改其实现
        public String getDetailContent() {
            // 从历史记录中查找第一个QUESTION类型的Entry作为详细内容
            return history.stream()
                          .filter(entry -> entry.getType() == HistoryEntry.EntryType.QUESTION)
                          .map(HistoryEntry::getContent)
                          .findFirst()
                          .orElse(null);
        }

        public String getReply() {
            // 从历史记录中查找最后一个REPLY类型的Entry作为最新回复
            return history.stream()
                          .filter(entry -> entry.getType() == HistoryEntry.EntryType.REPLY)
                          .map(HistoryEntry::getContent)
                          .reduce((first, second) -> second) // Get the last one
                          .orElse("暂无回复");
        }

        public String getTime() { return time; } // 这个时间是首次提问时间，不是每次历史记录的时间
        public String getStatus() { return status; }
        public String getCategory() { return category; }
        public boolean isFeatured() { return isFeatured; }
        public boolean isCollected() { return isCollected; }
        public List<HistoryEntry> getHistory() { return history; } // 新增 getter

        // Setter for status (for updating after a new reply/follow-up)
        public void setStatus(String status) { this.status = status; }
        // Setter for collected status (for updating from detail page)
        public void setCollected(boolean collected) { isCollected = collected; }
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
    public void initialize() {
        initializeConsultations();

        // 确保"大厅"按钮默认选中
        hallButton.getStyleClass().add("selected");
        myConsultationsButton.getStyleClass().remove("selected");

        // 设置顶部导航栏的点击事件
        hallButton.setOnAction(event -> {
            loadConsultationCards();
        });
        myConsultationsButton.setOnAction(event -> {
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

        // 设置默认选中的类别按钮
        if (allCategoriesButton != null) { // 添加 null 检查
            allCategoriesButton.getStyleClass().add("selected");
        }

        // 更新咨询数量（现在可以安全调用）
        updateConsultationCount();

        // 在所有按钮都已初始化并设置事件监听器后，最后加载卡片
        loadConsultationCards();
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
                    loadConsultationCards(); // 重新加载卡片以应用筛选
                    System.out.println("左侧导航按钮 " + button.getText() + " 被选中，筛选类型: " + currentFilter);
                });
            }
        }
    }

    private void loadConsultationCards() {
        cardsContainer.getChildren().clear();
        
        // 获取搜索文本
        String searchText = searchField.getText().toLowerCase();
        
        // 过滤咨询列表
        List<Consultation> filteredConsultations = allConsultations.stream()
            .filter(c -> c.getStatus().equals(ConsultationStatus.RESOLVED.displayName) ||
                         c.getStatus().equals(ConsultationStatus.UNRESOLVED.displayName)) // 只显示已解决和未解决
            .filter(c -> {
                // 根据当前筛选器类型过滤
                switch (currentFilter) {
                    case ALL:
                        return true;
                    case FEATURED:
                        return c.isFeatured();
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
                         c.getQuestion().toLowerCase().contains(searchText) ||
                         (c.getReply() != null && c.getReply().toLowerCase().contains(searchText)) ||
                         (c.getDetailContent() != null && c.getDetailContent().toLowerCase().contains(searchText)))
            .collect(Collectors.toList());

        // 动态添加咨询卡片
        for (Consultation consultation : filteredConsultations) {
            cardsContainer.getChildren().add(createConsultationCard(consultation));
        }
    }

    private String getConsultationStatusName(String displayName) {
        for (ConsultationStatus status : ConsultationStatus.values()) {
            if (status.displayName.equals(displayName)) {
                return status.name();
            }
        }
        return null; // 或者抛出异常，如果 displayName 无效
    }

    private Node createConsultationCard(Consultation consultation) {
        VBox card = new VBox();
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
        Label replyLabel = new Label();
        if (consultation.getReply() == null || consultation.getReply().trim().isEmpty() || consultation.getReply().equals("暂无回复")) {
            replyLabel.setText("暂无回复");
        } else {
            replyLabel.setText(consultation.getReply());
        }
        replyLabel.getStyleClass().add("card-reply");
        replyLabel.setWrapText(true);

        // 状态标签 - 大厅只显示"已解决"和"未解决"
        Label statusLabel = new Label();
        statusLabel.getStyleClass().add("card-status-label");
        if (consultation.getStatus().equals(ConsultationStatus.RESOLVED.displayName)) {
            statusLabel.setText("已解决");
            statusLabel.getStyleClass().add("status-resolved");
        } else if (consultation.getStatus().equals(ConsultationStatus.UNRESOLVED.displayName)) { // 明确处理仍需解决为未解决
            statusLabel.setText("仍需解决"); // 修改为 "仍需解决"
            statusLabel.getStyleClass().add("status-unresolved"); // 使用unresolved样式，表示未解决
        }
        // 未答复状态的咨询已在 loadConsultationCards 中过滤，这里无需处理

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
            boolean wasCollected = consultation.isCollected;
            consultation.isCollected = !wasCollected;
            updateCollectIcon(collectIcon, consultation.isCollected);
            event.consume();

            // 如果当前筛选器是"收藏"，并且该咨询卡片被取消收藏，则从列表中移除
            if (currentFilter == FilterType.COLLECTED && !consultation.isCollected) {
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
        System.out.println("尝试打开咨询详情窗口：" + consultation.getId()); // 添加调试打印语句
        System.out.println("StudentMainController - Passing consultation object with history size: " + consultation.getHistory().size());
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/consultation_detail.fxml"));
            Parent detailRoot = loader.load();

            ConsultationDetailController controller = loader.getController();
            // 直接传递 Consultation 对象，因为它们现在共享同一个定义
            controller.setConsultation(consultation);

            // 设置当咨询状态或收藏状态改变时通知父窗口刷新的回调
            controller.setOnConsultationUpdated(() -> {
                loadConsultationCards(); // 重新加载卡片以反映变化
            });

            Stage detailStage = new Stage();
            detailStage.setTitle("咨询详情");
            detailStage.setScene(new Scene(detailRoot));
            detailStage.initModality(Modality.APPLICATION_MODAL);
            detailStage.initOwner(cardsContainer.getScene().getWindow());
            detailStage.setWidth(800); // 固定宽度
            detailStage.setHeight(700); // 固定高度
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
        allConsultations = FXCollections.observableArrayList(
            new Consultation("20250611001", "食堂几点关门？", "我有一些关于食堂开放时间的具体疑问，希望能得到详细的回复。我需要确认八月份具体的开放日期，以便安排返校行程。请问有任何相关的通知或链接可以提供吗？", "暂无回复", "2025-06-09 10:00", ConsultationStatus.UNANSWERED.displayName, Category.LIFE.displayName, false, false),
            new Consultation("20250611002", "如何提高英语口语？", "希望了解一些提高英语口语的实用方法和资源，特别是针对大学生。", "多听多说，创造语言环境。", "2025-06-09 11:30", ConsultationStatus.UNRESOLVED.displayName, Category.LEARNING.displayName, false, false),
            new Consultation("20250611003", "图书馆开放时间？", null, "每日早8点至晚10点。", "2025-06-09 14:00", ConsultationStatus.RESOLVED.displayName, Category.OTHER.displayName, true, true),
            new Consultation("20250611004", "学校有心理咨询服务吗？", "想了解学校是否提供心理咨询服务，如何预约，以及费用等信息。", "暂无回复", "2025-06-09 16:45", ConsultationStatus.UNANSWERED.displayName, Category.LIFE.displayName, false, false),
            new Consultation("20250611005", "选课有什么注意事项？", "第一次选课，希望能得到一些指导，比如如何避免选到不适合的课程，或者有哪些推荐的通识课。", "注意学分要求和课程冲突。", "2025-06-09 09:15", ConsultationStatus.UNRESOLVED.displayName, Category.LEARNING.displayName, false, true),
            new Consultation("20250611006", "关于奖学金申请", "我有一些关于奖学金申请的疑问，想了解具体流程和所需材料。", "请参考学校官网奖学金申请指南。", "2025-06-10 09:00", ConsultationStatus.RESOLVED.displayName, Category.OTHER.displayName, false, false),
            new Consultation("20250611007", "如何办理休学？", "因个人原因需要休学，想了解休学手续的办理流程和注意事项。", "请到教务处办理相关手续。", "2025-06-10 11:00", ConsultationStatus.UNRESOLVED.displayName, Category.LIFE.displayName, false, false),
            new Consultation("20250611008", "C++编程学习", "初学C++，想知道有哪些好的学习资料和实践项目可以推荐。", "推荐《C++ Primer Plus》和在线编程平台。", "2025-06-10 14:30", ConsultationStatus.RESOLVED.displayName, Category.LEARNING.displayName, true, false),
            new Consultation("20250611009", "学校周边兼职机会", "想在课余时间找一些兼职工作，学校周边有哪些推荐的平台或机会？", "请关注学校勤工助学中心发布的信息。", "2025-06-10 16:00", ConsultationStatus.UNRESOLVED.displayName, Category.LIFE.displayName, false, false),
            new Consultation("20250611010", "毕业论文选题", "临近毕业，对论文选题感到困惑，希望能获得一些指导和建议。", "建议选择自己感兴趣且有一定研究基础的领域。", "2025-06-11 09:30", ConsultationStatus.UNRESOLVED.displayName, Category.LEARNING.displayName, false, false)
        );
    }

    private void updateConsultationCount() {
        // 实现更新咨询数量逻辑
    }
}