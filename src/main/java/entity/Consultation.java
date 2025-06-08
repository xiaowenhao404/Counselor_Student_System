package entity;

import javafx.beans.property.*;
import java.time.LocalDateTime;

public class Consultation {
    private final StringProperty consultationId = new SimpleStringProperty();
    private final StringProperty studentId = new SimpleStringProperty();
    private final StringProperty studentName = new SimpleStringProperty();
    private final StringProperty category = new SimpleStringProperty();
    private final StringProperty status = new SimpleStringProperty();
    private final ObjectProperty<LocalDateTime> questionTime = new SimpleObjectProperty<>();
    private final IntegerProperty replyCount = new SimpleIntegerProperty();
    private final IntegerProperty followUpCount = new SimpleIntegerProperty();
    private final BooleanProperty isHighlighted = new SimpleBooleanProperty();
    private final StringProperty questionContent = new SimpleStringProperty();

    // 构造函数
    public Consultation() {}

    public Consultation(String consultationId, String studentId, String studentName, 
                       String category, String status, LocalDateTime questionTime,
                       int replyCount, int followUpCount, boolean isHighlighted, String questionContent) {
        setConsultationId(consultationId);
        setStudentId(studentId);
        setStudentName(studentName);
        setCategory(category);
        setStatus(status);
        setQuestionTime(questionTime);
        setReplyCount(replyCount);
        setFollowUpCount(followUpCount);
        setIsHighlighted(isHighlighted);
        setQuestionContent(questionContent);
    }

    // 咨询编号
    public String getConsultationId() { return consultationId.get(); }
    public void setConsultationId(String consultationId) { this.consultationId.set(consultationId); }
    public StringProperty consultationIdProperty() { return consultationId; }

    // 学生学号
    public String getStudentId() { return studentId.get(); }
    public void setStudentId(String studentId) { this.studentId.set(studentId); }
    public StringProperty studentIdProperty() { return studentId; }

    // 学生姓名
    public String getStudentName() { return studentName.get(); }
    public void setStudentName(String studentName) { this.studentName.set(studentName); }
    public StringProperty studentNameProperty() { return studentName; }

    // 类别
    public String getCategory() { return category.get(); }
    public void setCategory(String category) { this.category.set(category); }
    public StringProperty categoryProperty() { return category; }

    // 状态
    public String getStatus() { return status.get(); }
    public void setStatus(String status) { this.status.set(status); }
    public StringProperty statusProperty() { return status; }

    // 提问时间
    public LocalDateTime getQuestionTime() { return questionTime.get(); }
    public void setQuestionTime(LocalDateTime questionTime) { this.questionTime.set(questionTime); }
    public ObjectProperty<LocalDateTime> questionTimeProperty() { return questionTime; }

    // 回复次数
    public int getReplyCount() { return replyCount.get(); }
    public void setReplyCount(int replyCount) { this.replyCount.set(replyCount); }
    public IntegerProperty replyCountProperty() { return replyCount; }

    // 追问次数
    public int getFollowUpCount() { return followUpCount.get(); }
    public void setFollowUpCount(int followUpCount) { this.followUpCount.set(followUpCount); }
    public IntegerProperty followUpCountProperty() { return followUpCount; }

    // 是否加精
    public boolean getIsHighlighted() { return isHighlighted.get(); }
    public void setIsHighlighted(boolean isHighlighted) { this.isHighlighted.set(isHighlighted); }
    public BooleanProperty isHighlightedProperty() { return isHighlighted; }

    // 提问内容
    public String getQuestionContent() { return questionContent.get(); }
    public void setQuestionContent(String questionContent) { this.questionContent.set(questionContent); }
    public StringProperty questionContentProperty() { return questionContent; }

    @Override
    public String toString() {
        return "Consultation{" +
                "consultationId='" + getConsultationId() + '\'' +
                ", studentId='" + getStudentId() + '\'' +
                ", studentName='" + getStudentName() + '\'' +
                ", category='" + getCategory() + '\'' +
                ", status='" + getStatus() + '\'' +
                ", questionTime=" + getQuestionTime() +
                ", replyCount=" + getReplyCount() +
                ", followUpCount=" + getFollowUpCount() +
                ", isHighlighted=" + getIsHighlighted() +
                '}';
    }
} 