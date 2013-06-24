package com.infoq.myqapp.service;

import com.infoq.myqapp.domain.Conference;
import com.infoq.myqapp.repository.ConferenceRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Calendar;
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

    public void createOrUpdate(Conference conference) {
        // TODO check conference

        conferenceRepository.save(conference);
    }

    public void delete(String conferenceId) {
        conferenceRepository.delete(conferenceId);
    }

    public List<Conference> getAllFutureConfs() {
        return mongoTemplate.find(query(where("endDate").gte(yesterday())).with(new Sort(Sort.Direction.ASC, "startDate")), Conference.class);
    }

    /**
     * @param year
     * @param month Month value is 1-based. e.g., 1 for January.
     * @return
     */
    public List<Conference> getConfsByMonth(int year, int month) {
        if (month < 1 || month > 12) {
            throw new RuntimeException("Month value is 1-based. e.g., 1 for January");
        }

        Query query = query(monthCriteria(year, month));
        return mongoTemplate.find(query, Conference.class);
    }

    public List<Conference> getConfsByTimestamp(long start, long end) {
        Criteria criteria = where("startDate").gte(new Date(start)).orOperator(where("endDate").lte(new Date(end)));
        return mongoTemplate.find(query(criteria), Conference.class);
    }

    private Date yesterday() {
        return new Date(new Date().getTime() - (1000 * 60 * 60 * 24));
    }

    private Criteria monthCriteria(int year, int month) {
        Calendar firstDayOfMonth = Calendar.getInstance();
        firstDayOfMonth.set(year, month - 1, 1);

        Calendar lastDayOfMonth = Calendar.getInstance();
        lastDayOfMonth.set(year, month - 1, firstDayOfMonth.getActualMaximum(Calendar.DAY_OF_MONTH));

        return where("startDate").gte(firstDayOfMonth.getTime()).orOperator(where("endDate").lte(lastDayOfMonth.getTime()));
    }
}
