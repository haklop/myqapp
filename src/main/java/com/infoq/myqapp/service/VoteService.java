package com.infoq.myqapp.service;

import com.infoq.myqapp.domain.Conference;
import com.infoq.myqapp.domain.Vote;
import com.infoq.myqapp.repository.VoteRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Service
public class VoteService {

    @Resource
    private VoteRepository voteRepository;

    @Resource
    private MongoTemplate mongoTemplate;

    public void create(Vote vote){
        voteRepository.save(vote);
    }

    public List<Vote> getAllVotes() {
        return mongoTemplate.find(query(where("isOpen").is(true)), Vote.class);
    }

    public void addVoterFor(String newEditorMail, String voterForMail){
        Vote vote = voteRepository.findOne(newEditorMail);
        vote.getVotersForMails().add(voterForMail);
        voteRepository.save(vote);

    }

    public void addVoterAgainst(String newEditorMail, String voterAgainstMail){
        Vote vote = voteRepository.findOne(newEditorMail);
        vote.getVotersAgainstMails().add(voterAgainstMail);
        voteRepository.save(vote);
    }


    public void closeVote(String newEditorMail) {
        Vote vote = voteRepository.findOne(newEditorMail);
        vote.setOpen(false);
    }
}
