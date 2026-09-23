package com.safayet.friendloantracker.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Data
@Document(collection = "contact_logs")
@NoArgsConstructor
@AllArgsConstructor
public class ContactLog {

    @Id
    private String id;

    @Indexed
    private String friendId;

    @Indexed
    private String loanId;

    private LocalDate contactDate;
    private String contactMethod;
    private String discussion;
    private String friendResponse;
    private LocalDate nextContactDate;

    @Transient
    private Friend friend;

    @Transient
    private Loan loan;
}
