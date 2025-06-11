package ui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Region;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

public class MyConsultationsController {
    @FXML
    private HBox navButtons;
    
    @FXML
    private TextField searchField;
    
    @FXML
    private VBox navButtonsContainer;
    
    @FXML
    private Button newConsultationButton;
    
    @FXML
    private HBox categoryBar;
    
    @FXML
    private VBox cardsContainer;
    
    private ObservableList<Consultation> consultations;
    private FilteredList<Consultation> filteredConsultations;
    
    @FXML
    public void initialize() {
        // 初始化咨询数据
        consultations = FXCollections.observableArrayList();
        filteredConsultations = new FilteredList<>(consultations);
        
        // 添加测试数据
        addTestData();
        
        // 初始化导航按钮点击事件
        navButtons.getChildren().forEach(node -> {
            if (node instanceof Button) {
                Button button = (Button) node;
                button.setOnAction(event -> {
                    navButtons.getChildren().forEach(btn -> {
                        if (btn instanceof Button) {
                            btn.getStyleClass().remove("selected");
                        }
                    });
                    button.getStyleClass().add("selected");
                });
            }
        });
        
        // 初始化左侧导航按钮点击事件
        navButtonsContainer.getChildren().forEach(node -> {
            if (node instanceof Button) {
                Button button = (Button) node;
                button.setOnAction(event -> {
                    navButtonsContainer.getChildren().forEach(btn -> {
                        if (btn instanceof Button) {
                            btn.getStyleClass().remove("selected");
                        }
                    });
                    button.getStyleClass().add("selected");
                    
                    // 根据选中的按钮筛选咨询
                    String status = button.getText();
                    filteredConsultations.setPredicate(consultation -> {
                        if (status.equals("全部")) {
                            return true;
                        }
                        return consultation.getStatus().equals(status);
                    });
                });
            }
        });
        
        // 初始化类别按钮点击事件
        categoryBar.getChildren().forEach(node -> {
            if (node instanceof Button) {
                Button button = (Button) node;
                button.setOnAction(event -> {
                    categoryBar.getChildren().forEach(btn -> {
                        if (btn instanceof Button) {
                            btn.getStyleClass().remove("selected");
                        }
                    });
                    button.getStyleClass().add("selected");
                });
            }
        });
        
        // 初始化"我要咨询"按钮点击事件
        newConsultationButton.setOnAction(event -> {
            // TODO: 跳转到新建咨询界面
        });
        
        // 初始化搜索框
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredConsultations.setPredicate(consultation -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return consultation.getQuestion().toLowerCase().contains(lowerCaseFilter) ||
                       consultation.getReply().toLowerCase().contains(lowerCaseFilter);
            });
        });
        
        // 监听筛选后的咨询列表变化
        filteredConsultations.addListener((javafx.collections.ListChangeListener.Change<? extends Consultation> change) -> {
            updateConsultationCards();
        });
        
        // 初始显示所有咨询卡片
        updateConsultationCards();
    }
    
    private void addTestData() {
        consultations.add(new Consultation("问题1", "回复1", LocalDateTime.now(), "未答复", false));
        consultations.add(new Consultation("问题2", "回复2", LocalDateTime.now().minusDays(1), "仍需解决", true));
        consultations.add(new Consultation("问题3", "回复3", LocalDateTime.now().minusDays(2), "已解决", true));
    }
    
    private void updateConsultationCards() {
        cardsContainer.getChildren().clear();
        
        for (Consultation consultation : filteredConsultations) {
            VBox card = createConsultationCard(consultation);
            cardsContainer.getChildren().add(card);
            
            // 添加分割线
            if (consultation != filteredConsultations.get(filteredConsultations.size() - 1)) {
                Separator separator = new Separator();
                separator.getStyleClass().add("card-separator");
                cardsContainer.getChildren().add(separator);
            }
        }
    }
    
    private VBox createConsultationCard(Consultation consultation) {
        VBox card = new VBox();
        card.getStyleClass().add("consultation-card");
        
        // 卡片头部
        HBox header = new HBox();
        header.getStyleClass().add("card-header");
        
        Label questionLabel = new Label(consultation.getQuestion());
        questionLabel.getStyleClass().add("question-label");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        
        Label timeLabel = new Label(consultation.getTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        timeLabel.getStyleClass().add("time-label");
        
        header.getChildren().addAll(questionLabel, spacer, timeLabel);
        
        // 回复内容
        Label replyLabel = new Label(consultation.getReply());
        replyLabel.getStyleClass().add("reply-label");
        
        // 卡片底部
        HBox footer = new HBox();
        footer.getStyleClass().add("card-footer");
        
        HBox statusContainer = new HBox();
        statusContainer.getStyleClass().add("status-container");
        
        Label statusLabel = new Label(consultation.getStatus());
        statusLabel.getStyleClass().add("status-label");
        statusLabel.getStyleClass().add(getStatusStyleClass(consultation.getStatus()));
        
        statusContainer.getChildren().add(statusLabel);
        
        Region footerSpacer = new Region();
        HBox.setHgrow(footerSpacer, javafx.scene.layout.Priority.ALWAYS);
        
        HBox interactionContainer = new HBox();
        interactionContainer.getStyleClass().add("interaction-container");
        
        ImageView messageIcon = new ImageView(new Image(getClass().getResourceAsStream("/images/message.png")));
        messageIcon.setFitWidth(16);
        messageIcon.setFitHeight(16);
        messageIcon.getStyleClass().add("message-icon");
        
        CheckBox solveCheckbox = new CheckBox();
        solveCheckbox.getStyleClass().add("solve-checkbox");
        solveCheckbox.setSelected(consultation.isSolved());
        
        ImageView checkboxImage = new ImageView(new Image(getClass().getResourceAsStream(
            consultation.isSolved() ? "/images/select.png" : "/images/not_select.png")));
        checkboxImage.setFitWidth(16);
        checkboxImage.setFitHeight(16);
        solveCheckbox.setGraphic(checkboxImage);
        
        solveCheckbox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            consultation.setSolved(newValue);
            checkboxImage.setImage(new Image(getClass().getResourceAsStream(
                newValue ? "/images/select.png" : "/images/not_select.png")));
            
            // 更新状态
            String newStatus = newValue ? "已解决" : "仍需解决";
            consultation.setStatus(newStatus);
            statusLabel.setText(newStatus);
            statusLabel.getStyleClass().clear();
            statusLabel.getStyleClass().add("status-label");
            statusLabel.getStyleClass().add(getStatusStyleClass(newStatus));
        });
        
        interactionContainer.getChildren().addAll(messageIcon, solveCheckbox);
        
        footer.getChildren().addAll(statusContainer, footerSpacer, interactionContainer);
        
        card.getChildren().addAll(header, replyLabel, footer);
        
        return card;
    }
    
    private String getStatusStyleClass(String status) {
        switch (status) {
            case "未答复":
                return "unanswered";
            case "仍需解决":
                return "needs-solution";
            case "已解决":
                return "solved";
            default:
                return "";
        }
    }
    
    // 咨询实体类
    private static class Consultation {
        private String question;
        private String reply;
        private LocalDateTime time;
        private String status;
        private boolean solved;
        
        public Consultation(String question, String reply, LocalDateTime time, String status, boolean solved) {
            this.question = question;
            this.reply = reply;
            this.time = time;
            this.status = status;
            this.solved = solved;
        }
        
        public String getQuestion() {
            return question;
        }
        
        public String getReply() {
            return reply;
        }
        
        public LocalDateTime getTime() {
            return time;
        }
        
        public String getStatus() {
            return status;
        }
        
        public void setStatus(String status) {
            this.status = status;
        }
        
        public boolean isSolved() {
            return solved;
        }
        
        public void setSolved(boolean solved) {
            this.solved = solved;
        }
    }
} 