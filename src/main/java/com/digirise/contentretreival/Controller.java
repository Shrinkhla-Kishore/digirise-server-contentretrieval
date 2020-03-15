package com.digirise.contentretreival;

import com.digirise.contentretreival.handler.AudioService;
import com.digirise.contentretreival.pojo.dto.AudioRequest;
import com.digirise.contentretreival.util.HttpResponseHeaders;
import javazoom.jl.player.advanced.AdvancedPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
class Controller {
    private static final Logger s_logger = LoggerFactory.getLogger(Controller.class);
    @Autowired
    private AudioService audioService;

    @PostMapping("/audio/location")
    public CompletableFuture<ResponseEntity<String>> audioLocation(@RequestBody AudioRequest audioRequest) {
        s_logger.info("Request contains, title = {}, episode number = {} and content = {}",
                audioRequest.getTitle(),audioRequest.getEpisodeNumber(), audioRequest.getLocation());
        CompletableFuture<ResponseEntity<String>> audioCf = audioService.getAudioLocation
                (audioRequest.getTitle(), audioRequest.getEpisodeNumber())
                .handle((audio, throwable) -> {
                    s_logger.info("Inside handle !!!");
                    ResponseEntity<String> responseEntity;
                    if (throwable == null && audio != null) {
                        s_logger.info("audioLocation is {}", audio.getLocation());
                        responseEntity  = new ResponseEntity<>(audio.getLocation(), HttpResponseHeaders.createHttpHeaders("location", audio.getLocation()), HttpStatus.CREATED);
                    } else {
                        s_logger.info("Received exception {}", throwable.getLocalizedMessage());
                        responseEntity  = new ResponseEntity<>(null, HttpResponseHeaders.createHttpHeaders("location", ""), HttpStatus.BAD_REQUEST);
                    }
                    return responseEntity;
                });
        return audioCf;
    }

    @RequestMapping(value = "/audio/content", method = RequestMethod.POST)
    public CompletableFuture<ResponseEntity<List<AdvancedPlayer>>> getAudioContent(@RequestBody AudioRequest audioRequest
    ) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        s_logger.info("AudioRepoImpl location uri is {}", audioRequest.getLocation());
        return audioService.getAudioContent(audioRequest.getLocation())
                .handle((advancedPlayers, throwable) -> {
                    ResponseEntity<List<AdvancedPlayer>> responseEntity;
                    if (advancedPlayers != null) {
                        s_logger.info("Size of the playlist is {}", advancedPlayers.size());
                        responseEntity = new ResponseEntity<>(advancedPlayers, HttpResponseHeaders.createHttpHeaders(), HttpStatus.OK);
                    } else
                        responseEntity = new ResponseEntity<>(null, HttpResponseHeaders.createHttpHeaders(), HttpStatus.NO_CONTENT);

                    return responseEntity;
                });
    }
 }

