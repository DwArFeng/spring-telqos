package com.dwarfeng.springtelqos.api.example;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Scanner;

public class Example {

    public static void main(String[] args) {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(
                "classpath:spring/application-context*.xml"
        );
        ctx.registerShutdownHook();
        ctx.start();

        Scanner scanner = new Scanner(System.in);

        // 显示欢迎信息。
        System.out.println("开发者您好!");
        System.out.println("这是一个示例, 用于演示 dwarfeng-ftp 的 Telqos 功能");
        System.out.println("您可以使用 telnet 客户端工具访问本机 ${telqos.port} 端口以体验本示例的功能");
        System.out.print("本示例的功能体验完成后，请按回车键结束示例...");
        scanner.nextLine();

        System.out.println("示例演示完毕, 感谢您测试与使用!");

        ctx.stop();
        ctx.close();
        System.exit(0);
    }
}
