package com.infoq.myqapp.repository;

import com.infoq.myqapp.domain.Conference;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ConferenceRepository extends MongoRepository<Conference, String> {
}
