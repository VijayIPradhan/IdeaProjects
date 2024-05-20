package com.clishell.commandshell.commands;

import com.clishell.commandshell.exception.UserAlreadyLoggedInException;
import com.clishell.commandshell.model.Person;
import com.clishell.commandshell.service.CommandService;
import org.jline.utils.AttributedString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.shell.Availability;
import org.springframework.shell.jline.PromptProvider;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import org.springframework.shell.standard.ShellOption;

import org.springframework.web.client.RestTemplate;


import java.util.Arrays;
import java.util.List;

@ShellComponent
public class TibcoCommands implements PromptProvider {

    private Person loggedInUser;

    @Autowired
    private CommandService commandService;

    private final RestTemplate restTemplate;

    public TibcoCommands(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    //Login Method
    @ShellMethod(key = "login", value = "Try to login with provided username and password")
    public void login(@ShellOption String username, @ShellOption String password) {
        try {
            if (username != null && password != null) {
                if (loggedInUser != null) {
                    throw new UserAlreadyLoggedInException(loggedInUser.name() + " is already logged in. Please logout first.");
                }
                this.loggedInUser = new Person(username, 24, "New York");
                System.out.println("Logged in as '" + username + "'\n");
            } else {
                System.out.println("Username or Password is empty\n");
            }
        } catch (UserAlreadyLoggedInException e) {
            System.out.println(e.getMessage());
        }
    }

    //Logout method
    @ShellMethod(key = "logout", value = "Logout User")
    public void logout() {
        try {
            if (loggedInUser == null) {
                throw new UserAlreadyLoggedInException("You are not logged in.");
            }
            loggedInUser= commandService.logout(loggedInUser);
        } catch (UserAlreadyLoggedInException e) {
            System.out.println(e.getMessage());
        }
    }

    //Admin access
    @ShellMethod(key = {"all", "a"}, value = "Get All Users")
    public void getAllUsers() {
        if (loggedInUser != null && loggedInUser.name().equalsIgnoreCase("admin")) {
            commandService.displayPersons(personList());
        } else {
            System.out.println("Please log in as admin.\n");
        }
    }

    @ShellMethod(key = "callGetAll", value = "Call GET /get-all endpoint")
    public void callGetAll() {
        ResponseEntity<Person[]> responseEntity = restTemplate.getForEntity("http://localhost:8080/get-all", Person[].class);

        HttpStatus statusCode = (HttpStatus) responseEntity.getStatusCode();

        if (statusCode.is2xxSuccessful()) {
            Person[] persons = responseEntity.getBody();

            if (persons != null && persons.length > 0) {
                commandService.displayPersons(Arrays.asList(persons));
            } else {
                System.out.println("No persons found.");
            }
        } else {
            System.out.println("Failed to retrieve all persons. Status code: " + statusCode);
        }
    }


    //+ All access
    @ShellMethod(key = {"person", "p"}, value = "Search User by Name")
    public void searchUser(String name) {
        String firstName = name.trim().split("\\s+")[0].toLowerCase();
        if (loggedInUser != null && loggedInUser.name().equals(firstName))
            commandService.displayPerson(name, personList());
        else
            System.out.println("Please Log In.\n");
    }



    private String getLoggedInUserName() {
        return (loggedInUser != null) ? loggedInUser.name().toLowerCase() : "tibco-bpm";
    }
    @Override
    public AttributedString getPrompt() {
        return new AttributedString(getLoggedInUserName() + ">");
    }
    private List<Person> personList() {
        return List.of(
                new Person("Adam Simp", 24, "Bengaluru"),
                new Person("Eve Simp", 24, "Mumbai"),
                new Person("Dev Simp", 22, "Delhi"),
                new Person("Joe Simp", 22, "Hyderabad")
        );
    }

    /*public Availability availabilityLogout() {
        return (loggedInUser != null) ? Availability.available() : Availability.unavailable("");
    }
    public Availability availabilityLoggedIn() {
        if (loggedInUser != null) {
            return Availability.unavailable("");
        } else {
            return Availability.available();
        }
    }*/
}
