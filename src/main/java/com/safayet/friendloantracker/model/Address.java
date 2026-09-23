package com.safayet.friendloantracker.model;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    @Size(max = 200, message = "Address cannot exceed 200 characters")
    private String fullAddress;
}
