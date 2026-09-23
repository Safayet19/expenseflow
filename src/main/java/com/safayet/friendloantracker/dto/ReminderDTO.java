package com.safayet.friendloantracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReminderDTO {

    private String id;

    @NotBlank(message = "Please select a friend")
    private String friendId;

    private String loanId;

    @NotNull(message = "Reminder date is required")
    private LocalDate reminderDate;

    @NotNull(message = "Reminder time is required")
    private LocalTime reminderTime;

    @NotBlank(message = "Reminder message cannot be empty")
    @Size(max = 300, message = "Reminder message cannot exceed 300 characters")
    private String message;

    private boolean completed;
}
