package com.melardev.spring.rxsecjwt.config.security;

import com.melardev.spring.rxsecjwt.enums.Role;
import com.melardev.spring.rxsecjwt.service.UserService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Component
public class AppAuthenticationManager implements ReactiveAuthenticationManager {

    @Autowired
    private JwtProvider jwtUtils;

    @Autowired
    UserService userService;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String authToken = authentication.getCredentials().toString();

        String username;
        try {
            username = jwtUtils.getSubjectFromToken(authToken);
        } catch (Exception e) {
            username = null;
        }
        if (username != null && jwtUtils.validateJwtToken(authToken)) {
            return userService.findByUsername(username).flatMap(user -> {
                if (!user.isEnabled())
                    return Mono.empty();

                Claims claims = jwtUtils.getAllClaimsFromToken(authToken);
                List<String> rolesStrings = claims.get("roles", List.class);
                List<Role> roles = new ArrayList<>();
                for (String roleString : rolesStrings) {
                    roles.add(Role.valueOf(roleString));
                }
                Authentication auth = new UsernamePasswordAuthenticationToken(
                        "",
                        null,
                        roles.stream().map(authority -> new SimpleGrantedAuthority(authority.name())).collect(Collectors.toList())
                );
                return Mono.just(auth);
            }).switchIfEmpty(Mono.empty());
        } else {
            return Mono.empty();
        }
    }
}
