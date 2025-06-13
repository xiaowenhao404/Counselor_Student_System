package entity;

import java.time.LocalDateTime;

public class Reply {
    private String RNumber;
    private String qNumber;
    private String content;
    private LocalDateTime time;
    private String responderName;

    public String getRNumber() {
        return RNumber;
    }

    public void setRNumber(String RNumber) {
        this.RNumber = RNumber;
    }

    public String getQNumber() {
        return qNumber;
    }

    public void setQNumber(String qNumber) {
        this.qNumber = qNumber;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public String getResponderName() {
        return responderName;
    }

    public void setResponderName(String responderName) {
        this.responderName = responderName;
    }
}