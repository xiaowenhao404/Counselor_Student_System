package ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    private static Stage primaryStage;
    private static String currentStudentId;
    private static String currentCounselorId;

    public static String getCurrentStudentId() {
        return currentStudentId;
    }

    public static void setCurrentStudentId(String studentId) {
        currentStudentId = studentId;
    }

    public static String getCurrentCounselorId() {
        return currentCounselorId;
    }

    public static void setCurrentCounselorId(String counselorId) {
        currentCounselorId = counselorId;
    }

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;

        // 加载登录界面
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/ui/login.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(Main.class.getResource("/ui/login.css").toExternalForm());

        primaryStage.setTitle("辅导员学生交流系统——登录");
        primaryStage.setScene(scene);

        // 让窗口根据内容自适应
        primaryStage.sizeToScene();
        primaryStage.setResizable(false);

        primaryStage.show();
    }

    public static void loadScene(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource(fxmlPath));
            Parent root = loader.load();
            Scene scene = new Scene(root);

            // 根据不同的界面加载对应的CSS
            if (fxmlPath.contains("login")) {
                scene.getStylesheets().add(Main.class.getResource("/ui/login.css").toExternalForm());
            } else if (fxmlPath.contains("student_main")) {
                scene.getStylesheets().add(Main.class.getResource("/ui/student_main.css").toExternalForm());
            } else if (fxmlPath.contains("counselor_main")) {
                scene.getStylesheets().add(Main.class.getResource("/ui/counselor_main.css").toExternalForm());
            } else if (fxmlPath.contains("admin_main")) {
                scene.getStylesheets().add(Main.class.getResource("/ui/admin_main.css").toExternalForm());
            }

            primaryStage.setScene(scene);
            System.out.println("切换到: " + fxmlPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}