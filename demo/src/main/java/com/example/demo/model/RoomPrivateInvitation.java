package com.example.demo.model;

import jakarta.persistence.*;

@Entity
public class RoomPrivateInvitation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private RoomPrivate roomPrivate;

    @ManyToOne
    private User invitedUser;

    private String status; // "PENDING", "APPROVED", "DENIED"

    // Getter & Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RoomPrivate getRoomPrivate() {
        return roomPrivate;
    }

    public void setRoomPrivate(RoomPrivate roomPrivate) {
        this.roomPrivate = roomPrivate;
    }

    public User getInvitedUser() {
        return invitedUser;
    }

    public void setInvitedUser(User invitedUser) {
        this.invitedUser = invitedUser;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
