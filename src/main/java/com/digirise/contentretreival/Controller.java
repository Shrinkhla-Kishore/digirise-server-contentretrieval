package com.digirise.contentretreival;

import com.digirise.contentretreival.handler.VideoService;
import com.digirise.contentretreival.pojo.dto.VideoRequest;
import com.digirise.contentretreival.util.HttpResponseHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@RestController
class Controller {
    private static final Logger s_logger = LoggerFactory.getLogger(Controller.class);
    @Autowired
    private VideoService videoService;

    @PostMapping("/video/location")
    public CompletableFuture<ResponseEntity<String>> videoLocationRequest(@RequestBody VideoRequest videoRequest) {
        s_logger.info("Request contains, title = {}, episode number = {} and content = {}",
                videoRequest.getTitle(),videoRequest.getEpisodeNumber(), videoRequest.getLocation());
        CompletableFuture<ResponseEntity<String>> videoCf = videoService.getVideoLocation
                (videoRequest.getTitle(), videoRequest.getEpisodeNumber())
                .thenApply((video) -> {
                    StringBuilder videoLocation = new StringBuilder();
                    videoLocation.append(video.getLocation());
                    s_logger.info("videoLocation is {}", videoLocation.toString());
                    ResponseEntity<String> responseEntity;
                    if (videoLocation.toString() != null && videoLocation.length() > 0) {
                        responseEntity  = new ResponseEntity<>(videoLocation.toString(), HttpResponseHeaders.createHttpHeaders("location", videoLocation.toString()), HttpStatus.CREATED);
                    } else {
                        responseEntity  = new ResponseEntity<>(null, HttpResponseHeaders.createHttpHeaders("location", ""), HttpStatus.BAD_REQUEST);
                    }
                    return responseEntity;
                });
        return videoCf;
    }

    @RequestMapping(value = "/video/content", method = RequestMethod.POST)
    public CompletableFuture<ResponseEntity<Clip>> getVideoContent(@RequestBody VideoRequest videoRequest
    ) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        s_logger.info("VideoRepoImpl location uri is {}", videoRequest.getLocation());
        return videoService.getVideoContent(videoRequest.getLocation())
                .handle((clip, throwable) -> {
                    ResponseEntity<Clip> responseEntity;
                    if (clip != null)
                        responseEntity = new ResponseEntity<>(clip, HttpResponseHeaders.createHttpHeaders(), HttpStatus.OK);
                    else
                        responseEntity = new ResponseEntity<>(null, HttpResponseHeaders.createHttpHeaders(), HttpStatus.NO_CONTENT);

                    return responseEntity;
                });
    }
 }

