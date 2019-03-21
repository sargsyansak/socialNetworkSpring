package com.example.socialnetworkspring.repository;


import com.example.socialnetworkspring.model.Message;
import com.example.socialnetworkspring.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Integer> {

    List<Message> findAllByFromIdOrToId(User fromId, User toId);

}
