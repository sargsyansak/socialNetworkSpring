package com.example.socialnetworkspring.security;


import com.example.socialnetworkspring.model.User;
import org.springframework.security.core.authority.AuthorityUtils;

public class SpringUser extends org.springframework.security.core.userdetails.User {

    private User user;

    public SpringUser(User user) {
        super(user.getEmail(), user.getPassword(), AuthorityUtils.createAuthorityList("user"));
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
