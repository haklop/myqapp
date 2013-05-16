package com.infoq.myqapp.service;

import com.infoq.myqapp.domain.Conference;
import com.infoq.myqapp.repository.ConferenceRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Service
public class ConferenceService {

    @Resource
    private ConferenceRepository conferenceRepository;

    @Resource
    private MongoTemplate mongoTemplate;

    public void createConf(Conference conference) {
        // TODO check conference

        conferenceRepository.save(conference);
    }

    public List<Conference> getAllConfs() {
        return mongoTemplate.find(query(where("endDate").gte(yesterday())).with(new Sort(Sort.Direction.ASC, "startDate")), Conference.class);
    }

    private Date yesterday() {
        return new Date(new Date().getTime() - (1000 * 60 * 60 * 24));
    }
}
