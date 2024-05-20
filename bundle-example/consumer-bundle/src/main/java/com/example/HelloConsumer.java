// HelloConsumer.java
package com.example;

import com.example.service.HelloService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component
public class HelloConsumer {

    private HelloService helloService;

    @Reference
    public void setHelloService(HelloService helloService) {
        this.helloService = helloService;
    }

    public void activate() {
        System.out.println(helloService.sayHello());
    }
}
