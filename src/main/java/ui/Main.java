package ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        primaryStage.setWidth(1200);
        primaryStage.setHeight(800);
        primaryStage.setResizable(false);

        // 默认加载学生端主界面
        loadScene("/ui/student_main.fxml");
        primaryStage.setTitle("辅导员学生交流系统——学生端");
        primaryStage.show();
    }

    public static void loadScene(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource(fxmlPath));
            Parent root = loader.load();
            // StudentMainController controller = loader.getController(); // 如果需要访问控制器实例，可以在这里获取
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Main.class.getResource("/ui/student_main.css").toExternalForm()); // 确保CSS被加载
            primaryStage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}