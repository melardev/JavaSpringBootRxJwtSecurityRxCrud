package com.melardev.spring.rxsecjwt.repositories;


import com.melardev.spring.rxsecjwt.entities.Todo;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface TodosRepository extends ReactiveCrudRepository<Todo, Long> {

    // this will not work, Spring data would still treat this
    // method as a query method and the name is not meaningful at all
    // @Query(fields = "{description: 0}", exists = true)

    // Get all documents, all fields except description
    // @Query(fields = "{description: 0}", value = "{id: {$exists:true}}")
    // Get all documents, specified fields only. Id:1 is optional, if skipped then it will be 1 anyways
    @Query(fields = "{id: 1, title: 1, completed:1, createdAt: 1, updatedAt:1}", value = "{id: {$exists:true}}")
    Flux<Todo> findAllHqlSummary();

    // This is treated as a query method!!! even using @Query, because we have only set fields arg, and not value
    @Query(fields = "{description:0}")
    Flux<Todo> findByCompletedFalse();

    // This is not a query method, why? notice the value arg is set.
    @Query(fields = "{description:0}", value = "{completed: false}")
    Flux<Todo> findByCompletedFalseHql();

    // This is a Spring Data query method
    @Query(fields = "{description:0}")
    Flux<Todo> findByCompletedIsTrue();

    @Query(fields = "{description:0}", value = "{completed: true}")
    Flux<Todo> findByCompletedIsTrueHql();

    Mono<Todo> findById(String id);

    Flux<Todo> findByCompletedTrue();

    Flux<Todo> findByCompletedIsFalse();

    Flux<Todo> findByCompleted(boolean done);

    Flux<Todo> findByTitleContains(String title);

    Flux<Todo> findByDescriptionContains(String description);


    Flux<Todo> findByDescriptionContains(Mono<String> description);

    Mono<Todo> findByTitleAndDescription(Mono<String> title, String description);

}