package com.safayet.friendloantracker.controller;

import com.safayet.friendloantracker.dto.ReminderDTO;
import com.safayet.friendloantracker.exception.InvalidOperationException;
import com.safayet.friendloantracker.model.Reminder;
import com.safayet.friendloantracker.services.FriendService;
import com.safayet.friendloantracker.services.LoanService;
import com.safayet.friendloantracker.services.ReminderService;
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

import java.time.LocalDate;
import java.time.ZoneId;

@Controller
@RequiredArgsConstructor
@RequestMapping("/reminders")
public class ReminderController {

    private static final ZoneId APP_ZONE = ZoneId.of("Asia/Dhaka");

    private final ReminderService reminderService;
    private final FriendService friendService;
    private final LoanService loanService;

    @GetMapping
    public String showReminders(Model model) {
        model.addAttribute("reminders", reminderService.getAllReminders());
        model.addAttribute("today", LocalDate.now(APP_ZONE));
        return "reminder-list";
    }

    @GetMapping("/new")
    public String newReminder(Model model) {
        model.addAttribute("reminderDTO", new ReminderDTO());
        addFormData(model);
        return "reminder-form";
    }

    @PostMapping("/save")
    public String saveReminder(
            @Valid @ModelAttribute("reminderDTO") ReminderDTO reminderDTO,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            addFormData(model);
            return "reminder-form";
        }

        try {
            reminderService.saveReminder(reminderDTO);
        } catch (InvalidOperationException exception) {
            bindingResult.reject("form.error", exception.getMessage());
            addFormData(model);
            return "reminder-form";
        }
        return "redirect:/reminders";
    }

    @GetMapping("/edit/{id}")
    public String editReminder(@PathVariable String id, Model model) {
        Reminder reminder = reminderService.getReminderById(id);

        if (reminder == null) {
            return "redirect:/reminders";
        }

        ReminderDTO reminderDTO = new ReminderDTO();
        BeanUtils.copyProperties(reminder, reminderDTO, "friend", "loan");
        model.addAttribute("reminderDTO", reminderDTO);
        addFormData(model);
        return "reminder-form";
    }

    @PostMapping("/complete/{id}")
    public String completeReminder(@PathVariable String id) {
        reminderService.markCompleted(id);
        return "redirect:/reminders";
    }

    @PostMapping("/delete/{id}")
    public String deleteReminder(@PathVariable String id) {
        reminderService.deleteReminder(id);
        return "redirect:/reminders";
    }

    private void addFormData(Model model) {
        model.addAttribute("friends", friendService.getAllFriends());
        model.addAttribute("loans", loanService.getAllLoans());
    }
}
