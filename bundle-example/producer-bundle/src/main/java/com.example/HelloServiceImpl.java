// HelloServiceImpl.java
package com.example;


import com.example.service.HelloService;

public class HelloServiceImpl implements HelloService {
    @Override
    public String sayHello() {
        return "Hello from OSGi!";
    }
}
