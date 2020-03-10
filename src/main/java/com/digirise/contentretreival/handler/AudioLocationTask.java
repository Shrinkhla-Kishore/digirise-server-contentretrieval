package com.digirise.contentretreival.handler;

import com.digirise.contentretreival.ApplicationContextProvider;
import com.digirise.contentretreival.database.Audio;
import com.digirise.contentretreival.database.AudioRepo;
import com.digirise.contentretreival.database.AudioRepoImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;
import java.util.function.Function;

/**
 * Created by IntelliJ IDEA.
 * Date: 2020-03-07
 * Author: shrinkhlak
 */
public class AudioLocationTask {
    private static final Logger s_logger = LoggerFactory.getLogger(AudioLocationTask.class);
    private static final int TIMEOUT_DURATION = 2000;
    private static final int MAX_RETRIES = 3;
    private AudioRepo audioRepo;
    private ScheduledExecutorService timeoutExecutorService = Executors.newScheduledThreadPool(1);
    private int retries = 0;
    private String title;
    private String uuid;

    public AudioLocationTask(String title, String uuid) {
        audioRepo = ApplicationContextProvider.getApplicationContext().getBean("audioRepoImpl", AudioRepoImpl.class);
        this.title = title;
        this.uuid = uuid;
    }

    public CompletableFuture<Audio> getAudioLocation(){
        CompletableFuture<Audio> audioLocation = CompletableFuture.supplyAsync(() -> {
            return audioRepo.getOrCreateAudioLocation(title, uuid);
        });
        CompletableFuture<Audio> timeoutFuture = failAfter(TIMEOUT_DURATION);
        audioLocation.applyToEither(timeoutFuture, Function.identity())
                .exceptionally((throwable) -> {
                    s_logger.debug("Timeout {}", throwable.getCause());
                    if (throwable.getCause() instanceof TimeoutException && retries < MAX_RETRIES) {
                        retries++;
                        s_logger.info("Failed to get audio location after {} tries", retries);
                        getAudioLocation();
                    } else {
                        s_logger.debug("audioRepo location for title" + title + "not found after " + MAX_RETRIES + 1 + "attempts");
                        return null;
                    }
                    return null;
                });
        return audioLocation;
    }

    private CompletableFuture<Audio> failAfter(int timeoutDuration) {
        CompletableFuture<Audio> promise = new CompletableFuture<Audio>();
        timeoutExecutorService.schedule(()-> {
            TimeoutException ex = new TimeoutException("AudioRepoImpl location not found. Timeout after " + TIMEOUT_DURATION);
            promise.completeExceptionally(ex);
        }, TIMEOUT_DURATION, TimeUnit.MILLISECONDS);
        return promise;
    }
}
