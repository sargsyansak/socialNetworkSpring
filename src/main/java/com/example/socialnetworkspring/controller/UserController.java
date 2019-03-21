package com.example.socialnetworkspring.controller;

import com.example.socialnetworkspring.model.User;
import com.example.socialnetworkspring.model.UserFriend;
import com.example.socialnetworkspring.model.UserRequest;
import com.example.socialnetworkspring.repository.UserFriendRepository;
import com.example.socialnetworkspring.repository.UserRepository;
import com.example.socialnetworkspring.repository.UserRequestRepository;
import com.example.socialnetworkspring.security.SpringUser;
import com.example.socialnetworkspring.service.EmailService;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class UserController {

    @Value("${image.upload.dir}")
    private String imageUploadDir;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserFriendRepository userFriendRepository;
    @Autowired
    private UserRequestRepository userRequestRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;


    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/loginSuccess")
    public String loginSuccess() {
        return "redirect:/user";
    }

    @GetMapping("/user")
    public String userPage(@AuthenticationPrincipal SpringUser springUser, ModelMap modelMap) {
        List<UserFriend> friends = userFriendRepository.findAllByToIdOrFromId(springUser.getUser().getId(), springUser.getUser().getId());
        List<User> userFriends = new ArrayList<>();
        for (UserFriend friend : friends) {
            if (friend.getFrom().getId() == springUser.getUser().getId()) {
                userFriends.add(friend.getTo());
            } else if (friend.getTo().getId() == springUser.getUser().getId()) {
                userFriends.add(friend.getFrom());
            }
        }
        modelMap.addAttribute("user", springUser.getUser());
        modelMap.addAttribute("users", userRepository.findAllByIdIsNotLike(springUser.getUser().getId()));
        modelMap.addAttribute("requests", userRequestRepository.findAllByToId(springUser.getUser().getId()));
        modelMap.addAttribute("friends", userFriends);
        return "userPage";
    }


    @GetMapping("/register")
    public String registerView() {
        return "register";
    }

    @PostMapping("/register")
    public String userRegister(@ModelAttribute User user, @RequestParam("pic") MultipartFile file) throws IOException {
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        File picture = new File(imageUploadDir + File.separator + fileName);
        file.transferTo(picture);
        user.setPicUrl(fileName);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return "redirect:register";
    }

    @GetMapping("/user/getImage")
    public void getImageAsByteArray(HttpServletResponse response, @RequestParam("picUrl") String picUrl) throws IOException {
        InputStream in = new FileInputStream(imageUploadDir + File.separator + picUrl);
        response.setContentType(MediaType.IMAGE_JPEG_VALUE);
        IOUtils.copy(in, response.getOutputStream());
    }

    @GetMapping("/sendRequest")
    public String request(@RequestParam("id") int id, @AuthenticationPrincipal SpringUser springUser) {
        UserRequest requests = new UserRequest();
        requests.setTo(userRepository.getOne(id));
        requests.setFrom(springUser.getUser());
        requests.setDate(new Date());
        userRequestRepository.save(requests);

        return "redirect:/user";
    }

    @GetMapping("/acceptOrReject")
    public String acceptOrReject(@RequestParam("id") int id, @RequestParam("action") String action, @AuthenticationPrincipal SpringUser springUser) {
        User fromUser = userRepository.getOne(id);
        if (action.equals("accept") && fromUser != null) {
            UserFriend userFriend = new UserFriend();
            userFriend.setFrom(fromUser);
            userFriend.setTo(springUser.getUser());
            userFriend.setDate(new Date());
            userFriendRepository.save(userFriend);
            userRequestRepository.deleteByToIdAndFromId(springUser.getUser().getId(), fromUser.getId());
        } else if (action.equals("reject") && userRepository.getOne(id) != null) {
            userRequestRepository.deleteByToIdAndFromId(fromUser.getId(), springUser.getUser().getId());
        }
        return "redirect:/user";
    }


    @GetMapping("/remove")
    public String deleteFriend(@RequestParam("id") int id, @AuthenticationPrincipal SpringUser springUser) {
        userFriendRepository.deleteByToIdAndFromIdOrFromIdAndToId(id, springUser.getUser().getId(), id, springUser.getUser().getId());
        return "redirect:/user";
    }

}
