package com.example.socialnetworkspring.repository;

import com.example.socialnetworkspring.model.UserFriend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserFriendRepository extends JpaRepository<UserFriend, Integer> {
    List<UserFriend> findAllByToIdOrFromId(int fromId, int toId);

    @Transactional
    void deleteByToIdAndFromIdOrFromIdAndToId(int ToId, int FromId, int secondToId, int secondFromId);
}
