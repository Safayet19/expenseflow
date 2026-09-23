package com.safayet.friendloantracker.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "friends")
@NoArgsConstructor
@AllArgsConstructor
public class Friend {

    @Id
    private String id;

    private String name;

    @Indexed
    private String phone;

    @Indexed
    private String email;

    private Address address = new Address();
    private String notes;

    @Transient
    private List<Loan> loans = new ArrayList<>();
}
