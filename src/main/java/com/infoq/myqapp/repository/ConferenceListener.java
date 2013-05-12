package com.infoq.myqapp.repository;

import com.infoq.myqapp.domain.Conference;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;

public class ConferenceListener extends AbstractMongoEventListener<Conference> {

    @Override
    public void onBeforeConvert(Conference source) {
        source.setId(source.getName() + "-" + source.getLocation() + "-" + source.getStartDate());
    }
}
