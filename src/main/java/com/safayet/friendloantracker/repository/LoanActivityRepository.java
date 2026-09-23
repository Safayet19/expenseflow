package com.safayet.friendloantracker.repository;

import com.safayet.friendloantracker.model.LoanActivity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface LoanActivityRepository extends MongoRepository<LoanActivity, String> {

    List<LoanActivity> findAllByOrderByActivityDateTimeDesc();

    List<LoanActivity> findAllByLoanIdOrderByActivityDateTimeDesc(String loanId);

    void deleteAllByLoanId(String loanId);
}
