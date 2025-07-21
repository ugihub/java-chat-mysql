package com.example.demo.service;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.List; // ✅ Tambahkan import ini

@Service
public class ChatService {

    @Autowired
    private RoomPublicRepository roomPublicRepo;

    @Autowired
    private RoomPrivateRepository roomPrivateRepo;

    @Autowired
    private InviteRepository inviteRepo;

    @Autowired
    private MessageRepository messageRepo;

    @Autowired
    private UserRepository userRepo;

    // ✅ Tambahkan method untuk mengambil semua room publik
    public List<RoomPublic> getAllPublicRooms() {
        return roomPublicRepo.findAll();
    }

    // ✅ Tambahkan method untuk mengambil semua room privat
    public List<RoomPrivate> getAllPrivateRooms() {
        return roomPrivateRepo.findAll();
    }

    // Buat Room Publik
    public RoomPrivate createPrivateRoom(String name, String inviteCode) {
        RoomPrivate room = new RoomPrivate();
        room.setName(name);
        room.setInviteCode(inviteCode);
        return roomPrivateRepo.save(room);
    }

    public RoomPublic createPublicRoom(String name) {
        RoomPublic room = new RoomPublic();
        room.setName(name);
        return roomPublicRepo.save(room);
    }

    // Undang Pengguna ke Room Privat
    public void inviteUserToPrivateRoom(User user, RoomPrivate room) {
        Invite invite = new Invite(user, room);
        inviteRepo.save(invite);
    }

    // Simpan Pesan ke Database
    public void saveMessage(Message message) {
        messageRepo.save(message);
    }

    public void addUserToPrivateRoom(User user, RoomPrivate room) {
        if (!room.getParticipants().contains(user)) {
            room.getParticipants().add(user);
            roomPrivateRepo.save(room);
        }
    }

}