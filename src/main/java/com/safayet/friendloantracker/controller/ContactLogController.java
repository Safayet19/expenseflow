package com.safayet.friendloantracker.controller;

import com.safayet.friendloantracker.dto.ContactLogDTO;
import com.safayet.friendloantracker.model.ContactLog;
import com.safayet.friendloantracker.services.ContactLogService;
import com.safayet.friendloantracker.services.FriendService;
import com.safayet.friendloantracker.services.LoanService;
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
@RequestMapping("/contacts")
public class ContactLogController {

    private final ContactLogService contactLogService;
    private final FriendService friendService;
    private final LoanService loanService;

    @GetMapping
    public String showContactLogs(Model model) {
        model.addAttribute("contactLogs", contactLogService.getAllContactLogs());
        return "contact-log-list";
    }

    @GetMapping("/new")
    public String newContactLog(Model model) {
        model.addAttribute("contactLogDTO", new ContactLogDTO());
        addFormData(model);
        return "contact-log-form";
    }

    @PostMapping("/save")
    public String saveContactLog(
            @Valid @ModelAttribute("contactLogDTO") ContactLogDTO contactLogDTO,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            addFormData(model);
            return "contact-log-form";
        }

        contactLogService.saveContactLog(contactLogDTO);
        return "redirect:/contacts";
    }

    @GetMapping("/edit/{id}")
    public String editContactLog(@PathVariable String id, Model model) {
        ContactLog contactLog = contactLogService.getContactLogById(id);

        if (contactLog == null) {
            return "redirect:/contacts";
        }

        ContactLogDTO contactLogDTO = new ContactLogDTO();
        BeanUtils.copyProperties(contactLog, contactLogDTO, "friend", "loan");
        model.addAttribute("contactLogDTO", contactLogDTO);
        addFormData(model);
        return "contact-log-form";
    }

    @GetMapping("/delete/{id}")
    public String deleteContactLog(@PathVariable String id) {
        contactLogService.deleteContactLog(id);
        return "redirect:/contacts";
    }

    private void addFormData(Model model) {
        model.addAttribute("friends", friendService.getAllFriends());
        model.addAttribute("loans", loanService.getAllLoans());
    }
}
