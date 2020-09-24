package com.dwarfeng.springtelqos.node.config;

import com.dwarfeng.springtelqos.impl.command.*;
import com.dwarfeng.springtelqos.impl.service.TelqosServiceImpl;
import com.dwarfeng.springtelqos.sdk.serialize.FastJsonDeserializer;
import com.dwarfeng.springtelqos.sdk.serialize.FastJsonSerializer;
import com.dwarfeng.springtelqos.stack.bean.TelqosConfig;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanReference;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.scheduling.config.TaskExecutorFactoryBean;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.Objects;

/**
 * Telqos Connection相关的 BeanDefinitionParser。
 *
 * @author DwArFeng
 * @since 1.0.0
 */
public class SpringTelqosDefinitionParser implements BeanDefinitionParser {

    private static final String TELQOS_NAMESPACE_URL = "http://dwarfeng.com/schema/spring-telqos";

    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        //获取bean名称。
        String configId = ParserUtil.mayResolve(parserContext, element.getAttribute("config-id"));
        String serviceId = ParserUtil.mayResolve(parserContext, element.getAttribute("service-id"));
        //检查bean名称是否重复。
        checkBeanDuplicated(parserContext, configId);
        checkBeanDuplicated(parserContext, serviceId);
        //构造TelqosConfig。
        BeanDefinitionBuilder telqosConfigBuilder = BeanDefinitionBuilder.rootBeanDefinition(TelqosConfig.class);
        //解析connection-setting。
        Element connectionSettingElement = (Element) element.getElementsByTagNameNS(
                TELQOS_NAMESPACE_URL, "connection-setting").item(0);
        if (Objects.isNull(connectionSettingElement)) {
            telqosConfigBuilder.addPropertyValue("port", 23);
            telqosConfigBuilder.addPropertyValue("whitelistRegex", "");
            telqosConfigBuilder.addPropertyValue("blacklistRegex", "");
            telqosConfigBuilder.addPropertyValue("charset", "UTF-8");
            telqosConfigBuilder.addPropertyValue("bannerUrl", "classpath:telqos/banner.txt");
        } else {
            telqosConfigBuilder.addPropertyValue("port", ParserUtil.mayResolve(
                    parserContext, connectionSettingElement.getAttribute("port")));
            telqosConfigBuilder.addPropertyValue("whitelistRegex", ParserUtil.mayResolve(
                    parserContext, connectionSettingElement.getAttribute("whitelist-regex")));
            telqosConfigBuilder.addPropertyValue("blacklistRegex", ParserUtil.mayResolve(
                    parserContext, connectionSettingElement.getAttribute("blacklist-regex")));
            telqosConfigBuilder.addPropertyValue("charset", ParserUtil.mayResolve(
                    parserContext, connectionSettingElement.getAttribute("charset")));
            telqosConfigBuilder.addPropertyValue("bannerUrl", ParserUtil.mayResolve(
                    parserContext, connectionSettingElement.getAttribute("banner-url")));
        }
        //解析serializer。
        Element serializerElement = (Element) element.getElementsByTagNameNS(
                TELQOS_NAMESPACE_URL, "serializer").item(0);
        if (Objects.isNull(serializerElement)) {
            telqosConfigBuilder.addPropertyValue("serializer", new FastJsonSerializer());
        } else {
            telqosConfigBuilder.addPropertyValue(
                    "serializer", referenceOrCreateBean(serializerElement, parserContext));
        }
        //解析deserializer。
        Element deserializerElement = (Element) element.getElementsByTagNameNS(
                TELQOS_NAMESPACE_URL, "deserializer").item(0);
        if (Objects.isNull(deserializerElement)) {
            telqosConfigBuilder.addPropertyValue("deserializer", new FastJsonDeserializer());
        } else {
            telqosConfigBuilder.addPropertyValue(
                    "deserializer", referenceOrCreateBean(deserializerElement, parserContext));
        }
        //解析command。
        Element commandElement = (Element) element.getElementsByTagNameNS(
                TELQOS_NAMESPACE_URL, "command").item(0);
        ManagedList<BeanReference> commandBeanReferences = new ManagedList<>();
        if (Objects.isNull(commandElement)) {
            registerDefaultCommand(commandBeanReferences, configId, serviceId, parserContext);
        } else {
            boolean useDefaultFlag = Boolean.parseBoolean(ParserUtil.mayResolve(
                    parserContext, connectionSettingElement.getAttribute("use-default")));
            if (useDefaultFlag) {
                registerDefaultCommand(commandBeanReferences, configId, serviceId, parserContext);
            }
            NodeList commandImpls = element.getElementsByTagNameNS(TELQOS_NAMESPACE_URL, "command-impl");
            for (int i = 0; i < commandImpls.getLength(); i++) {
                Element commandImplElement = (Element) commandImpls.item(i);
                commandBeanReferences.add(referenceOrCreateBean(commandImplElement, parserContext));
            }
        }
        telqosConfigBuilder.addPropertyValue("commands", commandBeanReferences);
        //解析task-pool。
        Element taskPoolElement = (Element) element.getElementsByTagNameNS(
                TELQOS_NAMESPACE_URL, "task-pool").item(0);
        BeanReference taskPoolBeanReference;
        if (Objects.isNull(taskPoolElement)) {
            String id = "telqosExecutor";
            checkBeanDuplicated(parserContext, id);
            BeanDefinitionBuilder executorBuilder = BeanDefinitionBuilder.rootBeanDefinition(TaskExecutorFactoryBean.class);
            executorBuilder.setScope(BeanDefinition.SCOPE_SINGLETON);
            executorBuilder.setLazyInit(false);
            parserContext.getRegistry().registerBeanDefinition(id, executorBuilder.getBeanDefinition());
            taskPoolBeanReference = new RuntimeBeanReference(id);
        } else {
            String id = ParserUtil.mayResolve(parserContext, taskPoolElement.getAttribute("id"));
            String ref = ParserUtil.mayResolve(parserContext, taskPoolElement.getAttribute("ref"));
            if (StringUtils.isNotEmpty(ref)) {
                taskPoolBeanReference = new RuntimeBeanReference(ref);
            } else {
                BeanDefinitionBuilder executorBuilder = BeanDefinitionBuilder.rootBeanDefinition(TaskExecutorFactoryBean.class);
                String keepAliveSeconds = taskPoolElement.getAttribute("keep-alive");
                if (StringUtils.isNotEmpty(keepAliveSeconds)) {
                    executorBuilder.addPropertyValue("keepAliveSeconds", keepAliveSeconds);
                }
                String queueCapacity = taskPoolElement.getAttribute("queue-capacity");
                if (StringUtils.isNotEmpty(queueCapacity)) {
                    executorBuilder.addPropertyValue("queueCapacity", queueCapacity);
                }
                String poolSize = taskPoolElement.getAttribute("pool-size");
                if (StringUtils.isNotEmpty(poolSize)) {
                    executorBuilder.addPropertyValue("poolSize", poolSize);
                }
                String rejectionPolicy = taskPoolElement.getAttribute("rejection-policy");
                if (StringUtils.isNotEmpty(rejectionPolicy)) {
                    String prefix = "java.util.concurrent.ThreadPoolExecutor.";
                    String policyClassName;
                    switch (rejectionPolicy) {
                        case "ABORT":
                            policyClassName = prefix + "AbortPolicy";
                            break;
                        case "CALLER_RUNS":
                            policyClassName = prefix + "CallerRunsPolicy";
                            break;
                        case "DISCARD":
                            policyClassName = prefix + "DiscardPolicy";
                            break;
                        case "DISCARD_OLDEST":
                            policyClassName = prefix + "DiscardOldestPolicy";
                            break;
                        default:
                            policyClassName = rejectionPolicy;
                            break;
                    }
                    executorBuilder.addPropertyValue("rejectedExecutionHandler", new RootBeanDefinition(policyClassName));
                }
                executorBuilder.setScope(BeanDefinition.SCOPE_SINGLETON);
                executorBuilder.setLazyInit(false);
                parserContext.getRegistry().registerBeanDefinition(id, executorBuilder.getBeanDefinition());
                taskPoolBeanReference = new RuntimeBeanReference(id);
            }
        }
        telqosConfigBuilder.addPropertyValue("executor", taskPoolBeanReference);
        //注册TelqosConfig。
        telqosConfigBuilder.setScope(BeanDefinition.SCOPE_SINGLETON);
        telqosConfigBuilder.setLazyInit(false);
        parserContext.getRegistry().registerBeanDefinition(configId, telqosConfigBuilder.getBeanDefinition());
        //构造TelqosService。
        BeanDefinitionBuilder telqosServiceBuilder = BeanDefinitionBuilder.rootBeanDefinition(TelqosServiceImpl.class);
        //TelqosService参数赋值。
        telqosServiceBuilder.addPropertyValue("telqosConfig", new RuntimeBeanReference(configId));
        //注册构造TelqosService。
        telqosServiceBuilder.setScope(BeanDefinition.SCOPE_SINGLETON);
        telqosServiceBuilder.setLazyInit(false);
        parserContext.getRegistry().registerBeanDefinition(serviceId, telqosServiceBuilder.getBeanDefinition());

        return null;
    }

    private void checkBeanDuplicated(ParserContext parserContext, String id) {
        if (parserContext.getRegistry().containsBeanDefinition(id)) {
            throw new IllegalStateException("Duplicated spring bean id " + id);
        }
    }

    private BeanReference referenceOrCreateBean(Element element, ParserContext parserContext) {
        String id = ParserUtil.mayResolve(parserContext, element.getAttribute("id"));
        String clazz = ParserUtil.mayResolve(parserContext, element.getAttribute("class"));
        String ref = ParserUtil.mayResolve(parserContext, element.getAttribute("ref"));
        if (StringUtils.isNotEmpty(ref)) {
            return new RuntimeBeanReference(ref);
        } else {
            checkBeanDuplicated(parserContext, id);
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(clazz);
            builder.setScope(BeanDefinition.SCOPE_SINGLETON);
            builder.setLazyInit(false);
            parserContext.getRegistry().registerBeanDefinition(id, builder.getBeanDefinition());
            return new RuntimeBeanReference(id);
        }
    }

    private void registerDefaultCommand(
            ManagedList<BeanReference> commandBeanReferences, String configId,
            String serviceId, ParserContext parserContext) {
        BeanDefinitionBuilder builder;
        String beanId;

        builder = BeanDefinitionBuilder.rootBeanDefinition(ListCommandCommand.class);
        beanId = getAvailableBeanName("listCommandCommand", parserContext);
        parserContext.getRegistry().registerBeanDefinition(beanId, builder.getBeanDefinition());
        commandBeanReferences.add(new RuntimeBeanReference(beanId));

        builder = BeanDefinitionBuilder.rootBeanDefinition(ManualCommand.class);
        beanId = getAvailableBeanName("manualCommand", parserContext);
        parserContext.getRegistry().registerBeanDefinition(beanId, builder.getBeanDefinition());
        commandBeanReferences.add(new RuntimeBeanReference(beanId));

        builder = BeanDefinitionBuilder.rootBeanDefinition(QuitCommand.class);
        beanId = getAvailableBeanName("quitCommand", parserContext);
        parserContext.getRegistry().registerBeanDefinition(beanId, builder.getBeanDefinition());
        commandBeanReferences.add(new RuntimeBeanReference(beanId));

        builder = BeanDefinitionBuilder.rootBeanDefinition(ListVariableCommand.class);
        beanId = getAvailableBeanName("listVariableCommand", parserContext);
        parserContext.getRegistry().registerBeanDefinition(beanId, builder.getBeanDefinition());
        commandBeanReferences.add(new RuntimeBeanReference(beanId));

        builder = BeanDefinitionBuilder.rootBeanDefinition(CopyVariableCommand.class);
        beanId = getAvailableBeanName("copyVariableCommand", parserContext);
        parserContext.getRegistry().registerBeanDefinition(beanId, builder.getBeanDefinition());
        commandBeanReferences.add(new RuntimeBeanReference(beanId));
    }

    private String getAvailableBeanName(String baseName, ParserContext parserContext) {
        if (!parserContext.getRegistry().containsBeanDefinition(baseName)) {
            return baseName;
        }
        String actualName;
        int index = 0;
        do {
            actualName = baseName + (index++);
        } while (parserContext.getRegistry().containsBeanDefinition(baseName));
        return actualName;
    }
}
