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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
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
        
        // 默认选中"大厅"按钮和"全部"按钮
        selectNavButton(hallButton);
        selectLeftNavButton(allButton);
        loadAllConsultations(); // 初始加载所有咨询
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
            // 不再切换主场景，而是打开新的模态窗口
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
                    switch (currentFilter) {
                        case ALL:
                            return true;
                        case FEATURED:
                            return c.isFeatured();
                        case CATEGORY:
                            // 类别筛选将由 filterConsultationsByCategory 处理
                            return true;
                        default:
                            return true;
                    }
                })
                .collect(Collectors.toList());
        updateConsultationCards(filteredList);
    }

    // 修改方法：根据类别筛选咨询
    private void filterConsultationsByCategory(String category) {
        if (currentDisplayedConsultations == null) {
            return;
        }
        List<Consultation> filteredList;
        if (category.equals("全部")) {
            filteredList = currentDisplayedConsultations;
        } else {
            filteredList = currentDisplayedConsultations.stream()
                    .filter(c -> c.getCategory().equals(category))
                    .collect(Collectors.toList());
        }
        updateConsultationCards(filteredList);
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
        statusLabel.setText(consultation.getStatus());
        statusLabel.getStyleClass().add(getStatusStyleClass(consultation.getStatus()));
        categoryTag.setText(consultation.getCategory());

        // 设置精选图标状态
        updateFeaturedIcon(featuredIcon, consultation.isFeatured());

        // 精选图标点击事件
        featuredIcon.setOnMouseClicked(event -> {
            try {
                toggleConsultationFeaturedStatus(consultation);
                updateFeaturedIcon(featuredIcon, consultation.isFeatured());
                // 刷新当前列表以反映状态变化
                loadFilteredConsultations(); // 刷新当前筛选状态下的列表
            } catch (SQLException e) {
                System.err.println("切换精选状态失败: " + e.getMessage());
                e.printStackTrace();
            }
        });

        // 留言图标点击事件 (TODO: 实现跳转到咨询详情页)
        messageIcon.setOnMouseClicked(event -> {
            System.out.println("点击留言图标，咨询ID: " + consultation.getQId());
            try {
                FXMLLoader detailLoader = new FXMLLoader(getClass().getResource("/ui/consultation_detail.fxml"));
                Parent detailRoot = detailLoader.load();
                ConsultationDetailController controller = detailLoader.getController();
                controller.setConsultation(consultation);
                // 详情页更新后，刷新当前筛选状态下的列表
                controller.setOnConsultationUpdated(this::loadFilteredConsultations);

                Stage detailStage = new Stage();
                detailStage.setTitle("咨询详情");
                detailStage.setScene(new Scene(detailRoot));
                detailStage.initModality(Modality.APPLICATION_MODAL);
                detailStage.initOwner(cardsContainer.getScene().getWindow());
                detailStage.setWidth(800);
                detailStage.setHeight(700);
                detailStage.showAndWait();
                loadFilteredConsultations(); // 详情页关闭后刷新列表
            } catch (IOException e) {
                System.err.println("打开咨询详情页面失败: " + e.getMessage());
                e.printStackTrace();
            }
        });

        return card;
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
} 