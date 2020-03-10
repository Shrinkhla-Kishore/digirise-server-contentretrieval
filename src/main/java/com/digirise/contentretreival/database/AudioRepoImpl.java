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
public class AudioRepoImpl implements AudioRepo {
    private static final Logger s_logger = LoggerFactory.getLogger(AudioRepoImpl.class);
    private static final String s_location = "C:\\Users\\test\\Music\\";
    private Map<String, Audio> audioLocationMap = new ConcurrentHashMap<>();
    private AtomicInteger episoderNumberCounter = new AtomicInteger(0);
    private int sleepTimer = 0;


    @Override
    public Audio getOrCreateAudioLocation(String audioTitle, String uuid) {
        Audio audio = audioLocationMap.get(uuid);
        if (audio == null){
            // For the test purpose only. No database is created
            audio = new Audio();
            audio.setTitle(audioTitle);
            audio.setEpisodeNumber(episoderNumberCounter.incrementAndGet());
            audio.setLocation(s_location);
            audioLocationMap.put(uuid, audio);
        }
        try {
            s_logger.info("Putting the current thread {} to sleep", Thread.currentThread().getName());
            Thread.currentThread().sleep(sleepTimer);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return audio;

    }
}
