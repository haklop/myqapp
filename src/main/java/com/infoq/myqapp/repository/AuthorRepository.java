package com.infoq.myqapp.repository;

import com.infoq.myqapp.domain.trello.Author;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AuthorRepository extends MongoRepository<Author, String> {
}
