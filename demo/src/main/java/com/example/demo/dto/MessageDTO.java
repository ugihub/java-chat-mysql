package com.example.demo.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Data Transfer Object untuk pesan chat
 */
public class MessageDTO {
    private Long id;
    private String content;
    private String timestampString;
    private String username;

    public MessageDTO(Long id, String content, String timestampString, String username) {
        this.id = id;
        this.content = content;
        this.timestampString = timestampString;
        this.username = username;
    }

    public MessageDTO() {
    }

    // Getter & Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTimestampString() {
        return timestampString;
    }

    public void setTimestampString(String timestampString) {
        this.timestampString = timestampString;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // toString (opsional untuk debugging)
    @Override
    public String toString() {
        return "MessageDTO{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", timestampString='" + timestampString + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}