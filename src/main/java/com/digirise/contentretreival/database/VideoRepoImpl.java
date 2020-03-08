package com.digirise.contentretreival.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by IntelliJ IDEA.
 * Date: 2020-03-07
 * Author: shrinkhlak
 */

@Repository
public class VideoRepoImpl implements VideoRepo{
    private static final Logger s_logger = LoggerFactory.getLogger(VideoRepoImpl.class);
    private static final String s_location = "C:\\Users\\PublicMusic\\";
    private Map<String, Video> videoLocationMap = new ConcurrentHashMap<>();
    private AtomicInteger episoderNumberCounter = new AtomicInteger(0);
    private int sleepTimer = 0;


    @Override
    public Video getOrCreateVideoLocation(String videoTitle, String uuid) {
        Video video = videoLocationMap.get(uuid);
        if (video == null){
            // For the test purpose only. No database is created
            video = new Video();
            video.setTitle(videoTitle);
            video.setEpisodeNumber(episoderNumberCounter.incrementAndGet());
            video.setLocation(s_location);
            videoLocationMap.put(uuid, video);
        }
        try {
            s_logger.info("Putting the current thread {} to sleep", Thread.currentThread().getName());
            Thread.currentThread().sleep(sleepTimer);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return video;

    }
}
