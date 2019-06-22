package com.melardev.spring.rxsecjwt.service;


import com.melardev.spring.rxsecjwt.entities.User;
import com.melardev.spring.rxsecjwt.enums.Role;
import com.melardev.spring.rxsecjwt.repositories.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class UserService implements ReactiveUserDetailsService {

    @Autowired
    private UsersRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return userRepository.findByUsername(username)
                // .switchIfEmpty(Mono.defer(() -> Mono.error(new UsernameNotFoundException("User Not Found"))))
                .switchIfEmpty(Mono.empty())
                .map(e -> e);
    }

    public Long countSync() {
        return userRepository.count().block();
    }

    public Mono<User> save(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRole() == null)
            user.setRole(Role.ROLE_USER);

        return userRepository.save(user);
    }

    public Flux<User> saveAll(User... users) {
        if (users.length == 0)
            return Flux.empty();

        Flux<User> savedUsers = Flux.empty();
        for (int i = 0; i < users.length; i++) {
            savedUsers = savedUsers.concatWith(save(users[i]));
        }
        return savedUsers;
    }

    public Mono<UserDetails> login(String username, String password) {
        return findByUsername(username)
                .flatMap(e -> passwordEncoder.matches(password, e.getPassword()) ? Mono.just(e) : Mono.empty())
                .switchIfEmpty(Mono.empty());
    }
}
