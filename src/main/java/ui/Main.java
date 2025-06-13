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
            System.out.println("正在加载场景: " + fxmlPath);
            System.out.println("DEBUG: Stage Hash Code (before load): " + primaryStage.hashCode());
            if (primaryStage.getScene() != null) {
                System.out.println("DEBUG: Current Scene Hash Code (before load): " + primaryStage.getScene().hashCode());
            } else {
                System.out.println("DEBUG: Current Scene is null (before load).");
            }
            
            // 检查FXML文件是否存在
            if (Main.class.getResource(fxmlPath) == null) {
                System.err.println("错误：无法找到FXML文件: " + fxmlPath);
                return;
            }
            
            FXMLLoader loader = new FXMLLoader(Main.class.getResource(fxmlPath));
            Parent root = loader.load();
            Scene scene = new Scene(root);

            System.out.println("DEBUG: New Scene Hash Code (after creation): " + scene.hashCode());

            // 根据不同的界面加载对应的CSS
            String cssPath = null;
            if (fxmlPath.contains("login")) {
                cssPath = "/ui/login.css";
            } else if (fxmlPath.contains("student_main")) {
                cssPath = "/ui/student_main.css";
            } else if (fxmlPath.contains("counselor_main") || fxmlPath.contains("counselor_my_consultations")) {
                cssPath = "/ui/counselor_main.css";
            } else if (fxmlPath.contains("admin_main")) {
                cssPath = "/ui/admin_main.css";
            }

            if (cssPath != null) {
                if (Main.class.getResource(cssPath) != null) {
                    scene.getStylesheets().add(Main.class.getResource(cssPath).toExternalForm());
                } else {
                    System.err.println("警告：找不到CSS文件: " + cssPath);
                }
            }

            primaryStage.setScene(scene);
            System.out.println("成功加载场景: " + fxmlPath);
            System.out.println("DEBUG: Stage Hash Code (after setScene): " + primaryStage.hashCode());
            System.out.println("DEBUG: New Current Scene Hash Code (after setScene): " + primaryStage.getScene().hashCode());
            if (primaryStage.getScene() != null && primaryStage.getScene().getRoot() != null) {
                System.out.println("DEBUG: primaryStage 当前场景根节点ID: " + primaryStage.getScene().getRoot().getId());
            } else {
                System.out.println("DEBUG: primaryStage 当前场景根节点为 null 或其根节点为 null。");
            }

        } catch (Exception e) {
            System.err.println("加载场景时发生错误: " + fxmlPath);
            System.err.println("错误详情: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}