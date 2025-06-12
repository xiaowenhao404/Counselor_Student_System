package ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.scene.layout.Priority;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ConsultationDetailController {

    @FXML
    private ImageView backIcon;
    @FXML
    private Label backLabel;
    @FXML
    private Label questionLabel;
    @FXML
    private Label timeLabel;
    @FXML
    private Label contentLabel;
    @FXML
    private Label statusLabel;
    @FXML
    private Label consultationIdLabel;
    @FXML
    private ImageView collectIcon;
    @FXML
    private VBox historyContainer;
    @FXML
    private Button askFurtherButton;
    @FXML
    private VBox askFurtherBox;
    @FXML
    private TextArea askFurtherTextArea;
    @FXML
    private Button sendAskFurtherButton;
    @FXML
    private Label askFurtherCharCountLabel;
    @FXML
    private HBox detailContentContainer; // 新增 fx:id 对应 HBox
    @FXML
    private Label detailContentLabelPrefix; // 新增 fx:id 对应 "详细内容：" 前缀标签

    private Consultation currentConsultation;
    private Runnable onCollectStatusChanged; // 用于通知父窗口收藏状态改变

    // 咨询状态枚举 (与 StudentMainController 中的保持一致)
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

    // 咨询数据模型 (为了简化，这里使用一个内部类，实际应用中可以考虑共享的实体类)
    public static class Consultation {
        private String question;
        private String time;
        private String content; // 详细提问内容，对应我要咨询里的内容
        private String reply; // 新增字段：回复内容
        private ConsultationStatus status;
        private BooleanProperty isCollected;
        private boolean isFeatured; // 精选状态，详情页只显示不操作

        public Consultation(String question, String time, String content, String reply, ConsultationStatus status, boolean isCollected, boolean isFeatured) {
            this.question = question;
            this.time = time;
            this.content = content;
            this.reply = reply;
            this.status = status;
            this.isCollected = new SimpleBooleanProperty(isCollected);
            this.isFeatured = isFeatured;
        }

        public String getQuestion() { return question; }
        public String getTime() { return time; }
        public String getContent() { return content; }
        public String getReply() { return reply; } // 新增getter
        public ConsultationStatus getStatus() { return status; }
        public boolean isCollected() { return isCollected.get(); }
        public void setCollected(boolean collected) { this.isCollected.set(collected); }
        public BooleanProperty collectedProperty() { return isCollected; }
        public boolean isFeatured() { return isFeatured; }
        public void setStatus(ConsultationStatus status) { this.status = status; }
    }

    // 历史记录条目数据模型
    public static class HistoryEntry {
        public enum EntryType {
            QUESTION, REPLY
        }
        private EntryType type;
        private String content;
        private String unit; // 回复单位 (仅回复类型有)
        private String timestamp;

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
    }

    @FXML
    public void initialize() {
        // 返回按钮点击事件
        HBox backButtonContainer = (HBox) backIcon.getParent();
        backButtonContainer.setOnMouseClicked(event -> closeWindow());

        // 收藏图标点击事件
        collectIcon.setOnMouseClicked(event -> toggleCollectStatus());

        // 追问按钮点击事件
        askFurtherButton.setOnAction(event -> showAskFurtherInput());

        // 发送追问按钮点击事件
        sendAskFurtherButton.setOnAction(event -> sendAskFurther());

        // 追问输入框字数限制和计数
        askFurtherTextArea.textProperty().addListener((observable, oldValue, newValue) -> {
            int charCount = newValue.length();
            askFurtherCharCountLabel.setText(charCount + "/100");
            if (charCount > 100) {
                askFurtherTextArea.getStyleClass().add("error-border");
                askFurtherCharCountLabel.getStyleClass().add("error-text");
            } else {
                askFurtherTextArea.getStyleClass().remove("error-border");
                askFurtherCharCountLabel.getStyleClass().remove("error-text");
            }
        });

        // 默认隐藏追问输入框
        askFurtherBox.setVisible(false);
        askFurtherBox.setManaged(false);
    }

    public void setConsultation(Consultation consultation) {
        this.currentConsultation = consultation;
        updateUIWithConsultationData();
    }

    public void setOnCollectStatusChanged(Runnable onCollectStatusChanged) {
        this.onCollectStatusChanged = onCollectStatusChanged;
    }

    private void updateUIWithConsultationData() {
        if (currentConsultation != null) {
            questionLabel.setText("问题：" + currentConsultation.getQuestion()); // 问题后面跟内容
            timeLabel.setText(currentConsultation.getTime()); // 提问时间字体变小在CSS里处理

            // 详细内容：如果当时没有输入的话就不用显示了，但是如果当时输入了就要显示
            if (currentConsultation.getContent() != null && !currentConsultation.getContent().trim().isEmpty()) {
                contentLabel.setText(currentConsultation.getContent()); // 仅设置内容，前缀由FXML负责
                detailContentContainer.setVisible(true);
                detailContentContainer.setManaged(true);
            } else {
                detailContentContainer.setVisible(false);
                detailContentContainer.setManaged(false);
            }

            // 去掉"当前状态"四个字，只保留一个状态的小方框
            statusLabel.setText(currentConsultation.getStatus().toString());
            statusLabel.getStyleClass().removeAll("status-unanswered", "status-unresolved", "status-resolved"); // 移除旧样式
            switch (currentConsultation.getStatus()) {
                case UNANSWERED:
                    statusLabel.getStyleClass().add("status-unanswered");
                    break;
                case UNRESOLVED:
                    statusLabel.getStyleClass().add("status-unresolved");
                    break;
                case RESOLVED:
                    statusLabel.getStyleClass().add("status-resolved");
                    break;
            }

            // 去掉"咨询编号"四个字，只保留编号：xxxx
            consultationIdLabel.setText("编号：" + Math.abs(currentConsultation.getQuestion().hashCode() % 100000)); // 保持显示编号
            updateCollectIcon(currentConsultation.isCollected());

            // 根据状态决定是否显示"我要追问"按钮
            if (currentConsultation.getStatus() == ConsultationStatus.UNANSWERED ||
                currentConsultation.getStatus() == ConsultationStatus.UNRESOLVED) {
                askFurtherButton.setVisible(true);
                askFurtherButton.setManaged(true);
            } else {
                askFurtherButton.setVisible(false);
                askFurtherButton.setManaged(false);
            }

            loadHistoryEntries();
        }
    }

    private void updateCollectIcon(boolean isCollected) {
        collectIcon.setImage(new Image(getClass().getResourceAsStream(
            isCollected ? "/images/collected.png" : "/images/uncollected.png")));
    }

    private void toggleCollectStatus() {
        if (currentConsultation != null) {
            currentConsultation.setCollected(!currentConsultation.isCollected());
            updateCollectIcon(currentConsultation.isCollected());
            if (onCollectStatusChanged != null) {
                onCollectStatusChanged.run(); // 通知父窗口刷新
            }
        }
    }

    private void showAskFurtherInput() {
        askFurtherBox.setVisible(true);
        askFurtherBox.setManaged(true);
        askFurtherButton.setVisible(false);
        askFurtherButton.setManaged(false);
        askFurtherTextArea.clear();
        askFurtherCharCountLabel.setText("0/100");
    }

    private void sendAskFurther() {
        String furtherQuestion = askFurtherTextArea.getText().trim();
        if (furtherQuestion.isEmpty() || furtherQuestion.length() > 100) {
            // 显示错误提示
            askFurtherTextArea.getStyleClass().add("error-border");
            return;
        }

        // TODO: 模拟数据插入，实际应与后端交互
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String timestamp = now.format(formatter);

        // 添加新的"发布咨询"记录
        // 注意：这里需要确保 ConsultationDetailController 知道如何更新其内部的 Consultation 对象的状态
        // 如果 ConsultationDetailController 的 Consultation 对象是直接从父窗口传递过来的引用，
        // 那么修改其状态会直接影响父窗口的数据。
        // 但为了数据的持久化和一致性，最好通过一个服务层来更新数据。
        // 在这里，我们先模拟更新状态和添加历史记录
        currentConsultation.setStatus(ConsultationStatus.UNRESOLVED); // 状态变为"仍需解决"

        HistoryEntry newEntry = new HistoryEntry(
            HistoryEntry.EntryType.QUESTION,
            furtherQuestion,
            null, // 追问没有回复单位
            timestamp
        );
        // 假设历史记录是可变的
        // For now, we'll just add it to a mock list and reload.
        // In a real application, you'd fetch the updated history from a backend.

        // 重新加载历史记录，现在假设我们有一个方法可以获取所有历史记录
        loadHistoryEntries(); // 重新加载以显示新的追问

        // 隐藏追问输入框，显示追问按钮
        askFurtherBox.setVisible(false);
        askFurtherBox.setManaged(false);
        askFurtherButton.setVisible(true);
        askFurtherButton.setManaged(true);

        // 刷新父窗口（如果需要，例如状态改变导致父窗口列表筛选变化）
        if (onCollectStatusChanged != null) {
            onCollectStatusChanged.run(); // 借用此回调，实际上应该有更通用的刷新机制
        }
    }

    private void loadHistoryEntries() {
        historyContainer.getChildren().clear(); // 清除现有记录

        // 模拟历史数据
        List<HistoryEntry> history = new ArrayList<>();
        history.add(new HistoryEntry(
            HistoryEntry.EntryType.REPLY,
            "同学你好，假期住宿安排尚未确定，请在学期结束前关注信息公告相关通知。",
            "辅导员A",
            "2025-06-08 18:45"
        ));
        history.add(new HistoryEntry(
            HistoryEntry.EntryType.QUESTION,
            "请问暑假宿舍的话八月份几号会开放宿舍能回学校啊？",
            null,
            "2025-06-08 18:39"
        ));
        // 添加刚刚的追问
        if (currentConsultation.getStatus() == ConsultationStatus.UNRESOLVED && !askFurtherTextArea.getText().trim().isEmpty()) {
            history.add(new HistoryEntry(
                HistoryEntry.EntryType.QUESTION,
                askFurtherTextArea.getText().trim(),
                null,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
            ));
        }

        // 按时间倒序排序 (最新在最上面)
        Collections.sort(history, (h1, h2) -> h2.getTimestamp().compareTo(h1.getTimestamp()));

        for (HistoryEntry entry : history) {
            historyContainer.getChildren().add(createHistoryEntryNode(entry));
        }
    }

    private Node createHistoryEntryNode(HistoryEntry entry) {
        VBox entryBox = new VBox();
        entryBox.getStyleClass().add("history-entry-box"); // 添加新样式，用于边框和背景
        entryBox.setPadding(new Insets(10));
        entryBox.setSpacing(5);

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(10);

        Label typeLabel = new Label();
        typeLabel.getStyleClass().add("history-type-label");

        Label timestampLabel = new Label(entry.getTimestamp());
        timestampLabel.getStyleClass().add("history-timestamp-label");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        VBox contentBox = new VBox(); // 用于包裹内容，实现框的效果
        contentBox.getStyleClass().add("history-content-box"); // 新增样式
        contentBox.setPadding(new Insets(5));

        Label contentLabel = new Label(entry.getContent());
        contentLabel.getStyleClass().add("history-content-label");
        contentLabel.setWrapText(true);
        contentBox.getChildren().add(contentLabel);

        if (entry.getType() == HistoryEntry.EntryType.QUESTION) {
            typeLabel.setText("发布咨询");
            typeLabel.getStyleClass().add("history-type-question"); // 添加特定样式
            header.getChildren().addAll(typeLabel, spacer, timestampLabel);
            entryBox.getChildren().addAll(header, contentBox); // 问题内容放入框中
        } else {
            typeLabel.setText("已回复");
            typeLabel.getStyleClass().add("history-type-reply"); // 添加特定样式
            Label unitLabel = new Label("回复单位：" + entry.getUnit());
            unitLabel.getStyleClass().add("history-unit-label");
            header.getChildren().addAll(typeLabel, unitLabel, spacer, timestampLabel);
            entryBox.getChildren().addAll(header, contentBox); // 回复内容放入框中
        }
        
        return entryBox;
    }

    private void closeWindow() {
        Stage stage = (Stage) backIcon.getScene().getWindow();
        stage.close();
    } 
} 