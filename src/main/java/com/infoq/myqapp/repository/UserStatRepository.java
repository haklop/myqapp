package com.infoq.myqapp.repository;

import com.infoq.myqapp.domain.UserStat;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserStatRepository extends MongoRepository<UserStat, String> {
}
