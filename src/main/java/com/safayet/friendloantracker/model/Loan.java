package com.safayet.friendloantracker.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Data
@Document(collection = "loans")
@CompoundIndexes({
        @CompoundIndex(name = "friend_status_idx", def = "{'friendId': 1, 'status': 1}")
})
@NoArgsConstructor
public class Loan {

    @Id
    private String id;

    @Indexed
    private String friendId;

    private String loanType;
    private String itemName;
    private Double amount;
    private Integer quantity;
    private LocalDate borrowDate;

    @Indexed
    private LocalDate expectedReturnDate;

    private String status;
    private String legacyTag;
    private String notes;
    private Set<String> tagIds = new LinkedHashSet<>();

    @Transient
    private Friend friend;

    @Transient
    private Set<Tag> tags = new LinkedHashSet<>();

    @Transient
    private List<Reminder> reminders = new ArrayList<>();

    @Transient
    private List<ContactLog> contactLogs = new ArrayList<>();

    @Transient
    private List<LoanActivity> activities = new ArrayList<>();

    @Transient
    public boolean isOverdue() {
        return expectedReturnDate != null
                && expectedReturnDate.isBefore(LocalDate.now())
                && !"RETURNED".equalsIgnoreCase(status);
    }
}
