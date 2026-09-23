package com.safayet.friendloantracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactLogDTO {

    private String id;

    @NotBlank(message = "Please select a friend")
    private String friendId;

    private String loanId;

    @NotNull(message = "Contact date is required")
    private LocalDate contactDate;

    @NotBlank(message = "Contact method is required")
    @Size(max = 40, message = "Contact method cannot exceed 40 characters")
    private String contactMethod;

    @NotBlank(message = "Discussion cannot be empty")
    @Size(max = 1000, message = "Discussion cannot exceed 1000 characters")
    private String discussion;

    @Size(max = 1000, message = "Friend response cannot exceed 1000 characters")
    private String friendResponse;

    private LocalDate nextContactDate;
}
