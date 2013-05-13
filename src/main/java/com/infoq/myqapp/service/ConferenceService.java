package com.infoq.myqapp.service;

import com.infoq.myqapp.domain.Conference;
import com.infoq.myqapp.repository.ConferenceRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ConferenceService {

    @Resource
    private ConferenceRepository conferenceRepository;

    public void createConf(Conference conference) {
        // TODO check conference

        conferenceRepository.save(conference);
    }

    public List<Conference> getAllConfs() {
        return conferenceRepository.findAll(new Sort(Sort.Direction.ASC, "startDate"));
    }
}
