package com.example.demo.service;

import com.example.demo.model.RoomPrivateInvitation;
import com.example.demo.repository.RoomPrivateInvitationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.User;

import java.util.List;

@Service
public class InvitationService {

    @Autowired
    private RoomPrivateInvitationRepository invitationRepo;

    public void saveInvitation(RoomPrivateInvitation invitation) {
        invitation.setStatus("PENDING");
        invitationRepo.save(invitation);
    }

    public List<RoomPrivateInvitation> getPendingInvitationsForUser(User user) {
        return invitationRepo.findByInvitedUser(user);
    }

    public void respondToInvitation(RoomPrivateInvitation invitation, String response) {
        invitation.setStatus(response.toUpperCase().equals("APPROVED") ? "APPROVED" : "DENIED");
        invitationRepo.save(invitation);
    }
}