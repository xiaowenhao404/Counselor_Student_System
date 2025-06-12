package ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import dao.ConsultationDaoImpl;
import entity.Consultation;

public class NewConsultationController {

    @FXML
    private TextField questionField;
    @FXML
    private Label questionCountLabel;
    @FXML
    private TextArea contentArea;
    @FXML
    private Label contentHintLabel;
    @FXML
    private Label contentCountLabel;
    @FXML
    private HBox tagButtonsContainer;
    @FXML
    private Button tagStudyButton;
    @FXML
    private Button tagLifeButton;
    @FXML
    private Button tagOtherButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Button publishButton;

    private Button selectedTagButton = null;
    private ConsultationDaoImpl consultationDao = new ConsultationDaoImpl();

    // 这里需要获取当前登录学生的学号，实际项目中应通过Session或全局变量传递
    private String currentStudentId = "202383290001"; // TODO: 替换为真实登录学生学号

    @FXML
    public void initialize() {
        // 问题输入框监听
        questionField.textProperty().addListener((observable, oldValue, newValue) -> {
            int len = newValue.length();
            questionCountLabel.setText(len + "/20");
            if (len > 20) {
                questionCountLabel.getStyleClass().add("error");
            } else {
                questionCountLabel.getStyleClass().remove("error");
            }
        });

        // 内容输入框监听
        contentArea.textProperty().addListener((observable, oldValue, newValue) -> {
            int len = newValue.length();
            contentCountLabel.setText(len + "/100");
            if (len > 100 || len == 0) {
                contentCountLabel.getStyleClass().add("error");
                contentHintLabel.setVisible(true);
            } else {
                contentCountLabel.getStyleClass().remove("error");
                contentHintLabel.setVisible(false);
            }
        });

        // 标签按钮点击事件
        List<Button> tagButtons = new ArrayList<>();
        tagButtons.add(tagStudyButton);
        tagButtons.add(tagLifeButton);
        tagButtons.add(tagOtherButton);

        for (Button button : tagButtons) {
            button.setOnAction(event -> {
                if (selectedTagButton != null) {
                    selectedTagButton.getStyleClass().remove("selected");
                }
                selectedTagButton = button;
                selectedTagButton.getStyleClass().add("selected");
            });
        }
    }

    @FXML
    private void handleBackToList() {
        closeWindow();
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    @FXML
    private void handlePublish() {
        String questionTitle = questionField.getText();
        String content = contentArea.getText();

        // 验证
        if (questionTitle.isEmpty()) {
            showAlert(AlertType.ERROR, "输入错误", "问题不能为空。");
            return;
        }
        if (questionTitle.length() > 20) {
            showAlert(AlertType.ERROR, "输入错误", "问题长度不能超过20个字符。");
            return;
        }
        if (content.isEmpty() || content.length() > 100) {
            showAlert(AlertType.ERROR, "输入错误", "详细提问内容不能为空，且长度不能超过100个字符。");
            return;
        }
        if (selectedTagButton == null) {
            showAlert(AlertType.ERROR, "输入错误", "请选择一个咨询标签。");
            return;
        }

        String selectedTag = selectedTagButton.getText();
        String category = selectedTag; // "学习"、"生活"、"其他"
        String status = "未回复";
        boolean highlighted = false;
        LocalDateTime now = LocalDateTime.now();
        String qNumber = generateQNumber();

        Consultation consultation = new Consultation();
        consultation.setQNumber(qNumber);
        consultation.setStudentId(currentStudentId);
        consultation.setCategory(category);
        consultation.setStatus(status);
        consultation.setQuestionTitle(questionTitle);
        consultation.setQuestionTime(now);
        consultation.setHighlighted(highlighted);
        consultation.setQuestionContent(content);
        // 其余字段如学生姓名、回复数等可后续补充

        try {
            boolean success = consultationDao.addConsultation(consultation);
            if (success) {
                showAlert(AlertType.INFORMATION, "成功", "新咨询发起成功！");
                closeWindow();
            } else {
                showAlert(AlertType.ERROR, "失败", "新咨询提交失败，请重试。");
            }
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "异常", "数据库操作异常: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String generateQNumber() {
        // 简单生成Q编号，实际应根据数据库已有编号生成
        return "Q" + System.currentTimeMillis();
    }

    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void closeWindow() {
        Stage stage = (Stage) questionField.getScene().getWindow();
        stage.close();
    }
}