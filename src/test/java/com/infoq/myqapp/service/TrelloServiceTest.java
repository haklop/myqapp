package com.infoq.myqapp.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import static org.fest.assertions.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/WEB-INF/spring-servlet.xml")
public class TrelloServiceTest {

    @Resource
    private TrelloService trelloService;

    @Test
    public void testGetToken() {
        //TODO
    }
}
