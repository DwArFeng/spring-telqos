package com.dwarfeng.springtelqos.node.configuration;

import com.dwarfeng.springtelqos.stack.handler.TelqosHandler;
import com.dwarfeng.springtelqos.stack.service.TelqosQosService;
import com.dwarfeng.springtelqos.stack.struct.TelqosConfig;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Spring Telqos XML 命名空间测试。
 *
 * @author DwArFeng
 * @since 3.0.0
 */
public class SpringTelqosNamespaceHandlerTest {

    @Test
    public void loadsXmlNamespaceWithoutStartingTelnetServer() {
        try (ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                "classpath:com/dwarfeng/springtelqos/node/spring/application-context-aop.xml",
                "classpath:com/dwarfeng/springtelqos/node/spring/application-context-placeholder.xml",
                "classpath:com/dwarfeng/springtelqos/node/spring/application-context-scan.xml",
                "classpath:com/dwarfeng/springtelqos/node/spring/application-context-task.xml",
                "classpath:com/dwarfeng/springtelqos/node/spring/application-context-telqos.xml"
        )) {
            assertEquals(1, context.getBeansOfType(TelqosConfig.class).size());
            assertEquals(1, context.getBeansOfType(TelqosHandler.class).size());
            assertEquals(1, context.getBeansOfType(TelqosQosService.class).size());
        }
    }
}
