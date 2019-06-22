package com.melardev.spring.rxsecjwt.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.melardev.spring.rxsecjwt.dtos.responses.ErrorResponse;
import com.melardev.spring.rxsecjwt.entities.Todo;
import com.melardev.spring.rxsecjwt.repositories.TodosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.List;
import java.util.function.Function;

@CrossOrigin
@RestController
@RequestMapping("/todos")
public class TodosController {

    @Autowired
    ObjectMapper mapper;

    @Autowired
    TodosRepository todosRepository;

    @GetMapping
    public Flux<Todo> getAll() {
        todosRepository.findAllHqlSummary().collectList().map(new Function<List<Todo>, Object>() {
            @Override
            public Object apply(List<Todo> todos) {

                return null;
            }
        }).subscribe();
        return todosRepository.findAllHqlSummary();
    }

    @GetMapping("/pending")
    public Flux<Todo> getPending() {
        return todosRepository.findByCompletedFalse();
    }


    @GetMapping("/completed")
    public Flux<Todo> getCompleted() {
        return todosRepository.findByCompletedIsTrueHql();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Object>> getById(@PathVariable("id") String id) {
        return this.todosRepository.findById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(new ResponseEntity(new ErrorResponse("Todo not found"), HttpStatus.NOT_FOUND));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public Mono<Todo> create(@Valid @RequestBody Todo todo) {
        return todosRepository.save(todo);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ResponseEntity<Object>> update(@PathVariable("id") String id, @RequestBody Todo todoInput) {

        Mono<ResponseEntity<Object>> res = todosRepository.findById(id)
                .flatMap(t -> {
                    String title = todoInput.getTitle();
                    if (title != null)
                        t.setTitle(title);

                    String description = todoInput.getDescription();
                    if (description != null)
                        t.setDescription(description);

                    t.setCompleted(todoInput.isCompleted());
                    return todosRepository.save(t)
                            .map(te -> te);
                })
                .map(ResponseEntity::ok)
                .defaultIfEmpty(new ResponseEntity(new ErrorResponse("Not found"), HttpStatus.NOT_FOUND));
        return res;
    }


    /*
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Object>> delete(@PathVariable("id") String id) {
        return todosRepository.deleteById(id)
                .then(Mono.just(new ResponseEntity<>(HttpStatus.NO_CONTENT)))
                // defaultIfEmpty should be never called actually, deleteById returns a Mono of Void
                .defaultIfEmpty(new ResponseEntity<>(new ErrorResponse("Todo not found"), HttpStatus.NOT_FOUND));
    }
    */


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ResponseEntity<Object>> delete(@PathVariable("id") String id) {
        return todosRepository.findById(id)
                .flatMap(t -> todosRepository.delete(t)
                        .then(Mono.just(new ResponseEntity<>(HttpStatus.NO_CONTENT))))
                .defaultIfEmpty(new ResponseEntity<>(new ErrorResponse("Todo not found"), HttpStatus.NOT_FOUND));
    }


    @DeleteMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ResponseEntity<Void>> deleteAll() {
        return todosRepository.deleteAll().then(Mono.just(new ResponseEntity<>(HttpStatus.NO_CONTENT)));
    }

}