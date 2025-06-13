package ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Separator;
import javafx.scene.control.Alert;
import javafx.scene.Node;
import javafx.scene.text.TextFlow;
import javafx.scene.text.Text;
import dao.ConsultationDao;
import dao.ConsultationDaoImpl;
import dao.CounselorDao;
import dao.CounselorDaoImpl;
import entity.Consultation;
import entity.Counselor;
import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class CounselorMyConsultationsController {

    @FXML
    private TextField searchField;
    @FXML
    private Button unansweredButton;
    @FXML
    private Button unresolvedButton;
    @FXML
    private Button resolvedButton;
    @FXML
    private VBox cardsContainer;

    private List<Consultation> allConsultationsForCounselor;
    private ConsultationDao consultationDao;
    private CounselorDao counselorDao;
    private Counselor currentCounselor;
    private String currentStatusFilter; // 不再有默认值，由按钮点击设置

    public CounselorMyConsultationsController() {
    }

    @FXML
    public void initialize() {
        System.out.println("DEBUG: CounselorMyConsultationsController initialize() 方法被调用。");
        consultationDao = new ConsultationDaoImpl();
        counselorDao = new CounselorDaoImpl();

        // 获取当前登录辅导员信息
        String counselorId = Main.getCurrentCounselorId();
        if (counselorId != null) {
            try {
                currentCounselor = counselorDao.getCounselorById(counselorId);
            } catch (SQLException e) {
                System.err.println("获取辅导员信息失败: " + e.getMessage());
            }
        }

        // 初始化搜索框
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.trim().isEmpty()) {
                refreshConsultations();
            } else {
                filterAndLoadConsultationCards(currentStatusFilter, newValue.trim());
            }
        });

        // 初始化状态按钮
        unansweredButton.setOnAction(event -> filterConsultations("未回复"));
        unresolvedButton.setOnAction(event -> filterConsultations("仍需解决"));
        resolvedButton.setOnAction(event -> filterConsultations("已解决"));

        // 初始化咨询列表（加载所有相关咨询）
        initializeConsultations();
        
        // 模拟点击"未回复"按钮，以应用默认筛选和高亮
        unansweredButton.fire();
    }

    private void initializeConsultations() {
        if (currentCounselor == null) {
            System.out.println("警告: 当前辅导员信息为空，无法初始化咨询列表。");
            allConsultationsForCounselor = java.util.Collections.emptyList();
            return;
        }
        try {
            // 获取该辅导员负责班级下的所有学生学号
            List<String> studentIds = counselorDao.getStudentIdsByCounselorId(currentCounselor.getCounselorId());
            System.out.println("获取到学生ID数量: " + studentIds.size());

            // 根据学生学号列表获取所有相关咨询
            allConsultationsForCounselor = consultationDao.getConsultationsByStudentIds(studentIds);
            System.out.println("成功加载咨询数量: " + allConsultationsForCounselor.size());
        } catch (SQLException e) {
            System.err.println("初始化辅导员咨询失败: " + e.getMessage());
            e.printStackTrace();
            allConsultationsForCounselor = java.util.Collections.emptyList();
        }
    }

    private void refreshConsultations() {
        System.out.println("刷新咨询列表...");
        initializeConsultations(); // 重新从数据库加载所有相关咨询
        filterConsultations(currentStatusFilter); // 应用当前筛选状态
    }

    private void filterConsultations(String status) {
        System.out.println("筛选咨询状态: " + status); // 添加调试日志
        unansweredButton.getStyleClass().removeAll("selected");
        unresolvedButton.getStyleClass().removeAll("selected");
        resolvedButton.getStyleClass().removeAll("selected");

        currentStatusFilter = status; // 更新当前筛选状态

        List<Consultation> filteredList = allConsultationsForCounselor.stream()
                .filter(c -> c.getStatus().equals(status))
                .collect(Collectors.toList());

        loadConsultationCards(filteredList);

        if ("未回复".equals(status)) {
            unansweredButton.getStyleClass().add("selected");
        } else if ("仍需解决".equals(status)) {
            unresolvedButton.getStyleClass().add("selected");
        } else if ("已解决".equals(status)) {
            resolvedButton.getStyleClass().add("selected");
        }
    }

    private void filterAndLoadConsultationCards(String status, String searchText) {
        List<Consultation> filteredAndSearchedList = allConsultationsForCounselor.stream()
                .filter(c -> c.getStatus().equals(status))
                .filter(c -> c.getTitle().toLowerCase().contains(searchText.toLowerCase()) ||
                             c.getContent().toLowerCase().contains(searchText.toLowerCase()))
                .collect(Collectors.toList());
        loadConsultationCards(filteredAndSearchedList);
    }

    private void loadConsultationCards(List<Consultation> consultations) {
        cardsContainer.getChildren().clear();
        System.out.println("准备加载 " + consultations.size() + " 张咨询卡片。");

        if (consultations.isEmpty()) {
            Label noResultsLabel = new Label("暂无相关咨询");
            noResultsLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #888; -fx-padding: 20px;");
            cardsContainer.getChildren().add(noResultsLabel);
            System.out.println("未找到相关咨询，显示'暂无相关咨询'。");
            return;
        }

        for (Consultation consultation : consultations) {
            try {
                cardsContainer.getChildren().add(createConsultationCard(consultation));
                cardsContainer.getChildren().add(createSeparator());
            } catch (IOException e) {
                System.err.println("创建咨询卡片失败: " + e.getMessage());
                e.printStackTrace();
            }
        }
        // 移除最后一个分隔符
        if (!cardsContainer.getChildren().isEmpty()) {
            cardsContainer.getChildren().remove(cardsContainer.getChildren().size() - 1);
        }
        System.out.println("已成功加载 " + cardsContainer.getChildren().size() + " 个UI元素到容器中。");
    }

    private void openConsultationDetail(Consultation consultation) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/counselor_consultation_detail.fxml"));
            Parent detailRoot = loader.load();
            CounselorConsultationDetailController controller = loader.getController();
            controller.setConsultation(consultation, true);

            Stage detailStage = new Stage();
            detailStage.setTitle("咨询详情");
            detailStage.setScene(new Scene(detailRoot));
            detailStage.initModality(Modality.APPLICATION_MODAL);
            detailStage.initOwner(cardsContainer.getScene().getWindow());
            detailStage.setWidth(800);
            detailStage.setHeight(700);
            detailStage.setResizable(false); // 不允许用户调整大小
            controller.setStage(detailStage); // 将Stage传递给控制器
            detailStage.showAndWait();

            refreshConsultations(); // 窗口关闭后刷新列表
        } catch (IOException e) {
            System.err.println("无法加载咨询详情窗口: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "错误", "无法加载咨询详情窗口。");
        }
    }

    private VBox createConsultationCard(Consultation consultation) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/components/consultation_card.fxml"));
        VBox card = loader.load();

        // 获取卡片内的组件
        Label questionLabel = (Label) card.lookup("#questionLabel");
        Label consultationIdLabel = (Label) card.lookup("#consultationIdLabel");
        Label timeLabel = (Label) card.lookup("#timeLabel");
        TextFlow replyContentTextFlow = (TextFlow) card.lookup("#replyContentTextFlow");
        Label statusLabel = (Label) card.lookup("#statusLabel");
        Label categoryTag = (Label) card.lookup("#categoryTag");
        ImageView messageIcon = (ImageView) card.lookup("#messageIcon");
        ImageView featuredIcon = (ImageView) card.lookup("#featuredIcon");

        // 设置值
        questionLabel.setText(consultation.getTitle());
        consultationIdLabel.setText("ID: " + consultation.getQId());
        timeLabel.setText(consultation.getQuestionTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        replyContentTextFlow.getChildren().clear();
        replyContentTextFlow.getChildren().add(new Text(consultation.getContent()));

        // 修正状态文本显示
        String statusText = consultation.getStatus();
        if ("仍需...".equals(statusText)) {
            statusLabel.setText("仍需解决");
        } else {
            statusLabel.setText(statusText);
        }
        String statusStyleClass = getStatusStyleClass(statusLabel.getText());
        if (statusStyleClass != null) {
            statusLabel.getStyleClass().add(statusStyleClass);
        }
        categoryTag.setText(consultation.getCategory());

        updateFeaturedIcon(featuredIcon, consultation.isFeatured());

        // 精选图标点击事件（阻止冒泡）
        featuredIcon.setOnMouseClicked(event -> {
            event.consume(); // 阻止事件冒泡到卡片
            try {
                toggleConsultationFeaturedStatus(consultation);
                updateFeaturedIcon(featuredIcon, consultation.isFeatured());
                refreshConsultations();
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "操作失败", "切换加精状态失败: " + e.getMessage());
                e.printStackTrace();
            }
        });

        // 只有点击卡片非精选图标区域才弹出详情
        card.setOnMouseClicked(event -> {
            if (event.getTarget() == featuredIcon) return;
            openConsultationDetail(consultation);
        });

        // 留言图标点击事件 (TODO: 实现跳转到咨询详情页)
        messageIcon.setOnMouseClicked(event -> {
            System.out.println("点击留言图标，咨询ID: " + consultation.getQId());
            try {
                FXMLLoader detailLoader = new FXMLLoader(getClass().getResource("/ui/consultation_detail.fxml"));
                Parent detailRoot = detailLoader.load();
                ConsultationDetailController controller = detailLoader.getController();
                controller.setConsultation(consultation);
                controller.setOnConsultationUpdated(this::refreshConsultations); // 设置回调，详情页更新后刷新列表

                Stage detailStage = new Stage();
                detailStage.setTitle("咨询详情");
                detailStage.setScene(new Scene(detailRoot));
                detailStage.initModality(Modality.APPLICATION_MODAL);
                detailStage.initOwner(cardsContainer.getScene().getWindow());
                detailStage.setWidth(800);
                detailStage.setHeight(700);
                detailStage.showAndWait();
            } catch (IOException e) {
                System.err.println("打开咨询详情页面失败: " + e.getMessage());
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "错误", "无法打开咨询详情页面。");
            }
        });

        return card;
    }

    private String getStatusStyleClass(String status) {
        switch (status) {
            case "未回复":
                return "status-unanswered";
            case "仍需解决":
                return "status-unresolved";
            case "已解决":
                return "status-resolved";
            default:
                return null;
        }
    }

    private void updateFeaturedIcon(ImageView icon, boolean isFeatured) {
        if (isFeatured) {
            icon.setImage(new Image(getClass().getResourceAsStream("/images/choosen.png"))); // 填充星形
            icon.getStyleClass().add("selected"); // 添加选中样式
        } else {
            icon.setImage(new Image(getClass().getResourceAsStream("/images/unchoosen.png"))); // 空心星形
            icon.getStyleClass().remove("selected"); // 移除选中样式
        }
    }

    private void toggleConsultationFeaturedStatus(Consultation consultation) throws SQLException {
        consultation.setFeatured(!consultation.isFeatured());
        consultationDao.updateConsultation(consultation);
    }

    private Separator createSeparator() {
        Separator separator = new Separator();
        separator.getStyleClass().add("card-separator");
        return separator;
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 