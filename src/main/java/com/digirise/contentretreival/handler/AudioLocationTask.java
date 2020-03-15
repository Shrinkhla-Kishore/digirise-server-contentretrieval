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
    private static final int TIMEOUT_DURATION = 1000;
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
        CompletableFuture<Audio> finalResults = new CompletableFuture<>();
        CompletableFuture<Audio> audioLocation = CompletableFuture.supplyAsync(() -> {
            Audio audio = audioRepo.getOrCreateAudioLocation(title, uuid);
            s_logger.info("Returning with results :)");
            finalResults.complete(audio);
            return audio;
        });
        CompletableFuture<Audio> timeoutFuture = failAfter(TIMEOUT_DURATION);
        audioLocation.applyToEither(timeoutFuture, Function.identity())
                .whenComplete((audio, throwable) -> {
                    s_logger.debug("Timeout {}", throwable.getCause());
                    if (throwable.getCause() instanceof TimeoutException && retries < MAX_RETRIES) {
                        retries++;
                        s_logger.info("Failed to get audio location after {} tries. Trying again !", retries);
                        audioLocation.cancel(true);
                        getAudioLocation();
                    } else if (audio == null && retries >= MAX_RETRIES){
                        s_logger.info("audioRepo location for title " + title + " not found after " + MAX_RETRIES  + " attempts");
                        finalResults.completeExceptionally(new Throwable("Unable to fetch location for title " + title + " after " + MAX_RETRIES +" attempts"));
                        s_logger.info("Returning :( !!!");
                    } else {
                        s_logger.info("Audio found");
                        finalResults.complete(audio);
                    }
                });
        s_logger.info("*** Returning the results ***");
        return finalResults;
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
