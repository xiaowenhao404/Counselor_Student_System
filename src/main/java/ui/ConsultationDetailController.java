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
import javafx.scene.control.Alert;

// Import the shared Consultation and HistoryEntry classes
import static ui.StudentMainController.Consultation;
import static ui.StudentMainController.HistoryEntry;
import static ui.StudentMainController.ConsultationStatus; // Also import ConsultationStatus

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
    private HBox detailContentContainer;
    @FXML
    private Label detailContentLabelPrefix;
    @FXML
    private Button markUnresolvedButton;

    private Consultation currentConsultation;
    private Runnable onConsultationUpdated;

    @FXML
    public void initialize() {
        // 返回按钮点击事件
        HBox backButtonContainer = (HBox) backIcon.getParent();
        backButtonContainer.setOnMouseClicked(event -> closeWindow());

        // 收藏图标点击事件
        collectIcon.setOnMouseClicked(event -> toggleCollectStatus());

        // 追问按钮点击事件
        askFurtherButton.setOnAction(event -> showAskFurtherInput());

        // "仍需解决"按钮点击事件
        markUnresolvedButton.setOnAction(event -> markConsultationAsUnresolved());

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

    public void setConsultation(Consultation consultation) { // Use StudentMainController.Consultation
        this.currentConsultation = consultation;
        updateUIWithConsultationData();
    }

    // New setter for the update callback
    public void setOnConsultationUpdated(Runnable onConsultationUpdated) {
        this.onConsultationUpdated = onConsultationUpdated;
    }

    private void updateUIWithConsultationData() {
        if (currentConsultation == null) return;

        // 更新问题标题
        questionLabel.setText(currentConsultation.getQuestion());

        // 更新提问时间 (这个时间是首次提问时间)
        timeLabel.setText(currentConsultation.getTime());

        // 更新详细内容（从历史记录中查找第一个QUESTION类型的Entry）
        String detailContent = currentConsultation.getHistory().stream()
                                  .filter(entry -> entry.getType() == HistoryEntry.EntryType.QUESTION)
                                  .map(HistoryEntry::getContent)
                                  .findFirst()
                                  .orElse(null);

        if (detailContent != null && !detailContent.trim().isEmpty()) {
            contentLabel.setText(detailContent);
            detailContentContainer.setVisible(true);
            detailContentContainer.setManaged(true);
        } else {
            contentLabel.setText(""); // Clear content if empty
            detailContentContainer.setVisible(false);
            detailContentContainer.setManaged(false);
        }

        // 更新状态
        statusLabel.getStyleClass().clear();
        statusLabel.getStyleClass().add("status-tag");
        String statusDisplayName = currentConsultation.getStatus();
        if (statusDisplayName.equals(ConsultationStatus.UNANSWERED.displayName)) {
            statusLabel.setText("未答复");
            statusLabel.getStyleClass().add("status-unanswered");
        } else if (statusDisplayName.equals(ConsultationStatus.UNRESOLVED.displayName)) {
            statusLabel.setText("仍需解决");
            statusLabel.getStyleClass().add("status-unresolved");
        } else if (statusDisplayName.equals(ConsultationStatus.RESOLVED.displayName)) {
            statusLabel.setText("已解决");
            statusLabel.getStyleClass().add("status-resolved");
        }

        // 控制"我要追问"按钮的可见性
        if (currentConsultation.getStatus().equals(ConsultationStatus.UNANSWERED.displayName) ||
            currentConsultation.getStatus().equals(ConsultationStatus.UNRESOLVED.displayName)) {
            askFurtherButton.setVisible(true);
            askFurtherButton.setManaged(true);
        } else {
            askFurtherButton.setVisible(false);
            askFurtherButton.setManaged(false);
        }

        // 控制"仍需解决"按钮的可见性
        if (currentConsultation.getStatus().equals(ConsultationStatus.RESOLVED.displayName)) {
            markUnresolvedButton.setVisible(true);
            markUnresolvedButton.setManaged(true);
        } else {
            markUnresolvedButton.setVisible(false);
            markUnresolvedButton.setManaged(false);
        }

        // 更新咨询编号
        consultationIdLabel.setText("编号: " + currentConsultation.getId());

        // 更新收藏图标
        updateCollectIcon(currentConsultation.isCollected()); // Use getter

        // 加载历史记录
        loadHistory();
    }

    private void updateCollectIcon(boolean isCollected) {
        collectIcon.setImage(new Image(getClass().getResourceAsStream(
            isCollected ? "/images/collected.png" : "/images/uncollected.png")));
    }

    private void toggleCollectStatus() {
        if (currentConsultation != null) {
            currentConsultation.setCollected(!currentConsultation.isCollected()); // Use setter
            updateCollectIcon(currentConsultation.isCollected());
            if (onConsultationUpdated != null) { // Call the new callback
                onConsultationUpdated.run();
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

        if (furtherQuestion.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "输入错误", "输入内容不能为空。");
            askFurtherTextArea.getStyleClass().add("error-border");
            return;
        }

        if (furtherQuestion.length() > 100) {
            showAlert(Alert.AlertType.WARNING, "输入错误", "输入内容不能超过100字。");
            askFurtherTextArea.getStyleClass().add("error-border");
            return;
        }

        // 移除可能的错误边框
        askFurtherTextArea.getStyleClass().remove("error-border");

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String timestamp = now.format(formatter);

        // 添加新的"发布咨询"记录到历史列表
        HistoryEntry newEntry = new HistoryEntry(
            HistoryEntry.EntryType.QUESTION,
            furtherQuestion,
            null, // 追问没有回复单位
            timestamp
        );
        currentConsultation.getHistory().add(newEntry); // Add to history list

        // 更新咨询状态为"仍需解决"
        currentConsultation.setStatus(ConsultationStatus.UNRESOLVED.displayName);

        // 重新加载历史记录和更新UI
        updateUIWithConsultationData(); // This will also re-evaluate button visibility

        // 隐藏追问输入框，显示追问按钮（如果状态允许）
        askFurtherBox.setVisible(false);
        askFurtherBox.setManaged(false);

        // 刷新父窗口（如果需要，例如状态改变导致父窗口列表筛选变化）
        if (onConsultationUpdated != null) { // Call the new callback
            onConsultationUpdated.run();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // New method to mark consultation as unresolved
    private void markConsultationAsUnresolved() {
        if (currentConsultation != null) {
            currentConsultation.setStatus(ConsultationStatus.UNRESOLVED.displayName);
            updateUIWithConsultationData(); // Refresh UI to reflect new status and button visibility
            if (onConsultationUpdated != null) {
                onConsultationUpdated.run(); // Notify parent to refresh
            }
        }
    }

    private void loadHistory() {
        historyContainer.getChildren().clear(); // 清除现有记录

        if (currentConsultation != null && currentConsultation.getHistory() != null) {
            // 获取历史记录的副本并进行排序
            List<HistoryEntry> sortedHistory = new ArrayList<>(currentConsultation.getHistory());
            Collections.sort(sortedHistory); // Uses the compareTo method in HistoryEntry (descending order)

            for (HistoryEntry entry : sortedHistory) {
                historyContainer.getChildren().add(createHistoryEntryNode(entry));
            }
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
        } else if (entry.getType() == HistoryEntry.EntryType.REPLY) {
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