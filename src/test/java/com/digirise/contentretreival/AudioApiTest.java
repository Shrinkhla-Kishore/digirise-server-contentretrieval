package com.digirise.contentretreival;

import com.digirise.contentretreival.handler.AudioService;
import com.digirise.contentretreival.pojo.dto.AudioRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import javazoom.jl.player.advanced.AdvancedPlayer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.Assert;
import org.springframework.web.context.WebApplicationContext;

import javax.sound.sampled.Clip;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@ExtendWith(SpringExtension.class)

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
class AudioApiTest {
    private static final Logger s_logger = LoggerFactory.getLogger(AudioApiTest.class);
    @Autowired
    private WebApplicationContext webApplicationContext;
    private static final String HOST = "http://localhost:8080";
    private String videoLocation = null;

    @BeforeAll
    static void enableLoggingOfRequestAndResponseForFailingTests() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    void create_video_returns_the_relative_video_location_async() throws Exception {
        AudioRequest videoRequest = new AudioRequest("audio_water");
        videoRequest.setEpisodeNumber(randomUUID());
        String requestBody = new ObjectMapper().writeValueAsString(videoRequest);

        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        MvcResult result = (MvcResult) mockMvc.perform(MockMvcRequestBuilders.post(HOST + "/audio/location")
        .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(requestBody)).andDo(MockMvcResultHandlers.print()).andReturn();
            mockMvc.perform(MockMvcRequestBuilders.asyncDispatch(result))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.header().exists("location"));
        if (MockMvcResultMatchers.header().exists("location") != null) {
            s_logger.info("Location for the audio content is {}", result.getResponse().getContentAsString());
        }


        videoRequest = new AudioRequest("audio_water");
        videoRequest.setLocation(result.getResponse().getContentAsString());
        requestBody = new ObjectMapper().writeValueAsString(videoRequest);

        MockMvc mockMvc2 = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        MvcResult result2 = (MvcResult) mockMvc2.perform(MockMvcRequestBuilders.post(HOST + "/audio/content")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(requestBody)).andDo(MockMvcResultHandlers.print()).andReturn();
        mockMvc2.perform(MockMvcRequestBuilders.asyncDispatch(result2))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
        s_logger.info("result is : {}", result2.getResponse().getContentAsString());
        Assert.notNull(result2.getResponse().getContentAsByteArray(), "Length should be greater than 0");
    }

    private String randomUUID() {
        return UUID.randomUUID().toString();
    }

    private Map<String, String> createAudioRequestBody(String title) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("title", title);
        return requestBody;
    }
}
