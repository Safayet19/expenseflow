package com.safayet.friendloantracker.controller;

import com.safayet.friendloantracker.dto.TagDTO;
import com.safayet.friendloantracker.model.Tag;
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

@Controller
@RequiredArgsConstructor
@RequestMapping("/tags")
public class TagController {

    private final TagService tagService;

    @GetMapping
    public String showTags(Model model) {
        model.addAttribute("tags", tagService.getAllTags());
        return "tag-list";
    }

    @GetMapping("/new")
    public String newTag(Model model) {
        model.addAttribute("tagDTO", new TagDTO());
        return "tag-form";
    }

    @PostMapping("/save")
    public String saveTag(
            @Valid @ModelAttribute("tagDTO") TagDTO tagDTO,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "tag-form";
        }

        tagService.saveTag(tagDTO);
        return "redirect:/tags";
    }

    @GetMapping("/edit/{id}")
    public String editTag(@PathVariable String id, Model model) {
        Tag tag = tagService.getTagById(id);
        if (tag == null) {
            return "redirect:/tags";
        }

        TagDTO tagDTO = new TagDTO();
        BeanUtils.copyProperties(tag, tagDTO);
        model.addAttribute("tagDTO", tagDTO);
        return "tag-form";
    }

    @GetMapping("/delete/{id}")
    public String deleteTag(@PathVariable String id) {
        tagService.deleteTag(id);
        return "redirect:/tags";
    }
}
