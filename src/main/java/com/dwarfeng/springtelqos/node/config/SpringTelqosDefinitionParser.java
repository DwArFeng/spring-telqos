package com.dwarfeng.springtelqos.node.config;

import com.dwarfeng.springtelqos.impl.service.TelqosServiceImpl;
import com.dwarfeng.springtelqos.stack.bean.TelqosConfig;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import java.util.Objects;

/**
 * Telqos 的 BeanDefinitionParser。
 *
 * @author DwArFeng
 * @since 1.0.0
 */
public class SpringTelqosDefinitionParser implements BeanDefinitionParser {

    private static final String TELQOS_NAMESPACE_URL = "http://dwarfeng.com/schema/spring-telqos";
    private static final String DEFAULT_TELQOS_CONFIG_ID = "telqosConfig";
    private static final String DEFAULT_CLI_HANDLER_ID = "telqosCliHandler";
    private static final String DEFAULT_SERIALIZER_ID = "telqosSerializer";
    private static final String DEFAULT_DESERIALIZER_ID = "telqosDeserializer";
    private static final String DEFAULT_CLI_HANDLER_CLASS = "com.dwarfeng.springtelqos.impl.handler.ApplicationContextCliHandler";
    private static final String DEFAULT_SERIALIZER_CLASS = "com.dwarfeng.springtelqos.sdk.serialize.FastJsonSerializer";
    private static final String DEFAULT_DESERIALIZER_CLASS = "com.dwarfeng.springtelqos.sdk.serialize.FastJsonDeserializer";

    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        String telqosConfigId = parseTelqosConfig(element, parserContext);
        String cliHandlerId = parseCliHandler(element, parserContext);
        String serializerId = parseSerializer(element, parserContext);
        String deserializerId = parseDeserializer(element, parserContext);
        parseTelqosService(element, parserContext, telqosConfigId, cliHandlerId, serializerId, deserializerId);
        return null;
    }

    private String parseTelqosConfig(Element element, ParserContext parserContext) {
        element = (Element) element.getElementsByTagNameNS(TELQOS_NAMESPACE_URL, "config").item(0);
        String id = Objects.isNull(element) ?
                DEFAULT_TELQOS_CONFIG_ID : element.getAttribute("id");
        checkBeanDuplicated(parserContext, id);
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(TelqosConfig.class);
        if (Objects.nonNull(element)) {
            builder.addPropertyValue("port", element.getAttribute("port"));
            builder.addPropertyValue("whitelistRegex", element.getAttribute("whitelist-regex"));
            builder.addPropertyValue("blacklistRegex", element.getAttribute("blacklist-regex"));
            builder.addPropertyValue("password", element.getAttribute("password"));
            builder.addPropertyValue("charset", element.getAttribute("charset"));
            builder.addPropertyValue("soBacklog", element.getAttribute("so-backlog"));
        }
        builder.setScope(BeanDefinition.SCOPE_SINGLETON);
        builder.setLazyInit(false);
        parserContext.getRegistry().registerBeanDefinition(id, builder.getBeanDefinition());
        return id;
    }

    private String parseCliHandler(Element element, ParserContext parserContext) {
        element = (Element) element.getElementsByTagNameNS(TELQOS_NAMESPACE_URL, "cli-handler").item(0);
        return parseCommonBean(element, parserContext, DEFAULT_CLI_HANDLER_ID, DEFAULT_CLI_HANDLER_CLASS);
    }

    private String parseSerializer(Element element, ParserContext parserContext) {
        element = (Element) element.getElementsByTagNameNS(TELQOS_NAMESPACE_URL, "serializer").item(0);
        return parseCommonBean(element, parserContext, DEFAULT_SERIALIZER_ID, DEFAULT_SERIALIZER_CLASS);
    }

    private String parseDeserializer(Element element, ParserContext parserContext) {
        element = (Element) element.getElementsByTagNameNS(TELQOS_NAMESPACE_URL, "deserializer").item(0);
        return parseCommonBean(element, parserContext, DEFAULT_DESERIALIZER_ID, DEFAULT_DESERIALIZER_CLASS);
    }

    private String parseCommonBean(
            Element element, ParserContext parserContext, String defaultBeanId, String defaultBeanClass) {
        if (Objects.nonNull(element) && StringUtils.isNotEmpty(element.getAttribute("ref"))) {
            return element.getAttribute("ref");
        }
        String id = Objects.isNull(element) ? defaultBeanId : element.getAttribute("id");
        String clazz = Objects.isNull(element) ? defaultBeanClass : element.getAttribute("class");
        checkBeanDuplicated(parserContext, id);
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(clazz);
        builder.setScope(BeanDefinition.SCOPE_SINGLETON);
        builder.setLazyInit(false);
        parserContext.getRegistry().registerBeanDefinition(id, builder.getBeanDefinition());
        return id;
    }

    private void parseTelqosService(
            Element element, ParserContext parserContext, String telqosConfigId, String cliHandlerId,
            String serializerId, String deserializerId) {
        String id = element.getAttribute("id");
        checkBeanDuplicated(parserContext, id);
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(TelqosServiceImpl.class);
        builder.addPropertyReference("telqosConfig", telqosConfigId);
        builder.addPropertyReference("cliHandler", cliHandlerId);
        builder.addPropertyReference("serializer", serializerId);
        builder.addPropertyReference("deserializer", deserializerId);
        builder.setScope(BeanDefinition.SCOPE_SINGLETON);
        builder.setLazyInit(false);
        parserContext.getRegistry().registerBeanDefinition(id, builder.getBeanDefinition());
    }

    private void checkBeanDuplicated(ParserContext parserContext, String id) {
        if (parserContext.getRegistry().containsBeanDefinition(id)) {
            throw new IllegalStateException("Duplicated spring bean id " + id);
        }
    }
}
