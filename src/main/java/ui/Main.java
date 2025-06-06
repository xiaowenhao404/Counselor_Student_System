package ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // 加载登录界面FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/login.fxml"));
        Parent root = loader.load();

        // 加载CSS样式
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/ui/login.css").toExternalForm());

        // 设置窗口标题和大小
        primaryStage.setTitle("学生信息交流管理系统");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}