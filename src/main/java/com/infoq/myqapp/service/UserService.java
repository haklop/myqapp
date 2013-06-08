package com.infoq.myqapp.service;

import com.infoq.myqapp.domain.UserProfile;
import com.infoq.myqapp.repository.UserProfileRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class UserService {

    @Resource
    private UserProfileRepository userProfileRepository;

    public List<UserProfile> getAllUsers() {
        return userProfileRepository.findAll();
    }

    public void create(UserProfile userProfile) {
        userProfileRepository.save(userProfile);
    }

    public void delete(String userId) {
        userProfileRepository.delete(userId);
    }

    public void update(String userId, UserProfile userProfile) {
        userProfileRepository.delete(userId);
        userProfileRepository.save(userProfile);
    }

    public boolean isAuthorized(String email){
        UserProfile one = userProfileRepository.findOne(email);
        return one != null;
    }

    public UserProfile get(String email) {
        return userProfileRepository.findOne(email);
    }

    public void save(UserProfile profileFromGoogle) {
        userProfileRepository.save(profileFromGoogle);
    }
}
