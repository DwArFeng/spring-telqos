package com.dwarfeng.springtelqos.node.configuration;

import com.dwarfeng.springtelqos.impl.service.TelqosQosServiceImpl;
import com.dwarfeng.springtelqos.sdk.util.BeanDefinitionParserUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import javax.annotation.Nonnull;

/**
 * Telqos Qos 元素的 BeanDefinitionParser。
 *
 * @author DwArFeng
 * @since 2.0.0
 */
public class SpringTelqosQosDefinitionParser implements BeanDefinitionParser {

    @Override
    public BeanDefinition parse(Element element, @Nonnull ParserContext parserContext) {
        String handlerName = BeanDefinitionParserUtil.mayResolvePlaceholder(
                parserContext, element.getAttribute("handler-name")
        );
        String serviceName = BeanDefinitionParserUtil.mayResolvePlaceholder(
                parserContext, element.getAttribute("service-name")
        );
        String semRef = BeanDefinitionParserUtil.mayResolvePlaceholder(
                parserContext, element.getAttribute("sem-ref")
        );
        String autoStart = BeanDefinitionParserUtil.mayResolvePlaceholder(
                parserContext, element.getAttribute("auto-start")
        );

        if (StringUtils.isEmpty(serviceName)) {
            serviceName = "telqosQosService";
        }
        if (StringUtils.isEmpty(handlerName)) {
            handlerName = "telqosHandlerImpl";
        }
        if (StringUtils.isEmpty(semRef)) {
            semRef = "mapServiceExceptionMapper";
        }
        if (StringUtils.isEmpty(autoStart)) {
            autoStart = "true";
        }

        BeanDefinitionParserUtil.makeSureBeanNameNotDuplicated(parserContext, serviceName);

        BeanDefinitionBuilder telqosQosServiceBuilder = BeanDefinitionBuilder.rootBeanDefinition(
                TelqosQosServiceImpl.class
        );
        telqosQosServiceBuilder.getRawBeanDefinition().setAutowireMode(AbstractBeanDefinition.AUTOWIRE_CONSTRUCTOR);
        ConstructorArgumentValues telqosQosServiceConstructorArgumentValues = new ConstructorArgumentValues();
        telqosQosServiceConstructorArgumentValues.addIndexedArgumentValue(0, new RuntimeBeanReference(handlerName));
        telqosQosServiceConstructorArgumentValues.addIndexedArgumentValue(1, new RuntimeBeanReference(semRef));
        telqosQosServiceBuilder.getRawBeanDefinition().setConstructorArgumentValues(
                telqosQosServiceConstructorArgumentValues
        );
        if (Boolean.parseBoolean(autoStart)) {
            telqosQosServiceBuilder.setInitMethodName("start");
        }
        telqosQosServiceBuilder.setScope(BeanDefinition.SCOPE_SINGLETON);
        telqosQosServiceBuilder.setLazyInit(false);
        parserContext.getRegistry().registerBeanDefinition(serviceName, telqosQosServiceBuilder.getBeanDefinition());

        return null;
    }
}
