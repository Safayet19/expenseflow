package com.safayet.friendloantracker.repository;

import com.safayet.friendloantracker.model.ContactLog;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ContactLogRepository extends MongoRepository<ContactLog, String> {

    List<ContactLog> findAllByLoanId(String loanId);

    boolean existsByFriendId(String friendId);

    boolean existsByLoanId(String loanId);

    void deleteAllByLoanId(String loanId);
}
