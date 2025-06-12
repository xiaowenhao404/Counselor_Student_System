package entity;

import java.time.LocalDateTime;

public class Followup {
    private String fNumber;
    private String qNumber;
    private String content;
    private LocalDateTime time;

    public String getFNumber() {
        return fNumber;
    }

    public void setFNumber(String fNumber) {
        this.fNumber = fNumber;
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
}