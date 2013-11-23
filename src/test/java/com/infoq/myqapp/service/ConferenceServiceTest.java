package com.infoq.myqapp.service;

import com.infoq.myqapp.domain.Conference;
import com.infoq.myqapp.repository.ConferenceRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/WEB-INF/spring-servlet.xml")
public class ConferenceServiceTest {

    @Mock
    private ConferenceRepository conferenceRepository;

    @Mock
    private MongoTemplate mongoTemplate;

    @Autowired
    @InjectMocks
    private ConferenceService conferenceService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateOrUpdate() {
        //Given
        Conference conf = new Conference();
        conf.setLocation("Paris");
        conf.setName("Allo Conf");
        conf.setStartDate(new Date());
        conf.setEndDate(new Date());
        conf.setWebsite("http://google.com");

        //When
        conferenceService.createOrUpdate(conf);

        //Then
        verify(conferenceRepository).save(conf);
        verifyNoMoreInteractions(conferenceRepository);
        verifyZeroInteractions(mongoTemplate);
    }

    @Test
    public void testDelete() {
        //When
        conferenceService.delete("Test");

        //Then
        verify(conferenceRepository).delete(eq("Test"));
        verifyNoMoreInteractions(conferenceRepository);
        verifyZeroInteractions(mongoTemplate);
    }

    @Test
    public void testGetAllFutureConfs() {
        //Given
        //List<Conference> confs = new ArrayList<>();
        //confs
        //when(mongoTemplate.find(any(Query.class), Conference.class)).thenReturn()

        //When
        //conferenceService.getAllFutureConfs();

        //Then
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLowerInvalidMonthValue() {
        conferenceService.getConfsByMonth(2000, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHigherInvalidMonthValue() {
        conferenceService.getConfsByMonth(2000, 13);
    }
}
