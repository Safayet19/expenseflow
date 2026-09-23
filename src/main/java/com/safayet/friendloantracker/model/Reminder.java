package com.safayet.friendloantracker.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Document(collection = "reminders")
@CompoundIndex(name = "date_completed_idx", def = "{'reminderDate': 1, 'completed': 1}")
@NoArgsConstructor
@AllArgsConstructor
public class Reminder {

    @Id
    private String id;

    @Indexed
    private String friendId;

    @Indexed
    private String loanId;

    private LocalDate reminderDate;
    private LocalTime reminderTime;
    private String message;
    private boolean completed;

    @Transient
    private Friend friend;

    @Transient
    private Loan loan;
}
