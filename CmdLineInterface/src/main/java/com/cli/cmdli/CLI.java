package com.cli.cmdli;

import jdk.jfr.Category;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;



public class CLI implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {
        if (args.length > 0) {
            String command = args[0];
            switch (command) {
                case "hello":
                    System.out.println("Hello, World!");
                    break;
                default:
                    System.out.println("Unknown command");
                    break;
            }
        } else {
            System.out.println("No command provided");
        }
    }
}
