package com.example.demo.repository;

import java.util.List;
import com.example.demo.model.User;

import com.example.demo.model.RoomPublic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomPublicRepository extends JpaRepository<RoomPublic, Long> {
    List<RoomPublic> findAll();
}
