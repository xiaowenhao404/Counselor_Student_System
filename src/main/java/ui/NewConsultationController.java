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
        String question = questionField.getText();
        String content = contentArea.getText();

        // 验证
        if (question.isEmpty()) {
            showAlert(AlertType.ERROR, "输入错误", "问题不能为空。");
            return;
        }
        if (question.length() > 20) {
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

        // 模拟数据提交
        String selectedTag = selectedTagButton.getText();
        // 实际应用中，这里会调用后端接口保存数据
        System.out.println("新咨询发布：");
        System.out.println("问题: " + question);
        System.out.println("内容: " + content);
        System.out.println("标签: " + selectedTag);
        System.out.println("状态: 未回复"); // 默认状态
        System.out.println("是否加精: 0"); // 默认不加精

        showAlert(AlertType.INFORMATION, "成功", "新咨询发起成功！");
        closeWindow();
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