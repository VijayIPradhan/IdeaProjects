package com.clishell.commandshell.service;

import com.clishell.commandshell.model.Person;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.shell.table.BorderStyle;
import org.springframework.shell.table.Table;
import org.springframework.shell.table.TableBuilder;
import org.springframework.shell.table.TableModelBuilder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

@Service
public class CommandServiceImpl implements CommandService {

    @Override
    public void displayPerson(String name, List<Person> people) {
        people.stream()
                .filter(p -> p.name().equals(name))
                .findFirst()
                .ifPresentOrElse(
                        person -> printTable(List.of(person)),
                        () -> System.err.println("Person with name '" + name + "' not found.")
                );
    }

    @Override
    public void displayPersons(List<Person> persons) {
        String format = promptUser("Enter the output format (json, table, tree):");
        switch (format) {
            case "json" -> printJson(persons);
            case "table" -> printTable(persons);
            case "tree" -> printTrees(persons);
            default -> System.err.println("Invalid format. Supported formats: json, table, tree");
        }
    }

    @Override
    public Person logout(Person loggedInUser) {
        String choice = promptUser("Do you want to logout? (y/n)");
        if (choice.equalsIgnoreCase("y")) {
            System.out.println("Logged out successfully.\n");
            loggedInUser = null;
        }
        return loggedInUser;
    }

    private String promptUser(String message) {
        Scanner scanner = new Scanner(System.in);
        System.out.println(message);
        return scanner.nextLine();
    }

    private void printTrees(List<Person> persons) {
        Map<Integer, List<Person>> ageGroups = persons.stream()
                .collect(Collectors.groupingBy(Person::age));
        printTreeRecursive(ageGroups);
    }

    private void printTreeRecursive(Map<Integer, List<Person>> ageGroups) {
        for (Map.Entry<Integer, List<Person>> entry : ageGroups.entrySet()) {
            int age = entry.getKey();
            List<Person> persons = entry.getValue();
            System.out.println(" Age: " + age);
            for (Person person : persons) {
                System.out.println("\t   └─ " + person.name() + "\n\t\t   └─ " + person.address());
            }
        }
    }

    private void printTable(List<Person> persons) {
        TableModelBuilder<Object> modelBuilder = new TableModelBuilder<>();
        modelBuilder.addRow().addValue("Name").addValue("Age").addValue("Address");
        for (Person person : persons) {
            modelBuilder.addRow().addValue(person.name()).addValue(person.age()).addValue(person.address());
        }
        Table table = new TableBuilder(modelBuilder.build())
                .addFullBorder(BorderStyle.fancy_double)
                .addHeaderBorder(BorderStyle.fancy_double)
                .build();
        System.out.println(table.render(120));

    }

    private void printJson(List<Person> persons) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        try {
            String json = objectMapper.writeValueAsString(persons);
            System.out.println(json);
        } catch (JsonProcessingException e) {
            System.err.println("Error converting to JSON: " + e.getMessage());
        }
    }
}
