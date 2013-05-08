package com.infoq.myqapp.repository;

import com.infoq.myqapp.domain.UserProfile;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserProfileRepository extends MongoRepository<UserProfile, String> {
}
