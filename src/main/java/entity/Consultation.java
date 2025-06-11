package entity;

import java.time.LocalDateTime;

public class Consultation {
    private String qNumber;
    private String studentId;
    private String studentName;
    private String category;
    private String status;
    private LocalDateTime questionTime;
    private int replyCount;
    private int followupCount;
    private boolean highlighted;

    public Consultation() {
    }

    public Consultation(String qNumber, String studentId, String studentName, String category,
            String status, LocalDateTime questionTime, int replyCount, int followupCount,
            boolean highlighted) {
        this.qNumber = qNumber;
        this.studentId = studentId;
        this.studentName = studentName;
        this.category = category;
        this.status = status;
        this.questionTime = questionTime;
        this.replyCount = replyCount;
        this.followupCount = followupCount;
        this.highlighted = highlighted;
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
        return highlighted;
    }

    public void setHighlighted(boolean highlighted) {
        this.highlighted = highlighted;
    }

    @Override
    public String toString() {
        return "Consultation{" +
                "qNumber='" + qNumber + '\'' +
                ", studentId='" + studentId + '\'' +
                ", studentName='" + studentName + '\'' +
                ", category='" + category + '\'' +
                ", status='" + status + '\'' +
                ", questionTime=" + questionTime +
                ", replyCount=" + replyCount +
                ", followupCount=" + followupCount +
                ", highlighted=" + highlighted +
                '}';
    }
}