package com.example.demo.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;

@Entity
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    @Column(columnDefinition = "DATETIME")
    private LocalDateTime timestamp;

    // Relasi User (tanpa @JsonBackReference di sini)
    @ManyToOne
    private User user;

    // Relasi RoomPublik (boleh tetap menggunakan @JsonBackReference)
    @JsonBackReference("room-public-messages")
    @ManyToOne
    private RoomPublic roomPublic;

    // Relasi RoomPrivat (boleh tetap menggunakan @JsonBackReference)
    @JsonBackReference("room-private-messages")
    @ManyToOne
    private RoomPrivate roomPrivate;

    // Field untuk frontend
    @Transient
    private String timestampString;

    // Konstruktor Default
    public Message() {
    }

    // Konstruktor dengan Parameter
    public Message(String content, User user, RoomPublic roomPublic) {
        this.content = content;
        this.user = user;
        this.roomPublic = roomPublic;
        this.roomPrivate = null; // ✅ Set ke null jika bukan room privat
        this.timestamp = LocalDateTime.now();
    }

    public Message(String content, User user, RoomPrivate roomPrivate) {
        this.content = content;
        this.user = user;
        this.roomPrivate = roomPrivate;
        this.roomPublic = null; // ✅ Set ke null jika bukan room publik
        this.timestamp = LocalDateTime.now();
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

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public RoomPublic getRoomPublic() {
        return roomPublic;
    }

    public void setRoomPublic(RoomPublic roomPublic) {
        this.roomPublic = roomPublic;
    }

    public RoomPrivate getRoomPrivate() {
        return roomPrivate;
    }

    public void setRoomPrivate(RoomPrivate roomPrivate) {
        this.roomPrivate = roomPrivate;
    }

    // ✅ Tambahkan getter & setter untuk timestampString
    public String getTimestampString() {
        return timestampString;
    }

    public void setTimestampString(String timestampString) {
        this.timestampString = timestampString;
    }
}