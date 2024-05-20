package com.clishell.commandshell.service;

import com.clishell.commandshell.model.Person;

import java.util.List;

public interface CommandService {
    void displayPerson(String name, List<Person> people);

    void displayPersons(List<Person> persons);

    Person logout(Person person);


}
