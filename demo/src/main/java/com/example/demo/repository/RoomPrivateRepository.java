package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.RoomPrivate;
import com.example.demo.model.User; // Import User

@Repository
public interface RoomPrivateRepository extends JpaRepository<RoomPrivate, Long> {

    Optional<RoomPrivate> findByInviteCode(String inviteCode);

    List<RoomPrivate> findByOwnerId(Long ownerId);

    Optional<RoomPrivate> findById(Long id);

    // Aktifkan kembali metode ini untuk ManyToMany
    List<RoomPrivate> findByParticipantsContaining(User user);
}