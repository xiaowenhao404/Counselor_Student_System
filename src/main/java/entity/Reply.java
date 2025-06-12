package entity;

import java.time.LocalDateTime;

public class Reply {
    private String rNumber;
    private String qNumber;
    private String content;
    private LocalDateTime time;
    private String responderName;

    public String getRNumber() {
        return rNumber;
    }

    public void setRNumber(String rNumber) {
        this.rNumber = rNumber;
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