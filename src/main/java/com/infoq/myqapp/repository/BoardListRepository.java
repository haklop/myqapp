package com.infoq.myqapp.repository;

import com.infoq.myqapp.domain.trello.BoardList;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BoardListRepository extends MongoRepository<BoardList, String> {
}
