package com.example.socialnetworkspring.security;


import com.example.socialnetworkspring.model.User;
import com.example.socialnetworkspring.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {

        User byEmail = userRepository.findByEmail(s);
        if (byEmail == null) {
            throw new UsernameNotFoundException("User with does not exists");
        }
        return new SpringUser(byEmail);
    }
}
