package com.example.demo.repository;

import com.example.demo.model.RoomPrivateInvitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.demo.model.RoomPrivate;
import com.example.demo.model.User;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

@Repository
public interface RoomPrivateInvitationRepository extends JpaRepository<RoomPrivateInvitation, Long> {

    List<RoomPrivateInvitation> findByInvitedUser(User user);

    List<RoomPrivateInvitation> findByRoomPrivate(RoomPrivate room);

    List<RoomPrivateInvitation> findByRoomPrivateOwnerId(Long ownerId);

    Optional<RoomPrivateInvitation> findByRoomPrivateAndInvitedUser(RoomPrivate room, User invitedUser);

    boolean existsByRoomPrivateAndInvitedUserAndStatus(RoomPrivate room, User invitedUser, String status);

    // ✅ Metode ini harus ada di sini untuk mengatasi error "cannot find symbol"
    @Modifying
    @Query("DELETE FROM RoomPrivateInvitation rpi WHERE rpi.roomPrivate = ?1")
    void deleteByRoomPrivate(RoomPrivate room);
}
