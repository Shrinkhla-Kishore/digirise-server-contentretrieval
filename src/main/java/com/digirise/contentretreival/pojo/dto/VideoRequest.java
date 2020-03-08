package com.digirise.contentretreival.pojo.dto;

import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by IntelliJ IDEA.
 * Date: 2020-02-15
 * Author: shrinkhlak
 */

//@Getter (lazy = true)
//@Setter
@NoArgsConstructor
public class VideoRequest {
    private static final Logger s_logger = LoggerFactory.getLogger(VideoRequest.class);
    private String title;
    private String episodeNumber;
    private String location;

    public VideoRequest(String title){
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getEpisodeNumber() {
        return episodeNumber;
    }

    public void setEpisodeNumber(String episodeNumber) {
        this.episodeNumber = episodeNumber;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
