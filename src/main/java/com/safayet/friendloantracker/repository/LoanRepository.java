package com.safayet.friendloantracker.repository;

import com.safayet.friendloantracker.model.Loan;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface LoanRepository extends MongoRepository<Loan, String> {

    List<Loan> findAllByFriendId(String friendId);

    boolean existsByFriendId(String friendId);

    List<Loan> findAllByTagIdsContaining(String tagId);

    List<Loan> findAllByIdIn(Collection<String> ids);
}
