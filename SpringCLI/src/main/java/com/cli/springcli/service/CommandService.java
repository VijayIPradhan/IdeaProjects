package com.cli.springcli.service;

import com.cli.springcli.controller.PersonController;
import com.cli.springcli.model.Person;
import org.springframework.shell.table.ArrayTableModel;
import org.springframework.shell.table.BorderStyle;
import org.springframework.shell.table.TableBuilder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommandService {

    public CommandService(PersonController personController) {
    }

    public TableBuilder listToArrayTableModel(List<Person> persons) {

        ArrayTableModel model = new ArrayTableModel(persons.stream()
                .map(p -> new String[]{String.valueOf(p.id()),p.name(), String.valueOf(p.age()), p.address()})
                .toArray(String[][]::new));
        TableBuilder tableBuilder = new TableBuilder(model);

        tableBuilder.addFullBorder(BorderStyle.fancy_light_double_dash);

        return tableBuilder;
        }
    public TableBuilder listToArrayTableModel(Person person) {
        List<Person> people = List.of(person);
        return listToArrayTableModel(people);
    }
}


