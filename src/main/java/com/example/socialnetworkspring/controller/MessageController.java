package com.example.socialnetworkspring.controller;


import com.example.socialnetworkspring.model.Message;
import com.example.socialnetworkspring.model.User;
import com.example.socialnetworkspring.repository.MessageRepository;
import com.example.socialnetworkspring.repository.UserRepository;
import com.example.socialnetworkspring.security.SpringUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Date;

@Controller
public class MessageController {

    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private UserRepository userRepository;
    private User user;

    @GetMapping("/messagePage")
    public String messagePage(@RequestParam("id") int id, ModelMap modelMap) {
        User user = userRepository.getOne(id);
        this.user = user;
        modelMap.addAttribute("messages", messageRepository.findAllByFromId_IdOrToId_Id(id, id));
        modelMap.addAttribute("toId", user);
        return "messagePage";
    }

    @PostMapping("/sendMessage")
    public String message(@ModelAttribute Message message, @AuthenticationPrincipal SpringUser springUser, RedirectAttributes redirectAttributes) {
        message.setFromId(springUser.getUser());
        message.setToId(user);
        message.setDate(new Date());
        messageRepository.save(message);
        redirectAttributes.addAttribute("id",user.getId());
        return "redirect:/messagePage";
    }
}
