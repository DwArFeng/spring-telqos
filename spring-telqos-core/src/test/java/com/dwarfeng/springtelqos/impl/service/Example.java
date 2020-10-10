package com.dwarfeng.springtelqos.impl.service;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Example {

    public static void main(String[] args) {
        ClassPathXmlApplicationContext ctx =
                new ClassPathXmlApplicationContext("classpath:spring/application-context*.xml");
        ctx.registerShutdownHook();
        ctx.start();
    }
}
