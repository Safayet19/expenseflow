package com.safayet.friendloantracker.repository;

import com.safayet.friendloantracker.model.Friend;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FriendRepository extends MongoRepository<Friend, String> {
}
