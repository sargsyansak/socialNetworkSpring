package com.example.socialnetworkspring.repository;

import com.example.socialnetworkspring.model.UserRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserRequestRepository extends JpaRepository<UserRequest, Integer> {
    List<UserRequest> findAllByToId(int id);

    @Transactional
    void deleteByToIdAndFromId(int toId, int fromId);

}
