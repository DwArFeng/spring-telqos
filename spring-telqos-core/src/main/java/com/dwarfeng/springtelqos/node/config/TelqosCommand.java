package com.dwarfeng.springtelqos.node.config;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * Telqos 命令注解。
 *
 * @author DwArFeng
 * @since 1.1.7
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface TelqosCommand {

    /**
     * 该命令的 bean 名称。
     *
     * @return 该命令的 bean 名称。
     * @see Component#value()
     */
    @AliasFor(annotation = Component.class)
    String value() default "";
}
