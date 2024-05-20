package com.cli.cmdli;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CmdLineInterfaceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CmdLineInterfaceApplication.class, args);
       // Service.waitForInput();
    }

}
