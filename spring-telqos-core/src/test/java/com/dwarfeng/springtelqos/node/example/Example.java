package com.dwarfeng.springtelqos.node.example;

import com.dwarfeng.dutil.basic.sdk.io.CT;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Scanner;

/**
 * Telqos 示例。
 *
 * @author DwArFeng
 * @since 2.0.0
 */
public class Example {

    static void main() {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(
                "classpath:com/dwarfeng/springtelqos/node/spring/application-context*.xml"
        );
        ctx.registerShutdownHook();
        ctx.start();

        Scanner scanner = new Scanner(System.in);

        // 显示欢迎信息。
        CT.trace("开发者您好!");
        CT.trace("这是一个示例, 用于演示 spring-telqos 的 Telqos 核心功能");
        CT.trace("您可以使用 telnet 客户端工具访问本机 ${telqos.port} 端口以体验本示例的功能");
        CT.trace("您可以测试 list、manual、quit 等内置命令");
        System.out.print("本示例的功能体验完成后，请按回车键结束示例...");
        scanner.nextLine();

        CT.trace("示例演示完毕, 感谢您测试与使用!");

        ctx.stop();
        ctx.close();
        System.exit(0);
    }
}
