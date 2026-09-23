package com.safayet.friendloantracker.repository;

import com.safayet.friendloantracker.model.Tag;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TagRepository extends MongoRepository<Tag, String> {
}
