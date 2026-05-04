package com.dwarfeng.springtelqos.node.configuration;

import com.dwarfeng.springtelqos.impl.handler.TelqosHandlerImpl;
import com.dwarfeng.springtelqos.sdk.util.BeanDefinitionParserUtil;
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
 * Telqos Handler 元素的 BeanDefinitionParser。
 *
 * @author DwArFeng
 * @since 2.0.0
 */
public class SpringTelqosHandlerDefinitionParser implements BeanDefinitionParser {

    @Override
    public BeanDefinition parse(Element element, @Nonnull ParserContext parserContext) {
        String handlerName = (String) BeanDefinitionParserUtil.mayResolveSpel(
                parserContext, element.getAttribute("handler-name")
        );
        String executorRef = (String) BeanDefinitionParserUtil.mayResolveSpel(
                parserContext, element.getAttribute("executor-ref")
        );
        String configRef = (String) BeanDefinitionParserUtil.mayResolveSpel(
                parserContext, element.getAttribute("config-ref")
        );

        BeanDefinitionParserUtil.makeSureBeanNameNotDuplicated(parserContext, handlerName);

        BeanDefinitionBuilder telqosHandlerBuilder = BeanDefinitionBuilder.rootBeanDefinition(TelqosHandlerImpl.class);
        telqosHandlerBuilder.getRawBeanDefinition().setAutowireMode(AbstractBeanDefinition.AUTOWIRE_CONSTRUCTOR);
        ConstructorArgumentValues telqosHandlerConstructorArgumentValues = new ConstructorArgumentValues();
        telqosHandlerConstructorArgumentValues.addIndexedArgumentValue(1, new RuntimeBeanReference(executorRef));
        telqosHandlerConstructorArgumentValues.addIndexedArgumentValue(2, new RuntimeBeanReference(configRef));
        telqosHandlerBuilder.getRawBeanDefinition().setConstructorArgumentValues(
                telqosHandlerConstructorArgumentValues
        );
        telqosHandlerBuilder.setScope(BeanDefinition.SCOPE_SINGLETON);
        telqosHandlerBuilder.setLazyInit(false);
        parserContext.getRegistry().registerBeanDefinition(handlerName, telqosHandlerBuilder.getBeanDefinition());

        return null;
    }
}
