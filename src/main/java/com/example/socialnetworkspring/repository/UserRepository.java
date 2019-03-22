package com.example.socialnetworkspring.repository;

import com.example.socialnetworkspring.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByEmail(String email);
    List<User> findAllByIdIsNotLike(int id);
    User findByToken(String token);


}
