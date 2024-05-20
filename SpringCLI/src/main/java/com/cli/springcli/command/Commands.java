package com.cli.springcli.command;

import com.cli.springcli.controller.PersonController;
import com.cli.springcli.model.Person;
import com.cli.springcli.service.CommandService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.collections4.trie.PatriciaTrie;
import org.jline.utils.AttributedString;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.shell.jline.PromptProvider;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.table.TableBuilder;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@ShellComponent
public class Commands implements PromptProvider {

    private final RestTemplate restTemplate;
    private final CommandService commandService;
    private final PersonController personController;

    public Commands(RestTemplate restTemplate, CommandService commandService, PersonController personController) {
        this.restTemplate = restTemplate;
        this.commandService = commandService;
        this.personController = personController;
    }
    private void printBanner() {
        System.out.println("  _______ _____ ____   _____  ____     ____  _____  __  __ ");
        System.out.println(" |__   __|_   _|  _ \\ / ____ / __ \\   |  _ \\|  __ \\|  \\/  |");
        System.out.println("    | |    | | | |_) | |    | |  | |  | |_) | |__) | \\  / |");
        System.out.println("    | |    | | |  _ <| |    | |  | |  |  _ <|  ___/| |\\/| |");
        System.out.println("    | |   _| |_| |_) | |___ | |__| |  | |_) | |    | |  | |");
        System.out.println("    |_|  |_____|____/ \\____  \\____/   |____/|_|    |_|  |_|");
        System.out.println();
    }

    // This method will be called before executing any other commands
    @ShellMethod(key = "start", value = "Start the CLI application")
    public void start() {
        printBanner();
    }


    @ShellMethod(key = {"all","a"}, value = "All Person in the List")
    public void all() {
        ResponseEntity<List<Person>> responseEntity = personController.getAllUser();
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            List<Person> persons = responseEntity.getBody();
            if (persons != null) {
                displayPersons(persons);
            } else {
                System.out.println("No persons found.");
            }
        } else {
            System.out.println("Failed to retrieve all persons.");
        }
    }

    private void displayPersons(List<Person> persons) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the output format (json, table, tree):");
        String format = scanner.nextLine();
                switch (format) {
                    case "json":
                        printJson(persons);
                        break;
                    case "table":
                        printTable(persons);
                        break;
                    case "tree":
                        printTrees(persons);
                        break;
                    default:
                        System.out.println("Invalid format. Supported formats: json, table, tree");
                }
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
            System.out.println(" " + "Age: " + age);
            for (Person person : persons) {
                System.out.println(" " + "      └─ " + person.name());
                System.out.println(" " + "          └─ " + person.address());
            }
        }
    }

    private void printTable(List<Person> persons) {
            TableBuilder tableBuilder = commandService.listToArrayTableModel(persons);
            System.out.println(tableBuilder.build().render(120));
    }

    private void printJson(List<Person> persons) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        try {
            String json = objectMapper.writeValueAsString(persons);
            System.out.println(json);
        } catch (JsonProcessingException e) {
            System.out.println("Error converting to JSON: " + e.getMessage());
        }
    }

    @ShellMethod(key = "allTable", value = "All Person in the List")
    public void allTable() {
        ResponseEntity<List<Person>> responseEntity = personController.getAllUser();
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            List<Person> persons = responseEntity.getBody();
            assert persons != null;
            TableBuilder tableBuilder = commandService.listToArrayTableModel(persons);
            System.out.println(tableBuilder.build().render(120));
        } else {
            System.out.println("Failed to retrieve all persons.");
        }
    }

    @ShellMethod(key = "get-by-id", value = "Get Person by Id")
    public void getById(Long id) {
        ResponseEntity<Person> responseEntity=personController.getUserById(id);
        if(responseEntity.getStatusCode().is2xxSuccessful()) {
            Person person = responseEntity.getBody();
            assert person != null;
            TableBuilder tableBuilder = commandService.listToArrayTableModel(person);
            System.out.println(tableBuilder.build().render(120));
        }
        else
            System.out.println("Failed to retrieve person by Id:"+id);
    }

    @ShellMethod(key = "callGetAll", value = "Call GET /get-all endpoint")
    public void callGetAll() {
        ResponseEntity<Person[]> responseEntity = restTemplate.getForEntity("http://localhost:8080/get-all", Person[].class);
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            Person[] persons = responseEntity.getBody();
            if (persons != null) {
                TableBuilder tableBuilder = commandService.listToArrayTableModel(Arrays.asList(persons));
                System.out.println(tableBuilder.build().render(120));
            } else {
                System.out.println("No persons found.");
            }
        } else {
            System.out.println("Failed to retrieve all persons.");
        }
    }

    @ShellMethod(key = "call-get-by-id", value = "Call GET /get/{id} endpoint")
    public void callGetById(Long id) {
        ResponseEntity<Person> responseEntity = restTemplate.getForEntity("http://localhost:8080/get/{id}", Person.class, id);
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            Person person = responseEntity.getBody();
            assert person != null;
            TableBuilder tableBuilder = commandService.listToArrayTableModel(person);
            System.out.println(tableBuilder.build().render(120));

        } else {
            System.out.println("Failed to retrieve person with ID: " + id);
        }
    }

    @ShellMethod(key = "allJson", value = "All Person in the List")
    public String allJson() {
        ResponseEntity<List<Person>> responseEntity = restTemplate.exchange(
                "http://localhost:8080/get-all",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        System.out.println();

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            List<Person> persons = responseEntity.getBody();
            assert persons != null;
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            try {
                return objectMapper.writeValueAsString(persons);
            } catch (JsonProcessingException e) {
                return "Error converting to JSON: " + e.getMessage();
            }
        } else {
            return "Failed to retrieve all persons.";
        }

    }
    @ShellMethod(key = "country-codes", value = "Get country codes and initials")
    public List<String> getCountryCodes(String code) {
        ResponseEntity<Map<String, String>> responseEntity = restTemplate.exchange(
                "http://country.io/phone.json",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Map<String, String>>() {}
        );
        Map<String, String> countryData = responseEntity.getBody();

        List<String> countryCodes = new ArrayList<>();
        if (countryData != null) {
            int count=0;
            for (Map.Entry<String, String> entry : countryData.entrySet()) {
                String countryCode = entry.getKey();
                String countryInitial = entry.getValue();
                if(countryCode.equals(code)) {
                    countryCodes.add(countryCode+" : +"+countryInitial);
                }
                else {
                    countryCodes.add("Country with code: " + code + " is invalid");
                    break;
                }
            }
        }
        return countryCodes;
    }

    @ShellMethod(key = "c-c-tree", value = "Get country codes and initials")
    public void getCountryCode() {
        ResponseEntity<Map<String, String>> responseEntity = restTemplate.exchange(
                "http://country.io/phone.json",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Map<String, String>>() {}
        );
        Map<String, String> countryData = responseEntity.getBody();

        PatriciaTrie<String> trie = new PatriciaTrie<>();
        if (countryData != null) {
            trie.putAll(countryData);
        }

        printTree(trie, "");
    }

    private void printTree(PatriciaTrie<String> trie, String prefix) {
        for (String key : trie.keySet()) {
            if (key.startsWith(prefix)) {
                String value = trie.get(key);
                System.out.println(prefix + key + " - " + value);
                String nextPrefix = key + "/";
                printTree(trie, nextPrefix);
            }
        }
    }

    @ShellMethod(key = "c-c", value = "Get country codes and initials")
    public void getCountryCodes() {
        ResponseEntity<Map<String, Object>> responseEntity = restTemplate.exchange(
                "https://jsonmock.hackerrank.com/api/countries",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Map<String, Object>>() {}
        );

        if (responseEntity.getStatusCode() == HttpStatus.OK &&
                responseEntity.getHeaders().getContentType().includes(MediaType.APPLICATION_JSON)) {
            Map<String, Object> responseData = responseEntity.getBody();

            if (responseData != null) {
                List<Map<String, String>> data = (List<Map<String, String>>) responseData.get("data");
                if (data != null) {
                    for (Map<String, String> entry : data) {
                        String country = entry.get("name");
                        String countryCode = entry.get("alpha2Code");
                        System.out.println(countryCode + " - " + country);
                    }
                }
            }
        } else {
            System.out.println("Failed to retrieve country data.");
        }
    }

    @Override
    public AttributedString getPrompt() {
        System.out.println();
           // printBanner();
            return new AttributedString("tibco-bpm>"); // Customize prompt as needed
        }

}
