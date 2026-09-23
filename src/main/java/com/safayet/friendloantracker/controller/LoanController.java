package com.safayet.friendloantracker.controller;

import com.safayet.friendloantracker.dto.LoanDTO;
import com.safayet.friendloantracker.exception.InvalidOperationException;
import com.safayet.friendloantracker.model.Loan;
import com.safayet.friendloantracker.model.Tag;
import com.safayet.friendloantracker.services.FriendService;
import com.safayet.friendloantracker.services.LoanService;
import com.safayet.friendloantracker.services.TagService;
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

import java.util.LinkedHashSet;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/loans")
public class LoanController {

    private final LoanService loanService;
    private final FriendService friendService;
    private final TagService tagService;

    @GetMapping
    public String showLoans(Model model) {
        model.addAttribute("loans", loanService.getAllLoans());
        return "loan-list";
    }

    @GetMapping("/new")
    public String newLoan(Model model) {
        model.addAttribute("loanDTO", new LoanDTO());
        addFormData(model);
        return "loan-form";
    }

    @PostMapping("/save")
    public String saveLoan(
            @Valid @ModelAttribute("loanDTO") LoanDTO loanDTO,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            addFormData(model);
            return "loan-form";
        }

        try {
            loanService.saveLoan(loanDTO);
        } catch (InvalidOperationException exception) {
            bindingResult.reject("form.error", exception.getMessage());
            addFormData(model);
            return "loan-form";
        }
        return "redirect:/loans";
    }

    @GetMapping("/edit/{id}")
    public String editLoan(@PathVariable String id, Model model) {
        Loan loan = loanService.getLoanById(id);

        if (loan == null) {
            return "redirect:/loans";
        }

        LoanDTO loanDTO = new LoanDTO();
        BeanUtils.copyProperties(
                loan,
                loanDTO,
                "friend",
                "tags",
                "reminders",
                "contactLogs",
                "activities",
                "legacyTag"
        );

        if (loan.getFriend() != null) {
            loanDTO.setFriendId(loan.getFriend().getId());
        }

        loanDTO.setTagIds(
                loan.getTags().stream()
                        .map(Tag::getId)
                        .collect(Collectors.toCollection(LinkedHashSet::new))
        );

        model.addAttribute("loanDTO", loanDTO);
        addFormData(model);
        return "loan-form";
    }

    @GetMapping("/details/{id}")
    public String loanDetails(@PathVariable String id, Model model) {
        Loan loan = loanService.getLoanById(id);

        if (loan == null) {
            return "redirect:/loans";
        }

        model.addAttribute("loan", loan);
        return "loan-details";
    }

    @PostMapping("/delete/{id}")
    public String deleteLoan(@PathVariable String id) {
        loanService.deleteLoan(id);
        return "redirect:/loans";
    }

    private void addFormData(Model model) {
        model.addAttribute("friends", friendService.getAllFriends());
        model.addAttribute("tags", tagService.getAllTags());
    }
}
