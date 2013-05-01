package com.infoq.myqapp.service;

import com.infoq.myqapp.domain.FeedEntry;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Service
public class FeedService {

    private static final Logger LOG = LoggerFactory.getLogger(FeedService.class);

    public List<FeedEntry> readFeed() throws Exception {
        URL url = new URL("http://www.infoq.com/rss/rss.action?token=ziwI7MykYwU7MxfdOUJtG2HZfe5boFWG");
        XmlReader reader = null;

        List<FeedEntry> feedEntries = new ArrayList<>();
        try {
            reader = new XmlReader(url);
            SyndFeed feed = new SyndFeedInput().build(reader);
            LOG.info("Syndication FeedEntry retrieved, author is : {}", feed.getAuthor());

            for (Object syndEntry : feed.getEntries()) {
                SyndEntry syndFeedEntry = (SyndEntry) syndEntry;

                LOG.info("FeedEntry retrieved : {}, {}", syndFeedEntry.getTitle(), syndFeedEntry.getLink());

                feedEntries.add(buildFeedEntry(syndFeedEntry));

            }
        } finally {
            if (reader != null) {
                reader.close();
            }
        }

        return feedEntries;
    }

    private FeedEntry buildFeedEntry(SyndEntry syndFeedEntry) {
        FeedEntry feedEntry = new FeedEntry();

        feedEntry.setTitle(syndFeedEntry.getTitle());
        feedEntry.setDescription(syndFeedEntry.getDescription().getValue());
        feedEntry.setLink(syndFeedEntry.getLink());
        feedEntry.setPublishedDate(syndFeedEntry.getPublishedDate());
        feedEntry.setCategories(syndFeedEntry.getCategories());

        return feedEntry;
    }
}
