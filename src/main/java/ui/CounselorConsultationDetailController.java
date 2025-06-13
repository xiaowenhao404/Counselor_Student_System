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
import dao.ReplyDao;
import dao.ReplyDaoImpl;
import dao.FollowupDaoImpl;
import dao.ConsultationDaoImpl;
import entity.Reply;
import entity.Followup;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.geometry.Insets;

public class CounselorConsultationDetailController {

    @FXML
    private Label questionLabel;
    @FXML
    private Label statusLabel;
    @FXML
    private VBox historyContainer;
    @FXML
    private TextArea replyTextArea; // 回复输入框
    @FXML
    private Button sendReplyButton; // 发送回复按钮
    @FXML
    private Label replyCharCountLabel; // 回复字数统计
    @FXML
    private Label studentNameLabel;
    @FXML
    private Label studentIdLabel;
    @FXML
    private Label typeLabel;
    @FXML
    private Label submitTimeLabel;
    @FXML
    private VBox replySection; // 添加新的 @FXML 变量

    private Consultation consultation;
    private Runnable onConsultationUpdated; // 用于通知父级列表刷新
    private String currentCounselorId = Main.getCurrentCounselorId();

    private ReplyDao replyDao;
    private final FollowupDaoImpl followupDao = new FollowupDaoImpl();
    private final ConsultationDaoImpl consultationDao = new ConsultationDaoImpl();

    private static final DateTimeFormatter DISPLAY_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private Stage stage;

    private boolean fromHall = false;
    public void setFromHall(boolean fromHall) {
        this.fromHall = fromHall;
    }

    @FXML
    public void initialize() {
        replyDao = new ReplyDaoImpl();

        // 回复文本框字数监听
        replyTextArea.textProperty().addListener((observable, oldValue, newValue) -> {
            int charCount = newValue.length();
            replyCharCountLabel.setText(charCount + "/200");
            if (charCount > 200) {
                replyTextArea.getStyleClass().add("error-border");
                replyCharCountLabel.getStyleClass().add("error"); // 使用error样式类
            } else {
                replyTextArea.getStyleClass().remove("error-border");
                replyCharCountLabel.getStyleClass().remove("error");
            }
        });

        // 发送回复按钮点击事件
        sendReplyButton.setOnAction(event -> sendReply());
    }

    public void setConsultation(Consultation consultation, boolean showReplySection) {
        this.consultation = consultation;
        updateUI();
        loadHistorySorted();

        // 根据参数控制回复区域的可见性
        replySection.setVisible(showReplySection);
        replySection.setManaged(showReplySection);
    }

    public void setOnConsultationUpdated(Runnable onConsultationUpdated) {
        this.onConsultationUpdated = onConsultationUpdated;
    }

    private void updateUI() {
        studentNameLabel.setText(consultation.getStudentName());
        studentIdLabel.setText(consultation.getStudentId());
        typeLabel.setText(consultation.getCategory());
        statusLabel.setText(consultation.getStatus());
        submitTimeLabel.setText(consultation.getQuestionTime().format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        updateUIWithConsultationData();
    }

    private void updateUIWithConsultationData() {
        if (consultation == null)
            return;
        questionLabel.setText(consultation.getQuestionTitle());
        // 移除 timeLabel 的设置，因为 submitTimeLabel 已经设置了提交时间
        // timeLabel.setText(
        //         consultation.getQuestionTime() != null
        //                 ? consultation.getQuestionTime().format(DISPLAY_TIME_FORMATTER)
        //                 : "");
        // 移除 contentLabel 的设置
        // contentLabel.setText(consultation.getQuestionContent()); // 显示最初的提问内容
        // 移除 detailContentContainer 的设置
        // detailContentContainer.setVisible(true);
        // detailContentContainer.setManaged(true);

        // 状态
        statusLabel.getStyleClass().clear();
        statusLabel.getStyleClass().add("status-tag");
        String statusDisplayName = consultation.getStatus();
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

        // 历史内容排序与展示
        loadHistorySorted();
    }

    public void sendReply() {
        String content = replyTextArea.getText().trim();
        if (content.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "提交失败", "回复内容不能为空！");
            return;
        }
        if (content.length() > 200) {
            showAlert(Alert.AlertType.WARNING, "提交失败", "回复内容不能超过200字！");
            return;
        }

        try {
            Reply reply = new Reply();
            reply.setRNumber("R" + (System.currentTimeMillis() % 100000000));
            reply.setQNumber(consultation.getQNumber());
            reply.setContent(content);
            reply.setTime(LocalDateTime.now());

            boolean success = replyDao.addReply(reply);
            if (!success) {
                showAlert(Alert.AlertType.ERROR, "提交失败", "数据库插入失败！");
                return;
            }

            // 只在初始状态为"未回复"时，才更新为"仍需解决"；"仍需解决"保持不变
            if ("未回复".equals(consultation.getStatus())) {
                consultation.setStatus("仍需解决");
                consultationDao.updateConsultation(consultation);
            }

            showAlert(Alert.AlertType.INFORMATION, "提交成功", "回复已成功提交！");
            replyTextArea.clear();
            replyCharCountLabel.setText("0/200");
            loadHistorySorted();

            if (onConsultationUpdated != null) {
                onConsultationUpdated.run();
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "提交失败", "提交回复失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadHistorySorted() {
        List<Reply> replies = replyDao.getRepliesByQNumber(consultation.getQNumber());
        List<Followup> followups = followupDao.getFollowupsByQNumber(consultation.getQNumber());

        List<HistoryItem> historyItems = new ArrayList<>();
        historyItems.add(new HistoryItem("发布咨询", consultation.getQuestionContent(),
                consultation.getQuestionTime(), consultation.getStudentName(), "question"));

        for (Reply reply : replies) {
            historyItems.add(new HistoryItem("已回复", reply.getContent(), reply.getTime(),
                    reply.getResponderName(), "reply"));
        }

        for (Followup followup : followups) {
            historyItems.add(new HistoryItem("追问", followup.getContent(),
                    followup.getTime(), consultation.getStudentName(), "followup"));
        }

        historyItems.sort(Comparator.comparing(HistoryItem::getTime));

        historyContainer.getChildren().clear(); // 清空原有内容

        for (HistoryItem item : historyItems) {
            historyContainer.getChildren().add(renderHistoryItem(item));
        }
    }

    private VBox renderHistoryItem(HistoryItem item) {
        VBox itemBox = new VBox(3);
        itemBox.setPadding(new Insets(10, 10, 10, 10));
        itemBox.getStyleClass().add("history-item");

        // 第一行：类型标签
        Label typeLabel = new Label(item.type);
        typeLabel.getStyleClass().add("history-type-label");

        // 第二行：内容
        Label contentLabel = new Label(item.content);
        contentLabel.getStyleClass().add("history-content-label");
        contentLabel.setWrapText(true);

        // 第三行：时间
        Label timeLabel = new Label(item.getTime().format(DISPLAY_TIME_FORMATTER));
        timeLabel.getStyleClass().add("history-time-label");

        itemBox.getChildren().addAll(typeLabel, contentLabel, timeLabel);
        return itemBox;
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private static class HistoryItem {
        String type;
        String content;
        LocalDateTime time;
        String responder;
        String itemType;

        public HistoryItem(String type, String content, LocalDateTime time, String responder, String itemType) {
            this.type = type;
            this.content = content;
            this.time = time;
            this.responder = responder;
            this.itemType = itemType;
        }

        public LocalDateTime getTime() {
            return time;
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
} 