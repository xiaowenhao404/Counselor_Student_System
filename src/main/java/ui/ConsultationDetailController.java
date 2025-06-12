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
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import entity.Consultation;
import dao.ReplyDaoImpl;
import dao.FollowupDaoImpl;
import dao.CollectDaoImpl;
import entity.Reply;
import entity.Followup;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import dao.ConsultationDaoImpl;

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

    private final ReplyDaoImpl replyDao = new ReplyDaoImpl();
    private final FollowupDaoImpl followupDao = new FollowupDaoImpl();
    private final CollectDaoImpl collectDao = new CollectDaoImpl();
    private final ConsultationDaoImpl consultationDao = new ConsultationDaoImpl();

    private static final DateTimeFormatter DISPLAY_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @FXML
    public void initialize() {
        HBox backButtonContainer = (HBox) backIcon.getParent();
        backButtonContainer.setOnMouseClicked(event -> closeWindow());
        collectIcon.setOnMouseClicked(event -> toggleCollectStatus());
        askFurtherButton.setOnAction(event -> showAskFurtherInput());
        markUnresolvedButton.setOnAction(event -> markConsultationAsUnresolved());
        sendAskFurtherButton.setOnAction(event -> sendAskFurther());
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
        askFurtherBox.setVisible(false);
        askFurtherBox.setManaged(false);
    }

    public void setConsultation(Consultation consultation) {
        this.currentConsultation = consultation;
        updateUIWithConsultationData();
    }

    public void setOnConsultationUpdated(Runnable onConsultationUpdated) {
        this.onConsultationUpdated = onConsultationUpdated;
    }

    private void updateUIWithConsultationData() {
        if (currentConsultation == null)
            return;
        questionLabel.setText(currentConsultation.getQuestionTitle());
        timeLabel.setText(
                currentConsultation.getQuestionTime() != null
                        ? currentConsultation.getQuestionTime().format(DISPLAY_TIME_FORMATTER)
                        : "");
        // 详细内容（如有）
        contentLabel.setText(""); // 暂时不显示历史记录
        detailContentContainer.setVisible(false);
        detailContentContainer.setManaged(false);
        // 状态
        statusLabel.getStyleClass().clear();
        statusLabel.getStyleClass().add("status-tag");
        String statusDisplayName = currentConsultation.getStatus();
        if ("未回复".equals(statusDisplayName)) {
            statusLabel.setText("未回复");
            statusLabel.getStyleClass().add("status-unanswered");
        } else if ("仍需解决".equals(statusDisplayName)) {
            statusLabel.setText("仍需解决");
            statusLabel.getStyleClass().add("status-unresolved");
        } else if ("已解决".equals(statusDisplayName)) {
            statusLabel.setText("已解决");
            statusLabel.getStyleClass().add("status-resolved");
        }
        consultationIdLabel.setText("编号: " + currentConsultation.getQNumber());
        updateCollectIcon(currentConsultation.isHighlighted());

        // 判断是否本人
        boolean isOwner = currentConsultation.getStudentId() != null &&
                currentConsultation.getStudentId().equals(Main.getCurrentStudentId());
        // 按钮逻辑
        askFurtherButton.setVisible(false);
        askFurtherButton.setManaged(false);
        markUnresolvedButton.setVisible(false);
        markUnresolvedButton.setManaged(false);
        askFurtherBox.setVisible(false);
        askFurtherBox.setManaged(false);
        if (isOwner) {
            if ("已解决".equals(currentConsultation.getStatus())) {
                // 显示红色"仍需解决"按钮
                markUnresolvedButton.setVisible(true);
                markUnresolvedButton.setManaged(true);
                markUnresolvedButton.setText("仍需解决");
                markUnresolvedButton.setStyle("-fx-background-color: #ff4d4d; -fx-text-fill: white;");
                askFurtherButton.setVisible(false);
                askFurtherButton.setManaged(false);
            } else if ("仍需解决".equals(currentConsultation.getStatus())) {
                // 显示紫色"我要追问"按钮
                askFurtherButton.setVisible(true);
                askFurtherButton.setManaged(true);
                askFurtherButton.setText("我要追问");
                askFurtherButton.setStyle(
                        "-fx-background-color: linear-gradient(to right, #667eea, #764ba2); -fx-text-fill: white;");
                markUnresolvedButton.setVisible(false);
                markUnresolvedButton.setManaged(false);
            }
        }
        // 其余情况无按钮
        // 历史内容排序与展示
        loadHistorySorted();
    }

    private void updateCollectIcon(boolean isCollected) {
        try {
            boolean collected = collectDao.isCollected(currentConsultation.getQNumber(), Main.getCurrentStudentId());
            collectIcon.setImage(new Image(getClass().getResourceAsStream(
                    collected ? "/images/collected.png" : "/images/uncollected.png")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void toggleCollectStatus() {
        try {
            boolean isCollected = collectDao.isCollected(currentConsultation.getQNumber(), Main.getCurrentStudentId());
            if (isCollected) {
                collectDao.removeCollect(currentConsultation.getQNumber(), Main.getCurrentStudentId());
            } else {
                collectDao.addCollect(currentConsultation.getQNumber(), Main.getCurrentStudentId());
            }
            updateCollectIcon(!isCollected);
            if (onConsultationUpdated != null)
                onConsultationUpdated.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAskFurtherInput() {
        askFurtherBox.setVisible(true);
        askFurtherBox.setManaged(true);
        askFurtherTextArea.setText("");
        askFurtherCharCountLabel.setText("0/100");
    }

    private void sendAskFurther() {
        String content = askFurtherTextArea.getText().trim();
        if (content.length() < 1 || content.length() > 100) {
            showAlert(Alert.AlertType.ERROR, "内容不规范", "追问内容需为1-100字");
            return;
        }
        boolean success = followupDao.addFollowup(currentConsultation.getQNumber(), content, LocalDateTime.now());
        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "追问成功", "追问已发布！");
            askFurtherBox.setVisible(false);
            askFurtherBox.setManaged(false);
            loadHistorySorted();
            if (onConsultationUpdated != null)
                onConsultationUpdated.run();
        } else {
            showAlert(Alert.AlertType.ERROR, "追问失败", "请稍后重试！");
        }
    }

    private void markConsultationAsUnresolved() {
        // 仅本人且状态为已解决时可用
        if (currentConsultation != null && "已解决".equals(currentConsultation.getStatus())) {
            currentConsultation.setStatus("仍需解决");
            try {
                consultationDao.updateConsultation(currentConsultation);
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "状态变更失败", "数据库更新失败: " + e.getMessage());
                return;
            }
            updateUIWithConsultationData();
            if (onConsultationUpdated != null)
                onConsultationUpdated.run();
        }
    }

    private void loadHistorySorted() {
        historyContainer.getChildren().clear();
        List<HistoryItem> items = new ArrayList<>();
        // 1. 发布咨询
        items.add(new HistoryItem("发布咨询", currentConsultation.getQuestionContent(),
                currentConsultation.getQuestionTime(), null));
        // 2. 回复
        List<Reply> replies = replyDao.getRepliesByQNumber(currentConsultation.getQNumber());
        for (Reply r : replies) {
            items.add(new HistoryItem("已回复", r.getContent(), r.getTime(), r.getResponderName()));
        }
        // 3. 追问
        List<Followup> followups = followupDao.getFollowupsByQNumber(currentConsultation.getQNumber());
        for (Followup f : followups) {
            items.add(new HistoryItem("追问", f.getContent(), f.getTime(), null));
        }
        // 4. 时间升序
        items.sort(Comparator.comparing(HistoryItem::getTime));
        for (HistoryItem item : items) {
            historyContainer.getChildren().add(renderHistoryItem(item));
        }
    }

    private static class HistoryItem {
        String type, content, responder;
        LocalDateTime time;

        HistoryItem(String type, String content, LocalDateTime time, String responder) {
            this.type = type;
            this.content = content;
            this.time = time;
            this.responder = responder;
        }

        public LocalDateTime getTime() {
            return time;
        }
    }

    private VBox renderHistoryItem(HistoryItem item) {
        VBox box = new VBox();
        box.setSpacing(5);
        Label typeLabel = new Label(item.type);
        typeLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #3b82f6;");
        Label contentLabel = new Label(item.content);
        contentLabel.setWrapText(true);
        HBox header = new HBox(typeLabel);
        if (item.responder != null) {
            Label responderLabel = new Label("回复单位: " + item.responder);
            responderLabel.setStyle("-fx-text-fill: #888;");
            header.getChildren().add(responderLabel);
        }
        Label timeLabel = new Label(item.time != null ? item.time.format(DISPLAY_TIME_FORMATTER) : "");
        timeLabel.setStyle("-fx-text-fill: #888;");
        box.getChildren().addAll(header, contentLabel, timeLabel);
        box.setStyle(
                "-fx-background-color: #fff; -fx-padding: 10; -fx-background-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.03), 2, 0, 0, 1);");
        return box;
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void closeWindow() {
        Stage stage = (Stage) questionLabel.getScene().getWindow();
        stage.close();
    }
}