package com.example.socialnetworkspring.controller;

import com.example.socialnetworkspring.model.User;
import com.example.socialnetworkspring.repository.UserRepository;
import com.example.socialnetworkspring.security.SpringUser;
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
import java.util.List;

@Controller
public class UserController {

    @Value("${image.upload.dir}")
    private String imageUploadDir;

    @Autowired
    private UserRepository userRepository;

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
        List<Integer> allFriendRequests = userRepository.findAllFriendRequests(springUser.getUser().getId());
        List<Integer> allFriends = userRepository.findAllFriends(springUser.getUser().getId());
        List<Integer> all = new ArrayList<>(allFriends);
        all.addAll(userRepository.findAllFriendsSecond(springUser.getUser().getId()));
        List<User> allById = userRepository.findAllById(allFriendRequests);
        List<User> allFriend = userRepository.findAllById(all);

        modelMap.addAttribute("user", springUser.getUser());
        modelMap.addAttribute("users", userRepository.findAll());
        modelMap.addAttribute("requests", allById);
        modelMap.addAttribute("friends", allFriend);
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
        User one = userRepository.getOne(id);
        if (one != null) {
            userRepository.saveFriendRequest(springUser.getUser().getId(), one.getId());
        }
        return "redirect:/user";
    }

    @GetMapping("/acceptOrReject")
    public String acceptOrReject(@RequestParam("id") int id, @RequestParam("action") String action, @AuthenticationPrincipal SpringUser springUser) {
        if (action.equals("accept") && userRepository.getOne(id) != null) {
            userRepository.addFriend(springUser.getUser().getId(), id);
            userRepository.removeRequest(id, springUser.getUser().getId());
        } else if (action.equals("reject") && userRepository.getOne(id) != null) {
            userRepository.removeRequest(id, springUser.getUser().getId());
        }
        return "redirect:/user";
    }


    @GetMapping("/remove")
    public String deleteFriend(@RequestParam("id") int id, @AuthenticationPrincipal SpringUser springUser) {
        userRepository.deleteFriendById(id, springUser.getUser().getId());
        userRepository.deleteUserFriendById(id, springUser.getUser().getId());
        return "redirect:/user";
    }

}
