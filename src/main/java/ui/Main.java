package ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // 获取命令行参数
        String view = getParameters().getRaw().isEmpty() ? "login" : getParameters().getRaw().get(0);

        Parent root;
        String title;

        switch (view.toLowerCase()) {
            case "login":
                // 加载登录界面FXML
                root = FXMLLoader.load(getClass().getResource("/ui/login.fxml"));
                title = "登录界面";
                break;
            // 在这里添加其他界面的case
            default:
                root = FXMLLoader.load(getClass().getResource("/ui/login.fxml"));
                title = "登录界面";
        }

        // 创建场景（样式表已在FXML中引用）
        Scene scene = new Scene(root);
        scene.setFill(javafx.scene.paint.Color.WHITE);

        // 设置窗口标题和大小
        primaryStage.setTitle(title);
        primaryStage.setScene(scene);
        primaryStage.setWidth(450);
        primaryStage.setHeight(650);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}