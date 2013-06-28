package com.infoq.myqapp.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.infoq.myqapp.domain.TrelloActivity;

public interface UserActivityRepository extends MongoRepository<TrelloActivity, String> {
}
