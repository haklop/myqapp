package com.infoq.myqapp.repository;

import com.infoq.myqapp.domain.TrelloActivity;
import org.springframework.data.mongodb.repository.MongoRepository;

@Deprecated
public interface UserActivityRepository extends MongoRepository<TrelloActivity, String> {
}
