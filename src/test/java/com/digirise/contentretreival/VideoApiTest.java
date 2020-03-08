package com.digirise.contentretreival;

import com.digirise.contentretreival.pojo.dto.VideoRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
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
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@ExtendWith(SpringExtension.class)

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
class VideoApiTest {
    private static final Logger s_logger = LoggerFactory.getLogger(VideoApiTest.class);
    @Autowired
    private WebApplicationContext webApplicationContext;
    private static final String HOST = "http://localhost:8080";

    @BeforeAll
    static void enableLoggingOfRequestAndResponseForFailingTests() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    void create_video_returns_the_relative_video_location_async() throws Exception {
        VideoRequest videoRequest = new VideoRequest("BigBangTheory");
        videoRequest.setEpisodeNumber(randomUUID());
        String requestBody = new ObjectMapper().writeValueAsString(videoRequest);

        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        MvcResult result = (MvcResult) mockMvc.perform(MockMvcRequestBuilders.post(HOST + "/video/location")
        .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(requestBody)).andDo(MockMvcResultHandlers.print()).andReturn();
            mockMvc.perform(MockMvcRequestBuilders.asyncDispatch(result))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.header().exists("location"));
        if (MockMvcResultMatchers.header().exists("location") != null) {
            s_logger.info("Location for the audio content is {}", result.getResponse().getContentAsString());
        }
    }

    private String randomUUID() {
        return UUID.randomUUID().toString();
    }

    private Map<String, String> createVideoRequestBody(String title) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("title", title);
        return requestBody;
    }
}
