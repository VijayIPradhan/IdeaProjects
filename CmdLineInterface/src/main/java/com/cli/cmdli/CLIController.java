package com.cli.cmdli;// CLIController.java
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class CLIController {

    private final Services service;

    public CLIController(Services service) {
        this.service = service;
    }

    @MessageMapping("/command")
    @SendTo("/topic/response")
    public String processCommand(String command) {
        return service.processInput(command);
    }
}
