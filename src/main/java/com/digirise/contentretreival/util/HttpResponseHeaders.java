package com.digirise.contentretreival.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

/**
 * Created by IntelliJ IDEA.
 * Date: 2020-02-15
 * Author: shrinkhlak
 */
public class HttpResponseHeaders {
    private static final Logger s_logger = LoggerFactory.getLogger(HttpResponseHeaders.class);

    public static HttpHeaders createHttpHeaders(String headerName, String headerValue) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.add(headerName, headerValue);
        return headers;
    }

    public static HttpHeaders createHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        return headers;
    }

}
