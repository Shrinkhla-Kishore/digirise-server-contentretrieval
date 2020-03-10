package com.digirise.contentretreival.handler;

import com.digirise.contentretreival.database.Audio;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.sound.sampled.*;
import java.io.File;
import java.io.FileInputStream;
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
public class AudioService {
    private static final Logger s_logger = LoggerFactory.getLogger(AudioService.class);

    public CompletableFuture<Audio> getAudioLocation(String title, String episodeNumber) {
        AudioLocationTask audioLocationTask = new AudioLocationTask(title,episodeNumber);
        return audioLocationTask.getAudioLocation();
    }

    // Read the audio content from a file system storage
    public CompletableFuture<List<AdvancedPlayer>> getAudioContent(String contentLocation) throws IOException, UnsupportedAudioFileException, LineUnavailableException {
        CompletableFuture<List<AdvancedPlayer>> audioContent = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            List<AdvancedPlayer> audioFiles = new ArrayList<>();
            AudioInputStream audioInputStream = null;
            Clip clip = null;
            try {
                List<File> allFiles = Arrays.asList(new File(contentLocation).listFiles());
                for (File fileName : allFiles) {
                    s_logger.info("file to fetch is {}", contentLocation + fileName);
                    if (fileName.toString().contains(".mp3")) {
                        FileInputStream in = new FileInputStream(fileName);
                        AdvancedPlayer player = new AdvancedPlayer(in);
                        audioFiles.add(player);
                    }
                }
                s_logger.info("Total number of audio files found is {}", audioFiles.size());
                audioContent.complete(audioFiles);
            } catch (IOException ioe) {
                s_logger.trace("IOException:{}", ioe.getMessage());
                audioContent.completeExceptionally(ioe);
            }catch (JavaLayerException jle) {
                s_logger.trace("IOException:{}", jle.getMessage());
                audioContent.completeExceptionally(jle);
            }
        });
        return audioContent;
    }
}