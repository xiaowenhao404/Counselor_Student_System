package ui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import java.io.IOException;

public class NewConsultationController {
    @FXML
    private HBox navButtons;
    
    @FXML
    private TextField searchField;
    
    @FXML
    private TextArea questionArea;
    
    @FXML
    public void initialize() {
        // 初始化导航按钮点击事件
        navButtons.getChildren().forEach(node -> {
            if (node instanceof Button) {
                Button button = (Button) node;
                button.setOnAction(event -> {
                    try {
                        String fxmlPath = button.getText().equals("大厅") ? 
                            "/ui/student_main.fxml" : "/ui/my_consultations.fxml";
                        Scene scene = new Scene(FXMLLoader.load(getClass().getResource(fxmlPath)));
                        Stage stage = (Stage) button.getScene().getWindow();
                        stage.setScene(scene);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }
    
    @FXML
    private void handleSubmit() {
        String question = questionArea.getText().trim();
        if (question.isEmpty()) {
            showAlert("提示", "请输入问题描述");
            return;
        }
        
        // TODO: 获取选中的类别
        String category = "学习"; // 默认类别
        
        // TODO: 保存咨询信息到数据库
        
        // 提交成功后返回我的咨询界面
        try {
            Scene scene = new Scene(FXMLLoader.load(getClass().getResource("/ui/my_consultations.fxml")));
            Stage stage = (Stage) questionArea.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 