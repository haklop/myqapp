package com.infoq.myqapp.repository;

import com.infoq.myqapp.domain.Vote;
import com.infoq.myqapp.domain.trello.Item;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface VoteRepository extends MongoRepository<Vote, String> {
}
