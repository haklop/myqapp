package com.infoq.myqapp.service;

import com.infoq.myqapp.domain.FeedEntry;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/config/spring/spring-servlet.xml")
public class FeedServiceTest {

    @Autowired
    private FeedService feedService;

    @Test
    public void testReadFeed() throws Exception {
        List<FeedEntry> feedEntries = feedService.readFeed();

        assertThat(feedEntries).isNotEmpty();
    }
}
