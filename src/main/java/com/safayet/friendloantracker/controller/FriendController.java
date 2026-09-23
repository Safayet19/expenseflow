package com.safayet.friendloantracker.controller;

import com.safayet.friendloantracker.dto.FriendDTO;
import com.safayet.friendloantracker.model.Friend;
import com.safayet.friendloantracker.services.FriendService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/friends")
public class FriendController {

    private final FriendService friendService;

    @GetMapping
    public String showFriends(Model model) {
        model.addAttribute("friends", friendService.getAllFriends());
        return "friend-list";
    }

    @GetMapping("/new")
    public String newFriend(Model model) {
        model.addAttribute("friendDTO", new FriendDTO());
        return "friend-form";
    }

    @PostMapping("/save")
    public String saveFriend(
            @Valid @ModelAttribute("friendDTO") FriendDTO friendDTO,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "friend-form";
        }

        friendService.saveFriend(friendDTO);
        return "redirect:/friends";
    }

    @GetMapping("/edit/{id}")
    public String editFriend(@PathVariable String id, Model model) {
        Friend friend = friendService.getFriendById(id);

        if (friend == null) {
            return "redirect:/friends";
        }

        FriendDTO friendDTO = new FriendDTO();
        BeanUtils.copyProperties(friend, friendDTO, "loans");
        model.addAttribute("friendDTO", friendDTO);
        return "friend-form";
    }

    @GetMapping("/delete/{id}")
    public String deleteFriend(@PathVariable String id) {
        friendService.deleteFriend(id);
        return "redirect:/friends";
    }
}
