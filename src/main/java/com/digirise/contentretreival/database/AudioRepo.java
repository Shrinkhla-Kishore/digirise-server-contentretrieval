package com.digirise.contentretreival.database;

public interface AudioRepo {
    public Audio getOrCreateAudioLocation(String videoTitle, String uuid);
}
