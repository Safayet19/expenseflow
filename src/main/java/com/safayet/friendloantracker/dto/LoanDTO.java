package com.safayet.friendloantracker.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanDTO {

    private String id;

    @NotBlank(message = "Please select a friend")
    private String friendId;

    @NotBlank(message = "Loan type cannot be empty")
    @Size(max = 40, message = "Loan type cannot exceed 40 characters")
    private String loanType;

    @Size(max = 120, message = "Item name cannot exceed 120 characters")
    private String itemName;

    @PositiveOrZero(message = "Amount cannot be negative")
    @DecimalMax(value = "999999999.99", message = "Amount is too large")
    private Double amount;

    @PositiveOrZero(message = "Quantity cannot be negative")
    @Max(value = 100000, message = "Quantity is too large")
    private Integer quantity;

    @NotNull(message = "Borrow date is required")
    @PastOrPresent(message = "Borrow date cannot be in the future")
    private LocalDate borrowDate;

    @NotNull(message = "Expected return date is required")
    private LocalDate expectedReturnDate;

    @NotBlank(message = "Status cannot be empty")
    private String status;

    private Set<String> tagIds = new LinkedHashSet<>();

    @Size(max = 1000, message = "Notes cannot exceed 1000 characters")
    private String notes;
}
