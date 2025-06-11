package ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class StudentMainController {
    @FXML
    private HBox navButtons;
    
    @FXML
    private TextField searchField;
    
    @FXML
    private VBox navButtonsContainer;
    
    @FXML
    private Button newConsultationButton;
    
    @FXML
    private HBox categoryBar;
    
    @FXML
    public void initialize() {
        // 初始化导航按钮点击事件
        navButtons.getChildren().forEach(node -> {
            if (node instanceof Button) {
                Button button = (Button) node;
                button.setOnAction(event -> {
                    navButtons.getChildren().forEach(btn -> {
                        if (btn instanceof Button) {
                            btn.getStyleClass().remove("selected");
                        }
                    });
                    button.getStyleClass().add("selected");
                });
            }
        });
        
        // 初始化左侧导航按钮点击事件
        navButtonsContainer.getChildren().forEach(node -> {
            if (node instanceof Button) {
                Button button = (Button) node;
                button.setOnAction(event -> {
                    navButtonsContainer.getChildren().forEach(btn -> {
                        if (btn instanceof Button) {
                            btn.getStyleClass().remove("selected");
                        }
                    });
                    button.getStyleClass().add("selected");
                });
            }
        });
        
        // 初始化类别按钮点击事件
        categoryBar.getChildren().forEach(node -> {
            if (node instanceof Button) {
                Button button = (Button) node;
                button.setOnAction(event -> {
                    categoryBar.getChildren().forEach(btn -> {
                        if (btn instanceof Button) {
                            btn.getStyleClass().remove("selected");
                        }
                    });
                    button.getStyleClass().add("selected");
                });
            }
        });
        
        // 初始化"我要咨询"按钮点击事件
        newConsultationButton.setOnAction(event -> {
            // TODO: 跳转到新建咨询界面
        });
    }
} 