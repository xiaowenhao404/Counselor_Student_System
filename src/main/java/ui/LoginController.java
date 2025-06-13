package ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.event.ActionEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;
import service.AuthService;

public class LoginController {
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField showPasswordField;

    @FXML
    private ImageView passwordVisibilityIcon;

    @FXML
    private Button loginButton;

    @FXML
    private Button studentRoleButton;

    @FXML
    private Button counselorRoleButton;

    @FXML
    private Button adminRoleButton;

    @FXML
    private ImageView usernameIcon;

    private String selectedRole = null;
    private int loginAttempts = 3;
    private boolean passwordVisible = false;
    private AuthService authService;

    @FXML
    public void initialize() {
        // 默认选中学生角色并更新图标
        handleRoleSelect(new ActionEvent(studentRoleButton, null));

        // 添加文本同步监听器
        passwordField.textProperty().bindBidirectional(showPasswordField.textProperty());

        // 初始化 AuthService
        authService = new AuthService();
    }

    @FXML
    private void handleRoleSelect(ActionEvent event) {
        // 移除所有按钮的选中样式
        studentRoleButton.getStyleClass().remove("selected");
        counselorRoleButton.getStyleClass().remove("selected");
        adminRoleButton.getStyleClass().remove("selected");

        // 根据点击的按钮设置选中样式和更新图标
        Button clickedButton = (Button) event.getSource();
        clickedButton.getStyleClass().add("selected");

        if (clickedButton == studentRoleButton) {
            selectedRole = "学生";
            usernameIcon.setImage(new Image(getClass().getResourceAsStream("/images/login_student.png")));
        } else if (clickedButton == counselorRoleButton) {
            selectedRole = "辅导员";
            usernameIcon.setImage(new Image(getClass().getResourceAsStream("/images/login_teacher.png")));
        } else if (clickedButton == adminRoleButton) {
            selectedRole = "管理员";
            usernameIcon.setImage(new Image(getClass().getResourceAsStream("/images/login_admin.png")));
        }
    }

    @FXML
    private void togglePasswordVisibility(MouseEvent event) {
        if (passwordVisible) {
            passwordField.setVisible(true);
            showPasswordField.setVisible(false);
            passwordVisibilityIcon.setImage(new Image(getClass().getResourceAsStream("/images/invisible.png")));
        } else {
            passwordField.setVisible(false);
            showPasswordField.setVisible(true);
            passwordVisibilityIcon.setImage(new Image(getClass().getResourceAsStream("/images/visible.png")));
        }
        passwordVisible = !passwordVisible;
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordVisible ? showPasswordField.getText() : passwordField.getText();

        // 验证输入
        if (selectedRole == null || username.isEmpty() || password.isEmpty()) {
            showError("请选择角色并填写完整信息");
            return;
        }

        // 根据选择的角色进行验证
        AuthService.LoginResult result = null;
        switch (selectedRole) {
            case "学生":
                result = authService.login(username, password, AuthService.UserType.STUDENT);
                break;
            case "辅导员":
                result = authService.login(username, password, AuthService.UserType.COUNSELOR);
                break;
            case "管理员":
                result = authService.login(username, password, AuthService.UserType.ADMIN);
                break;
        }

        if (result != null && result.isSuccess()) {
            switch (result.getUserType()) {
                case STUDENT:
                    Main.setCurrentStudentId(username);
                    Main.setCurrentCounselorId(null);
                    showSuccess("学生登录成功！欢迎，" + result.getUserName() + "！");
                    try {
                        openStudentMainWindow();
                    } catch (Exception e) {
                        showError("打开学生界面失败：" + e.getMessage());
                    }
                    break;
                case COUNSELOR:
                    Main.setCurrentCounselorId(username);
                    Main.setCurrentStudentId(null);
                    showSuccess("辅导员登录成功！欢迎，" + result.getUserName() + "！");
                    try {
                        openCounselorMainWindow();
                    } catch (Exception e) {
                        showError("打开辅导员界面失败：" + e.getMessage());
                    }
                    break;
                case ADMIN:
                    showSuccess("管理员登录成功！");
                    try {
                        openAdminMainWindow();
                    } catch (Exception e) {
                        showError("打开管理员界面失败：" + e.getMessage());
                    }
                    break;
            }
        } else {
            handleLoginFailure();
        }
    }

    private void handleLoginFailure() {
        loginAttempts--;
        if (loginAttempts > 0) {
            showError("账号或密码错误，还剩 " + loginAttempts + " 次尝试机会");
        } else {
            showError("超过最大尝试次数，程序将关闭");
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.close();
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("错误");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("成功");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void openStudentMainWindow() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/student_main.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/ui/student_main.css").toExternalForm());

        Stage stage = new Stage();
        stage.setTitle("学生主界面 - 辅导员学生交流信息管理系统");
        stage.setScene(scene);
        stage.setWidth(1200);
        stage.setHeight(800);
        stage.setResizable(false);
        stage.show();

        // 关闭登录窗口
        Stage loginStage = (Stage) loginButton.getScene().getWindow();
        loginStage.close();
    }

    private void openCounselorMainWindow() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/counselor_main.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/ui/counselor_main.css").toExternalForm());

        Stage stage = new Stage();
        stage.setTitle("辅导员主界面 - 辅导员学生交流信息管理系统");
        stage.setScene(scene);
        stage.setWidth(1200);
        stage.setHeight(800);
        stage.setResizable(false);
        stage.show();

        // 关闭登录窗口
        Stage loginStage = (Stage) loginButton.getScene().getWindow();
        loginStage.close();
    }

    private void openAdminMainWindow() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/admin_main.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/ui/admin_main.css").toExternalForm());

        Stage stage = new Stage();
        stage.setTitle("管理员主界面 - 辅导员学生交流信息管理系统");
        stage.setScene(scene);
        stage.setWidth(1200);
        stage.setHeight(800);
        stage.setResizable(false);
        stage.show();

        // 关闭登录窗口
        Stage loginStage = (Stage) loginButton.getScene().getWindow();
        loginStage.close();
    }
}