package com.melardev.spring.rxsecjwt.controllers;


import com.melardev.spring.rxsecjwt.config.security.JwtProvider;
import com.melardev.spring.rxsecjwt.dtos.requests.LoginDto;
import com.melardev.spring.rxsecjwt.dtos.responses.AppResponse;
import com.melardev.spring.rxsecjwt.dtos.responses.ErrorResponse;
import com.melardev.spring.rxsecjwt.dtos.responses.LoginSuccessResponse;
import com.melardev.spring.rxsecjwt.dtos.responses.SuccessResponse;
import com.melardev.spring.rxsecjwt.entities.User;
import com.melardev.spring.rxsecjwt.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
public class AuthController {

    @Autowired
    UserService userService;


    @Autowired
    JwtProvider jwtProvider;

    @Autowired
    ReactiveAuthenticationManager authenticationManager;


    @PostMapping("/users")
    public Mono<ResponseEntity<AppResponse>> registerUser(@RequestBody User user) {
        return userService.findByUsername(user.getUsername())
                .map(u -> new ResponseEntity<AppResponse>(new ErrorResponse("Username already taken"), HttpStatus.BAD_REQUEST))
                .switchIfEmpty(
                        userService.save(user)
                                .map(e -> new ResponseEntity<AppResponse>(new SuccessResponse("User registered successfully"), HttpStatus.OK)))
                .doOnError(ex -> userService.save(user)
                        .map(e -> new ResponseEntity<AppResponse>(new SuccessResponse("User registered successfully"), HttpStatus.OK)));
    }


    @PostMapping("/login")
    public Mono<ResponseEntity<AppResponse>> login(@Valid @RequestBody LoginDto loginRequest) {

        return userService.login(loginRequest.getUsername(), loginRequest.getPassword())
                .map(user -> {
                    String jwt = jwtProvider.generateJwtToken((User) user);
                    SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
                    return ResponseEntity.ok((AppResponse) LoginSuccessResponse.build(jwt, (User) user));
                }).defaultIfEmpty(new ResponseEntity<>(new ErrorResponse("Bad credentials"), HttpStatus.FORBIDDEN));
    }
}
