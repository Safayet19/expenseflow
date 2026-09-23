package com.safayet.friendloantracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TagDTO {

    private String id;

    @NotBlank(message = "Tag name cannot be empty")
    @Size(min = 2, max = 40, message = "Tag name must be between 2 and 40 characters")
    private String name;

    @Size(max = 300, message = "Description cannot exceed 300 characters")
    private String description;
}
