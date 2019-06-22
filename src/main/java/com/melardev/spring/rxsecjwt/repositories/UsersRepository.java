package com.melardev.spring.rxsecjwt.repositories;


import com.melardev.spring.rxsecjwt.entities.User;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface UsersRepository extends ReactiveCrudRepository<User, Long> {

    Mono<User> findByUsername(String username);

}
