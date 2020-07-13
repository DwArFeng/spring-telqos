package com.dwarfeng.springtelqos.node.config;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * Telqos 命名空间处理器。
 *
 * @author DwArFeng
 * @since 1.0.0
 */
public class SpringTelqosNamespaceHandler extends NamespaceHandlerSupport {

    @Override
    public void init() {
        registerBeanDefinitionParser("service", new SpringTelqosDefinitionParser());
    }
}
