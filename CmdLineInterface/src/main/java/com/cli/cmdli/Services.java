package com.cli.cmdli;// Service.java
import org.springframework.stereotype.Service;

@Service
public class Services {

    private final Person person;

    public Services() {
        this.person = new Person("Adam", 25, "CEO");
    }

    public String processInput(String input) {

        switch (input) {
            case "1":
                return "Hello, your name is " + person.getName();
            case "2":
                return "Hello, your age is " + person.getAge();
            case "3":
                return "Hello, your designation is " + person.getDesignation();
            case "exit":
                System.exit(10001);
            default:
                return "Unknown command";
        }
    }
}
