package com.cli.springcli.controller;

import com.cli.springcli.model.Person;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
public class PersonController {
    private final List<Person> listOfPersons;

    public PersonController() {
        this.listOfPersons = createListOfPersons();
    }

    @GetMapping(value = "/get-all")
    public ResponseEntity<List<Person>> getAllUser() {
        return ResponseEntity.ok().body(listOfPersons);
    }


    @GetMapping(value = "/get/{id}")
    public ResponseEntity<Person> getUserById(@PathVariable Long id) {
        Optional<Person> optionalPerson = listOfPersons.stream()
                .filter(person -> person.id().equals(id))
                .findFirst();
        return optionalPerson.map(person -> ResponseEntity.ok().body(person))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private List<Person> createListOfPersons() {
        Person p1 = new Person(1L, "Adam Savage", 23, "Madison Squares, New York, US");
        Person p2 = new Person(2L, "Adam Bill", 23, "Madison Square1, New York, US");
        Person p3 = new Person(3L, "Adam Funk", 22, "Madison Square2, New York, US");
        Person p4 = new Person(4L, "Adam Shell", 22, "Madison Square3, New York, US");
        return List.of(p1, p2, p3, p4);
    }
}
