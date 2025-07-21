package com.example.demo.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;

@Entity
public class RoomPrivate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String inviteCode; // Generate menggunakan UUID

    // Pemilik room
    @ManyToOne
    private User owner;

    // ✅ Relasi ManyToMany dengan Set<User>
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
        name = "room_private_participants",
        joinColumns = @JoinColumn(name = "room_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> participants = new HashSet<>();

    // Relasi dengan Message
    @OneToMany(mappedBy = "roomPrivate", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Message> messages = new HashSet<>();

    // Relasi: Room privat memiliki banyak invite
    @OneToMany(mappedBy = "roomPrivate", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RoomPrivateInvitation> invites = new HashSet<>();

    // Konstruktor Default
    public RoomPrivate() {
        this.participants = new HashSet<>();
        this.messages = new HashSet<>();
        this.invites = new HashSet<>();
    }

    // Konstruktor dengan Parameter
    public RoomPrivate(String name, String inviteCode) {
        this();
        this.name = name;
        this.inviteCode = inviteCode;
    }

    // Konstruktor dengan owner
    public RoomPrivate(User owner) {
        this();
        this.owner = owner;
    }

    // Getter & Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    public Set<Message> getMessages() {
        return messages;
    }

    public void setMessages(Set<Message> messages) {
        this.messages = messages;
    }

    public Set<RoomPrivateInvitation> getInvites() {
        return invites;
    }

    public void setInvites(Set<RoomPrivateInvitation> invites) {
        this.invites = invites;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public Set<User> getParticipants() {
        return participants;
    }

    public void setParticipants(Set<User> participants) {
        this.participants = participants;
    }

    @Override
    public String toString() {
        return "RoomPrivate{"
                + "id=" + id
                + ", name='" + name + '\''
                + ", inviteCode='" + inviteCode + '\''
                + '}';
    }
}
