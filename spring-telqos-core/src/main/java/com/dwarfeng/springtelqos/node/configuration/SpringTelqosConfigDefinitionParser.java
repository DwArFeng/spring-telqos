package com.dwarfeng.springtelqos.node.configuration;

import com.dwarfeng.springtelqos.sdk.configuration.CommandClassPathBeanDefinitionScanner;
import com.dwarfeng.springtelqos.sdk.util.BeanDefinitionParserUtil;
import com.dwarfeng.springtelqos.stack.struct.TelqosConfig;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanReference;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Telqos Config 元素的 BeanDefinitionParser。
 *
 * @author DwArFeng
 * @since 2.0.0
 */
public class SpringTelqosConfigDefinitionParser implements BeanDefinitionParser {

    private static final String TELQOS_NAMESPACE_URL = "http://dwarfeng.com/schema/spring-telqos";

    @Override
    public BeanDefinition parse(Element element, @Nonnull ParserContext parserContext) {
        String configName = (String) BeanDefinitionParserUtil.mayResolveSpel(
                parserContext, element.getAttribute("config-name")
        );

        BeanDefinitionParserUtil.makeSureBeanNameNotDuplicated(parserContext, configName);

        Element connectionSettingElement = (Element) element
                .getElementsByTagNameNS(TELQOS_NAMESPACE_URL, "connection-setting").item(0);
        Element commandElement = (Element) element
                .getElementsByTagNameNS(TELQOS_NAMESPACE_URL, "command").item(0);
        Element namingStrategyElement = (Element) element
                .getElementsByTagNameNS(TELQOS_NAMESPACE_URL, "naming-strategy").item(0);

        ManagedList<BeanReference> commandBeanReferences = new ManagedList<>();
        if (Objects.nonNull(commandElement)) {
            NodeList commandImpls = element.getElementsByTagNameNS(TELQOS_NAMESPACE_URL, "command-impl");
            for (int i = 0; i < commandImpls.getLength(); i++) {
                Element commandImplElement = (Element) commandImpls.item(i);
                commandBeanReferences.addAll(parseCommandImpl(commandImplElement, parserContext));
            }
        }

        RootBeanDefinition telqosConfigBuilderBeanDefinition = new RootBeanDefinition(TelqosConfig.Builder.class);
        if (Objects.nonNull(connectionSettingElement)) {
            telqosConfigBuilderBeanDefinition.getPropertyValues().add(
                    "port",
                    BeanDefinitionParserUtil.mayResolvePlaceholder(
                            parserContext, connectionSettingElement.getAttribute("port")
                    )
            );
            telqosConfigBuilderBeanDefinition.getPropertyValues().add(
                    "whitelistRegex",
                    BeanDefinitionParserUtil.mayResolvePlaceholder(
                            parserContext, connectionSettingElement.getAttribute("whitelist-regex")
                    )
            );
            telqosConfigBuilderBeanDefinition.getPropertyValues().add(
                    "blacklistRegex",
                    BeanDefinitionParserUtil.mayResolvePlaceholder(
                            parserContext, connectionSettingElement.getAttribute("blacklist-regex")
                    )
            );
            telqosConfigBuilderBeanDefinition.getPropertyValues().add(
                    "charset",
                    BeanDefinitionParserUtil.mayResolvePlaceholder(
                            parserContext, connectionSettingElement.getAttribute("charset")
                    )
            );
            telqosConfigBuilderBeanDefinition.getPropertyValues().add(
                    "bannerUrl",
                    BeanDefinitionParserUtil.mayResolvePlaceholder(
                            parserContext, connectionSettingElement.getAttribute("banner-url")
                    )
            );
        }
        telqosConfigBuilderBeanDefinition.getPropertyValues().add("commands", commandBeanReferences);
        if (Objects.nonNull(namingStrategyElement)) {
            telqosConfigBuilderBeanDefinition.getPropertyValues().add(
                    "namingStrategy",
                    BeanDefinitionParserUtil.mayResolvePlaceholder(
                            parserContext, namingStrategyElement.getAttribute("value")
                    )
            );
        }
        telqosConfigBuilderBeanDefinition.setScope(BeanDefinition.SCOPE_SINGLETON);
        telqosConfigBuilderBeanDefinition.setLazyInit(false);
        String telqosConfigBuilderBeanName = BeanDefinitionParserUtil.parseAvailableBeanName(
                parserContext, configName + "ConfigBuilder"
        );
        parserContext.getRegistry().registerBeanDefinition(
                telqosConfigBuilderBeanName, telqosConfigBuilderBeanDefinition
        );

        RootBeanDefinition telqosConfigBeanDefinition = new RootBeanDefinition(TelqosConfig.class);
        telqosConfigBeanDefinition.setFactoryBeanName(telqosConfigBuilderBeanName);
        telqosConfigBeanDefinition.setFactoryMethodName("build");
        telqosConfigBeanDefinition.setScope(BeanDefinition.SCOPE_SINGLETON);
        telqosConfigBeanDefinition.setLazyInit(false);
        parserContext.getRegistry().registerBeanDefinition(configName, telqosConfigBeanDefinition);

        return null;
    }

    private Set<BeanReference> parseCommandImpl(Element commandImplElement, ParserContext parserContext) {
        String commandName = (String) BeanDefinitionParserUtil.mayResolveSpel(
                parserContext, commandImplElement.getAttribute("command-name")
        );
        String clazz = (String) BeanDefinitionParserUtil.mayResolveSpel(
                parserContext, commandImplElement.getAttribute("class")
        );
        String commandRef = (String) BeanDefinitionParserUtil.mayResolveSpel(
                parserContext, commandImplElement.getAttribute("command-ref")
        );
        String packageScan = (String) BeanDefinitionParserUtil.mayResolveSpel(
                parserContext, commandImplElement.getAttribute("package-scan")
        );

        if (StringUtils.isNotEmpty(commandRef)) {
            return Collections.singleton(new RuntimeBeanReference(commandRef));
        } else if (StringUtils.isNotEmpty(packageScan)) {
            CommandClassPathBeanDefinitionScanner scanner = new CommandClassPathBeanDefinitionScanner(
                    parserContext.getRegistry(), parserContext.getReaderContext().getEnvironment()
            );
            scanner.scan(packageScan);
            Set<String> beanNames = scanner.getScannedBeanNames();
            Set<BeanReference> beanReferenceSet = new LinkedHashSet<>(beanNames.size());
            for (String beanName : beanNames) {
                beanReferenceSet.add(new RuntimeBeanReference(beanName));
            }
            return beanReferenceSet;
        } else {
            BeanDefinitionParserUtil.makeSureBeanNameNotDuplicated(parserContext, commandName);
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(clazz);
            builder.setScope(BeanDefinition.SCOPE_SINGLETON);
            builder.setLazyInit(false);
            parserContext.getRegistry().registerBeanDefinition(commandName, builder.getBeanDefinition());
            return Collections.singleton(new RuntimeBeanReference(commandName));
        }
    }
}
