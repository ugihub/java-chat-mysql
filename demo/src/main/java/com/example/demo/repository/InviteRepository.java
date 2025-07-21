package com.example.demo.repository;

import com.example.demo.model.Invite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InviteRepository extends JpaRepository<Invite, Long> {}
