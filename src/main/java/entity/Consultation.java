package entity;

import java.time.LocalDateTime;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class Consultation {
    private String qNumber;
    private String studentId;
    private String studentName;
    private String category;
    private String status;
    private String questionTitle;
    private LocalDateTime questionTime;
    private int replyCount;
    private int followupCount;
    private BooleanProperty highlighted = new SimpleBooleanProperty(false);
    private String questionContent;
    private boolean collected = false;

    public Consultation() {
    }

    public Consultation(String qNumber, String studentId, String studentName, String category,
            String status, String questionTitle, LocalDateTime questionTime, int replyCount, int followupCount,
            boolean highlighted) {
        this.qNumber = qNumber;
        this.studentId = studentId;
        this.studentName = studentName;
        this.category = category;
        this.status = status;
        this.questionTitle = questionTitle;
        this.questionTime = questionTime;
        this.replyCount = replyCount;
        this.followupCount = followupCount;
        this.highlighted.set(highlighted);
    }

    public String getQNumber() {
        return qNumber;
    }

    public void setQNumber(String qNumber) {
        this.qNumber = qNumber;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getQuestionTitle() {
        return questionTitle;
    }

    public void setQuestionTitle(String questionTitle) {
        this.questionTitle = questionTitle;
    }

    public LocalDateTime getQuestionTime() {
        return questionTime;
    }

    public void setQuestionTime(LocalDateTime questionTime) {
        this.questionTime = questionTime;
    }

    public int getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(int replyCount) {
        this.replyCount = replyCount;
    }

    public int getFollowupCount() {
        return followupCount;
    }

    public void setFollowupCount(int followupCount) {
        this.followupCount = followupCount;
    }

    public boolean isHighlighted() {
        return highlighted.get();
    }

    public void setHighlighted(boolean highlighted) {
        this.highlighted.set(highlighted);
    }

    public BooleanProperty highlightedProperty() {
        return highlighted;
    }

    public String getQuestionContent() {
        return questionContent;
    }

    public void setQuestionContent(String questionContent) {
        this.questionContent = questionContent;
    }

    public boolean isCollected() {
        return collected;
    }

    public void setCollected(boolean collected) {
        this.collected = collected;
    }

    // 提供别名方法以兼容 CounselorMainController
    public String getQId() {
        return getQNumber();
    }

    public String getTitle() {
        return getQuestionTitle();
    }

    public String getContent() {
        return getQuestionContent();
    }

    public boolean isFeatured() {
        return isHighlighted();
    }

    public void setFeatured(boolean featured) {
        setHighlighted(featured);
    }

    @Override
    public String toString() {
        return "Consultation{" +
                "qNumber='" + qNumber + '\'' +
                ", studentId='" + studentId + '\'' +
                ", studentName='" + studentName + '\'' +
                ", category='" + category + '\'' +
                ", status='" + status + '\'' +
                ", questionTitle='" + questionTitle + '\'' +
                ", questionTime=" + questionTime +
                ", replyCount=" + replyCount +
                ", followupCount=" + followupCount +
                ", highlighted=" + highlighted.get() +
                '}';
    }
}