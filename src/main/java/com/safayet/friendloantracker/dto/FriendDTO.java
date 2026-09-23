package com.safayet.friendloantracker.dto;

import com.safayet.friendloantracker.model.Address;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FriendDTO {

    private String id;

    @NotBlank(message = "Friend name cannot be empty")
    @Size(min = 2, max = 80, message = "Friend name must be between 2 and 80 characters")
    private String name;

    @NotBlank(message = "Phone number cannot be empty")
    @Size(min = 7, max = 20, message = "Phone number must be between 7 and 20 characters")
    @Pattern(
            regexp = "^(?=(?:.*\\d){7,})[+0-9()\\- ]+$",
            message = "Enter a valid phone number with at least 7 digits"
    )
    private String phone;

    @Email(message = "Enter a valid email address")
    @Size(max = 120, message = "Email cannot exceed 120 characters")
    private String email;

    @Valid
    private Address address = new Address();

    @Size(max = 1000, message = "Notes cannot exceed 1000 characters")
    private String notes;
}
