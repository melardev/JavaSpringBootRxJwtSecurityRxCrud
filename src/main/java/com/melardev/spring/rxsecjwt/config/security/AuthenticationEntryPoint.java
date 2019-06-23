package com.melardev.spring.rxsecjwt.config.security;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.melardev.spring.rxsecjwt.dtos.responses.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.ByteBuffer;

@Component
public class AuthenticationEntryPoint implements ServerAuthenticationEntryPoint {

    @Autowired
    ObjectMapper mapper;

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationEntryPoint.class);


    @Override
    public Mono<Void> commence(ServerWebExchange serverWebExchange, AuthenticationException e) {
        // Called when the user tries to access an endpoint which requires to be authenticated
        // we just return unauthorized, basically when we should send a 401 status code response.
        logger.error("Unauthorized error. Message - {}", e.getMessage());


        ServerHttpResponse res = serverWebExchange.getResponse();
        res.setStatusCode(HttpStatus.UNAUTHORIZED);
        res.getHeaders().set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        DataBuffer buffer;
        try {
            buffer = res.bufferFactory().wrap(ByteBuffer.wrap(mapper.writeValueAsString(new ErrorResponse("You must authenticated")).getBytes()));

        } catch (JsonProcessingException ex) {
            buffer = res.bufferFactory().wrap("{\"success\": false}".getBytes());
        }

        return res.writeAndFlushWith(Mono.just(Mono.just(buffer)));
    }
}