package com.infoq.myqapp.repository;

import com.infoq.myqapp.domain.TrelloMember;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TrelloMemberRepository extends MongoRepository<TrelloMember, String> {
}
