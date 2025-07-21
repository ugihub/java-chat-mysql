package com.example.demo.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;

@Entity
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = true, unique = true)
    private String email;

    // Relasi: User bisa memiliki banyak RoomPrivate (sebagai owner)
    @OneToMany(mappedBy = "owner")
    private Set<RoomPrivate> ownedPrivateRooms = new HashSet<>();

    // Relasi: User bisa menjadi peserta di banyak RoomPrivate - ManyToMany mappedBy
    @ManyToMany(mappedBy = "participants")
    private Set<RoomPrivate> participatedPrivateRooms = new HashSet<>();

    // Relasi dengan RoomPublic
    @OneToMany(mappedBy = "owner")
    private Set<RoomPublic> ownedPublicRooms = new HashSet<>();

    // Relasi dengan RoomPrivateInvitation (undangan yang diterima user)
    @OneToMany(mappedBy = "invitedUser")
    private Set<RoomPrivateInvitation> privateRoomInvitations = new HashSet<>();

    // Konstruktor
    public User() {
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    // Getter dan Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<RoomPrivate> getOwnedPrivateRooms() {
        return ownedPrivateRooms;
    }

    public void setOwnedPrivateRooms(Set<RoomPrivate> ownedPrivateRooms) {
        this.ownedPrivateRooms = ownedPrivateRooms;
    }

    public Set<RoomPrivate> getParticipatedPrivateRooms() {
        return participatedPrivateRooms;
    }

    public void setParticipatedPrivateRooms(Set<RoomPrivate> participatedPrivateRooms) {
        this.participatedPrivateRooms = participatedPrivateRooms;
    }

    public Set<RoomPublic> getOwnedPublicRooms() {
        return ownedPublicRooms;
    }

    public void setOwnedPublicRooms(Set<RoomPublic> ownedPublicRooms) {
        this.ownedPublicRooms = ownedPublicRooms;
    }

    public Set<RoomPrivateInvitation> getPrivateRoomInvitations() {
        return privateRoomInvitations;
    }

    public void setPrivateRoomInvitations(Set<RoomPrivateInvitation> privateRoomInvitations) {
        this.privateRoomInvitations = privateRoomInvitations;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return new HashSet<>(); // Kosong karena tidak ada role/authority di sini
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String toString() {
        return "User{"
                + "id=" + id
                + ", username='" + username + '\''
                + ", email='" + email + '\''
                + '}';
    }

        // ✅ Override equals() dan hashCode()
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id != null && id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}