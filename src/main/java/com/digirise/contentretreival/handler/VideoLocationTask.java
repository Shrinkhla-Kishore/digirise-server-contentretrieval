package com.digirise.contentretreival.handler;

import com.digirise.contentretreival.ApplicationContextProvider;
import com.digirise.contentretreival.database.Video;
import com.digirise.contentretreival.database.VideoRepo;
import com.digirise.contentretreival.database.VideoRepoImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;
import java.util.function.Function;

/**
 * Created by IntelliJ IDEA.
 * Date: 2020-03-07
 * Author: shrinkhlak
 */
public class VideoLocationTask {
    private static final Logger s_logger = LoggerFactory.getLogger(VideoLocationTask.class);
    private static final int TIMEOUT_DURATION = 2000;
    private static final int MAX_RETRIES = 3;
    private VideoRepo videoRepo;
    private ScheduledExecutorService timeoutExecutorService = Executors.newScheduledThreadPool(1);
    private int retries = 0;
    private String title;
    private String uuid;

    public VideoLocationTask(String title, String uuid) {
        videoRepo = ApplicationContextProvider.getApplicationContext().getBean("videoRepoImpl", VideoRepoImpl.class);
        this.title = title;
        this.uuid = uuid;
    }

    public CompletableFuture<Video> getVideoLocation(){
        CompletableFuture<Video> videoLocation = CompletableFuture.supplyAsync(() -> {
            return videoRepo.getOrCreateVideoLocation(title, uuid);
        });
        CompletableFuture<Video> timeoutFuture = failAfter(TIMEOUT_DURATION);
        videoLocation.applyToEither(timeoutFuture, Function.identity())
                .exceptionally((throwable) -> {
                    s_logger.debug("Timeout {}", throwable.getCause());
                    if (throwable.getCause() instanceof TimeoutException && retries < MAX_RETRIES) {
                        retries++;
                        s_logger.info("Failed to get video location after {} tries", retries);
                        getVideoLocation();
                    } else {
                        s_logger.debug("videoRepo location for title" + title + "not found after " + MAX_RETRIES + 1 + "attempts");
                        return null;
                    }
                    return null;
                });
        return videoLocation;
    }

    private CompletableFuture<Video> failAfter(int timeoutDuration) {
        CompletableFuture<Video> promise = new CompletableFuture<Video>();
        timeoutExecutorService.schedule(()-> {
            TimeoutException ex = new TimeoutException("VideoRepoImpl location not found. Timeout after " + TIMEOUT_DURATION);
            promise.completeExceptionally(ex);
        }, TIMEOUT_DURATION, TimeUnit.MILLISECONDS);
        return promise;
    }
}
