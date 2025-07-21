package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.model.Message;
import com.example.demo.model.RoomPrivate;
import com.example.demo.model.RoomPublic;
import com.example.demo.model.User;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByRoomPublic(RoomPublic room);

    List<Message> findByRoomPrivate(RoomPrivate room);

    void deleteByRoomPrivate(RoomPrivate room);

    @Modifying
    @Query("DELETE FROM Message m WHERE m.roomPrivate.id = ?1 AND m.user.id = ?2")
    void deleteByRoomPrivateIdAndUserId(Long roomPrivateId, Long userId);

    @Modifying
    @Query("DELETE FROM Message m WHERE m.roomPrivate.id = ?1 AND m.user.id = ?2 AND m.id IN ?3")
    void deleteByRoomPrivateIdAndUserIdAndIdIn(Long roomPrivateId, Long userId, List<Long> ids);

    @Modifying
    @Query("DELETE FROM Message m WHERE m.roomPrivate.id = ?1 AND m.user.id = ?2 AND m.id IN ?3")
    void deleteByRoomPrivateIdAndUserAndIdIn(Long roomPrivateId, Long userId, List<Long> ids);

    void deleteByRoomPublic(RoomPublic room);

    void deleteByRoomPublicAndUser(RoomPublic room, User user);

    // ✅ Pastikan method ini ada dan benar
    @Modifying
    @Query("DELETE FROM Message m WHERE m.roomPublic = ?1 AND m.user = ?2 AND m.id IN ?3")
    void deleteByRoomPublicAndUserAndIdIn(RoomPublic room, User user, List<Long> ids);

}
