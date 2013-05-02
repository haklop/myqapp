package com.infoq.myqapp.repository;

import com.infoq.myqapp.domain.FeedEntry;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FeedRepository extends MongoRepository<FeedEntry, String> {
}
