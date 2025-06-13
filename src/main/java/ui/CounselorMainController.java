package ui;

import dao.ConsultationDao;
import dao.ConsultationDaoImpl;
import dao.CounselorDao;
import dao.CounselorDaoImpl;
import entity.Consultation;
import entity.Counselor;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class CounselorMainController implements Initializable {
    private enum FilterType {
        ALL, FEATURED, CATEGORY
    }

    private FilterType currentFilter = FilterType.ALL;

    @FXML private Button hallButton;
    @FXML private Button myConsultationsButton;
    @FXML private TextField searchField;
    @FXML private Button allButton;
    @FXML private Button featuredButton;
    @FXML private Button allCategoriesButton;
    @FXML private Button studyButton;
    @FXML private Button lifeButton;
    @FXML private Button otherButton;
    @FXML private VBox cardsContainer;
    @FXML private HBox categoryBar;
    @FXML private VBox consultationList;
    @FXML private Button logoutButton;

    private ConsultationDao consultationDao;
    private CounselorDao counselorDao;
    private Counselor currentCounselor;
    private List<Consultation> currentDisplayedConsultations;
    private Button currentSelectedLeftNavButton;
    private Button currentSelectedCategoryButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        consultationDao = new ConsultationDaoImpl();
        counselorDao = new CounselorDaoImpl();

        // 获取当前登录辅导员信息
        String counselorId = Main.getCurrentCounselorId();
        if (counselorId != null) {
            try {
                currentCounselor = counselorDao.getCounselorById(counselorId);
            } catch (SQLException e) {
                System.err.println("获取辅导员信息失败: " + e.getMessage());
                e.printStackTrace();
            }
        }

        initializeNavButtons();
        initializeLeftNavButtons();
        initializeCategoryButtons();
        initializeSearchField();
        
        // 设置退出登录按钮事件
        logoutButton.setOnAction(event -> handleLogout());
        
        // 默认选中"大厅"按钮和"全部"按钮
        selectNavButton(hallButton);
        selectLeftNavButton(allButton);
        showHall(); // 默认显示大厅内容
    }

    private void initializeNavButtons() {
        hallButton.setOnAction(e -> {
            System.out.println("点击大厅按钮");
            selectNavButton(hallButton);
            Main.loadScene("/ui/counselor_main.fxml");
            // 重新初始化大厅界面的筛选状态
            selectLeftNavButton(allButton);
            selectCategoryButton(allCategoriesButton);
            loadAllConsultations();
        });
        myConsultationsButton.setOnAction(e -> {
            System.out.println("点击我的答疑按钮 - 准备打开模态窗口。");
            selectNavButton(myConsultationsButton);
            // 弹出新窗口
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/counselor_my_consultations.fxml"));
                Parent myConsultationsRoot = loader.load();

                Stage modalStage = new Stage();
                modalStage.setTitle("我的答疑");
                modalStage.setScene(new Scene(myConsultationsRoot));
                modalStage.initModality(Modality.APPLICATION_MODAL); // 设置为模态窗口
                modalStage.initOwner(hallButton.getScene().getWindow()); // 设置父窗口
                // 可以根据需要设置窗口大小
                modalStage.setWidth(1000);
                modalStage.setHeight(800);
                modalStage.showAndWait(); // 显示并等待关闭
                System.out.println("我的答疑模态窗口已关闭。");
                // 模态窗口关闭后，将顶部导航按钮的选中状态切换回"大厅"
                selectNavButton(hallButton);
            } catch (IOException ex) {
                System.err.println("打开我的答疑模态窗口失败: " + ex.getMessage());
                ex.printStackTrace();
            }
        });
    }

    private void selectNavButton(Button selectedButton) {
        System.out.println("选择导航按钮: " + selectedButton.getText());
        // 移除所有按钮的选中状态
        hallButton.getStyleClass().remove("selected");
        myConsultationsButton.getStyleClass().remove("selected");
        
        // 添加选中状态到当前按钮
        selectedButton.getStyleClass().add("selected");
    }

    private void initializeLeftNavButtons() {
        allButton.setOnAction(e -> {
            selectLeftNavButton(allButton);
            currentFilter = FilterType.ALL;
            loadFilteredConsultations();
        });
        featuredButton.setOnAction(e -> {
            selectLeftNavButton(featuredButton);
            currentFilter = FilterType.FEATURED;
            loadFilteredConsultations();
        });
    }

    private void selectLeftNavButton(Button selectedButton) {
        // 移除所有按钮的选中状态
        allButton.getStyleClass().remove("selected");
        featuredButton.getStyleClass().remove("selected");
        
        // 添加选中状态到当前按钮
        selectedButton.getStyleClass().add("selected");
        currentSelectedLeftNavButton = selectedButton;
    }

    private void initializeCategoryButtons() {
        allCategoriesButton.setOnAction(e -> {
            selectCategoryButton(allCategoriesButton);
            currentFilter = FilterType.ALL; // 类别筛选中的"全部"也对应所有咨询
            loadFilteredConsultations();
        });
        studyButton.setOnAction(e -> {
            selectCategoryButton(studyButton);
            currentFilter = FilterType.CATEGORY;
            filterConsultationsByCategory("学习");
        });
        lifeButton.setOnAction(e -> {
            selectCategoryButton(lifeButton);
            currentFilter = FilterType.CATEGORY;
            filterConsultationsByCategory("生活");
        });
        otherButton.setOnAction(e -> {
            selectCategoryButton(otherButton);
            currentFilter = FilterType.CATEGORY;
            filterConsultationsByCategory("其他");
        });

        // 默认选中全部类别按钮
        selectCategoryButton(allCategoriesButton);
    }

    private void selectCategoryButton(Button selectedButton) {
        if (currentSelectedCategoryButton != null) {
            currentSelectedCategoryButton.getStyleClass().remove("selected");
        }
        selectedButton.getStyleClass().add("selected");
        currentSelectedCategoryButton = selectedButton;
    }

    private void initializeSearchField() {
        searchField.setOnAction(e -> performSearch());
    }

    // 新增方法：加载所有咨询并存储，用于后续筛选
    private void loadAllConsultations() {
        try {
            // 加载所有咨询
            currentDisplayedConsultations = consultationDao.getAllConsultations();
            System.out.println("成功加载所有咨询数量: " + currentDisplayedConsultations.size());
            loadFilteredConsultations(); // 默认显示所有咨询
        } catch (SQLException e) {
            System.err.println("加载所有咨询失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 修改方法：根据当前筛选器加载咨询
    private void loadFilteredConsultations() {
        List<Consultation> filteredList = currentDisplayedConsultations.stream()
                .filter(c -> {
                    // 先按左侧导航栏筛选
                    boolean leftNavMatch = true;
                    if (currentSelectedLeftNavButton == featuredButton) {
                        leftNavMatch = c.isFeatured();
                    }
                    // 再按类别栏筛选
                    boolean categoryMatch = true;
                    if (currentSelectedCategoryButton != null && currentSelectedCategoryButton != allCategoriesButton) {
                        String category = currentSelectedCategoryButton.getText();
                        categoryMatch = c.getCategory().equals(category);
                    }
                    return leftNavMatch && categoryMatch;
                })
                .collect(Collectors.toList());
        updateConsultationCards(filteredList);
    }

    // 修改方法：根据类别筛选咨询
    private void filterConsultationsByCategory(String category) {
        // 只更新当前类别按钮，不直接筛选，交由loadFilteredConsultations统一处理
        loadFilteredConsultations();
    }

    private void performSearch() {
        String searchText = searchField.getText().trim();
        if (searchText.isEmpty()) {
            // 如果搜索框为空，重新加载当前筛选状态的咨询
            loadFilteredConsultations();
        } else {
            List<Consultation> searchResults = currentDisplayedConsultations.stream()
                    .filter(c -> c.getTitle().toLowerCase().contains(searchText.toLowerCase()) ||
                                 c.getContent().toLowerCase().contains(searchText.toLowerCase()))
                    .collect(Collectors.toList());
            updateConsultationCards(searchResults);
        }
    }

    private void updateConsultationCards(List<Consultation> consultations) {
        cardsContainer.getChildren().clear();
        System.out.println("准备在大厅加载 " + consultations.size() + " 张咨询卡片。");
        if (consultations != null && !consultations.isEmpty()) {
            for (Consultation consultation : consultations) {
                try {
                    cardsContainer.getChildren().add(createConsultationCard(consultation));
                } catch (IOException e) {
                    System.err.println("创建咨询卡片失败: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } else {
            Label noResultsLabel = new Label("暂无相关咨询");
            noResultsLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #888; -fx-padding: 20px;");
            cardsContainer.getChildren().add(noResultsLabel);
            System.out.println("大厅未找到相关咨询，显示'暂无相关咨询'。");
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
        statusLabel.getStyleClass().add(getStatusStyleClass(statusLabel.getText()));
        categoryTag.setText(consultation.getCategory());

        // 设置精选图标状态
        updateFeaturedIcon(featuredIcon, consultation.isFeatured());

        // 精选图标点击事件（阻止冒泡）
        featuredIcon.setOnMouseClicked(event -> {
            event.consume(); // 阻止事件冒泡到卡片
            try {
                toggleConsultationFeaturedStatus(consultation);
                updateFeaturedIcon(featuredIcon, consultation.isFeatured());
                loadFilteredConsultations();
            } catch (SQLException e) {
                System.err.println("切换精选状态失败: " + e.getMessage());
                e.printStackTrace();
            }
        });

        // 只有点击卡片非精选图标区域才弹出详情
        card.setOnMouseClicked(event -> {
            // 如果点击的是精选图标则不处理
            if (event.getTarget() == featuredIcon) return;
            openConsultationDetail(consultation);
        });

        return card;
    }

    private void openConsultationDetail(Consultation consultation) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/counselor_consultation_detail.fxml"));
            Parent detailRoot = loader.load();
            CounselorConsultationDetailController controller = loader.getController();
            controller.setConsultation(consultation, false);

            Stage detailStage = new Stage();
            detailStage.setTitle("咨询详情");
            detailStage.setScene(new Scene(detailRoot));
            detailStage.initModality(Modality.APPLICATION_MODAL);
            detailStage.initOwner(cardsContainer.getScene().getWindow());
            detailStage.setWidth(800);
            detailStage.setHeight(700);
            detailStage.setResizable(false);
            controller.setStage(detailStage);
            detailStage.showAndWait();

            loadFilteredConsultations(); // 窗口关闭后刷新列表
        } catch (IOException e) {
            System.err.println("无法加载咨询详情窗口: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "错误", "无法加载咨询详情窗口。");
        }
    }

    private String getStatusStyleClass(String status) {
        return switch (status) {
            case "已解决" -> "status-resolved";
            case "未回复" -> "status-unanswered";
            case "仍需解决" -> "status-unresolved";
            default -> "";
        };
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

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showHall() {
        consultationList.getChildren().clear();
        // 重新加载大厅原有内容
        // 这里假设原有内容为categoryBar和ScrollPane
        consultationList.getChildren().add(categoryBar);
        javafx.scene.control.ScrollPane scrollPane = new javafx.scene.control.ScrollPane(cardsContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("scroll-pane");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        consultationList.getChildren().add(scrollPane);
        // 恢复筛选和卡片加载
        selectLeftNavButton(allButton);
        selectCategoryButton(allCategoriesButton);
        loadAllConsultations();
    }

    private void showMyConsultations() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/counselor_my_consultations.fxml"));
            VBox myConsultationsRoot = loader.load();
            consultationList.getChildren().setAll(myConsultationsRoot.getChildren());
        } catch (IOException ex) {
            System.err.println("加载我的答疑内容失败: " + ex.getMessage());
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "错误", "无法加载我的答疑内容。");
        }
    }

    private void handleLogout() {
        try {
            // 关闭当前窗口
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.close();

            // 打开登录界面
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/login.fxml"));
            Parent root = loader.load();
            Stage loginStage = new Stage();
            loginStage.setTitle("登录");
            loginStage.setScene(new Scene(root));
            loginStage.show();
        } catch (IOException e) {
            System.err.println("打开登录界面失败：" + e.getMessage());
            e.printStackTrace();
        }
    }
} 