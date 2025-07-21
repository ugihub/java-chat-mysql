package com.example.demo.model;

import jakarta.persistence.*;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
public class Invite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE) // ✅ Tambahkan import
    private User user;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE) // ✅ Tambahkan import
    private RoomPrivate roomPrivate;

    // Konstruktor Default
    public Invite() {}

    // Konstruktor dengan Parameter
    public Invite(User user, RoomPrivate roomPrivate) {
        this.user = user;
        this.roomPrivate = roomPrivate;
    }

    // Getter & Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public RoomPrivate getRoomPrivate() {
        return roomPrivate;
    }

    public void setRoomPrivate(RoomPrivate roomPrivate) {
        this.roomPrivate = roomPrivate;
    }

    @Override
    public String toString() {
        return "Invite{" +
                "id=" + id +
                '}';
    }
}