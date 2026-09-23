package com.safayet.friendloantracker.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Data
@Document(collection = "loan_activities")
@NoArgsConstructor
@AllArgsConstructor
public class LoanActivity {

    @Id
    private String id;

    @Indexed
    private String loanId;

    private String activityMessage;

    @Indexed
    private LocalDateTime activityDateTime;

    @Transient
    private Loan loan;

    public LoanActivity(Loan loan, String activityMessage) {
        this.loan = loan;
        this.loanId = loan == null ? null : loan.getId();
        this.activityMessage = activityMessage;
        this.activityDateTime = LocalDateTime.now(ZoneId.of("Asia/Dhaka"));
    }
}
