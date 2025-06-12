import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        // Load the MyConsultations FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/my_consultations.fxml"));
        Parent root = loader.load();

        primaryStage.setTitle("辅导员学生交流系统——我的咨询");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
} 