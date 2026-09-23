package com.safayet.friendloantracker.controller;

import com.safayet.friendloantracker.services.LoanActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/activities")
public class LoanActivityController {

    private final LoanActivityService loanActivityService;

    @GetMapping
    public String showActivities(Model model) {
        model.addAttribute("activities", loanActivityService.getAllActivities());
        return "activity-list";
    }
}
