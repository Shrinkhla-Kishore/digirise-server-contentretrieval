package com.digirise.contentretreival.handler;

import com.digirise.contentretreival.database.Video;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Created by IntelliJ IDEA.
 * Date: 2020-03-07
 * Author: shrinkhlak
 */
@Service
public class VideoService {
    private static final Logger s_logger = LoggerFactory.getLogger(VideoService.class);

    public CompletableFuture<Video> getVideoLocation(String title, String episodeNumber) {
        VideoLocationTask videoLocationTask = new VideoLocationTask(title,episodeNumber);
        return videoLocationTask.getVideoLocation();
    }

    // Read the video content from a file system storage
    public CompletableFuture<Clip> getVideoContent(String contentLocation) throws IOException, UnsupportedAudioFileException, LineUnavailableException {
        CompletableFuture<Clip> videoContent = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            AudioInputStream audioInputStream = null;
            Clip clip = null;
            try {
                List<String> allFiles = Arrays.asList(new File(contentLocation).list());
                for (String fileName : allFiles) {
                    s_logger.info("file to fetch is {}", contentLocation + fileName);
                    audioInputStream = AudioSystem.getAudioInputStream(
                            new File(contentLocation + fileName).getAbsoluteFile());
                    clip = AudioSystem.getClip();
                    clip.open(audioInputStream);
                    videoContent.complete(clip);
                }
            } catch (LineUnavailableException lue) {
                s_logger.trace("LineUnavailableException:{}", lue.getMessage());
                videoContent.completeExceptionally(lue);
            } catch (UnsupportedAudioFileException uafe) {
                s_logger.trace("UnsupportedAudioFileException:{}", uafe.getMessage());
                videoContent.completeExceptionally(uafe);
            } catch (IOException ioe) {
                s_logger.trace("IOException:{}", ioe.getMessage());
                videoContent.completeExceptionally(ioe);
            }
        });
        return videoContent;
    }
}