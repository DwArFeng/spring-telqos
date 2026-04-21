package com.dwarfeng.springtelqos.node.configuration;

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
        registerBeanDefinitionParser("config", new SpringTelqosConfigDefinitionParser());
        registerBeanDefinitionParser("handler", new SpringTelqosHandlerDefinitionParser());
        registerBeanDefinitionParser("qos", new SpringTelqosQosDefinitionParser());
    }
}
