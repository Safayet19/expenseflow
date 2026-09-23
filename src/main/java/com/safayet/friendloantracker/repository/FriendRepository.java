package com.safayet.friendloantracker.repository;

import com.safayet.friendloantracker.model.Friend;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FriendRepository extends MongoRepository<Friend, String> {

    boolean existsByPhone(String phone);

    boolean existsByPhoneAndIdNot(String phone, String id);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, String id);
}
