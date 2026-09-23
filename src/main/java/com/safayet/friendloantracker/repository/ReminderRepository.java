package com.safayet.friendloantracker.repository;

import com.safayet.friendloantracker.model.Reminder;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ReminderRepository extends MongoRepository<Reminder, String> {

    List<Reminder> findAllByLoanId(String loanId);

    boolean existsByFriendId(String friendId);

    void deleteAllByLoanId(String loanId);
}
