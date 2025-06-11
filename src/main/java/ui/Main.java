package ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // 直接加载咨询大厅界面
        Parent root = FXMLLoader.load(getClass().getResource("/ui/student_main.fxml"));
        String title = "辅导员学生交流系统";

        // 创建场景（样式表已在FXML中引用）
        Scene scene = new Scene(root);
        scene.setFill(javafx.scene.paint.Color.WHITE);

        // 设置窗口标题和大小
        primaryStage.setTitle(title);
        primaryStage.setScene(scene);
        primaryStage.setWidth(1200);
        primaryStage.setHeight(800);
        primaryStage.setResizable(true);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}