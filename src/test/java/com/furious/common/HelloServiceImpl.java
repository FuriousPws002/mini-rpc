package com.furious.common;

import java.time.LocalDateTime;

import lombok.SneakyThrows;

/**
 * @author furious 2024/5/6
 */
public class HelloServiceImpl implements HelloService {
    @Override
    public void sayHello() {
        System.err.println("sayHello mini-rpc");
    }

    @Override
    @SneakyThrows
    public String sayHello(String name) {
        String s = "sayHello " + name + " " + LocalDateTime.now();
        System.err.println(s);
        Thread.sleep(3000);
        return s;
    }
}
