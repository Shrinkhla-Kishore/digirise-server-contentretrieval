package com.digirise.contentretreival.database;

public interface VideoRepo {
    public Video getOrCreateVideoLocation(String videoTitle, String uuid);
}
