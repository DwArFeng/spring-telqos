package com.dwarfeng.springtelqos.node.config;

import com.dwarfeng.springtelqos.impl.command.ListCommandCommand;
import com.dwarfeng.springtelqos.impl.command.ManualCommand;
import com.dwarfeng.springtelqos.impl.command.QuitCommand;
import com.dwarfeng.springtelqos.impl.service.TelqosServiceImpl;
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

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Telqos Connection 相关的 BeanDefinitionParser。
 *
 * @author DwArFeng
 * @since 1.0.0
 */
public class SpringTelqosDefinitionParser implements BeanDefinitionParser {

    private static final String TELQOS_NAMESPACE_URL = "http://dwarfeng.com/schema/spring-telqos";

    @Override
    public BeanDefinition parse(Element element, @Nonnull ParserContext parserContext) {
        // 获取 bean 名称。
        String configId = ParserUtil.mayResolve(parserContext, element.getAttribute("config-id"));
        String serviceId = ParserUtil.mayResolve(parserContext, element.getAttribute("service-id"));
        // 检查 bean 名称是否重复。
        checkBeanDuplicated(parserContext, configId);
        checkBeanDuplicated(parserContext, serviceId);
        // 构造 TelqosConfig。
        BeanDefinitionBuilder telqosConfigBuilder = BeanDefinitionBuilder.rootBeanDefinition(TelqosConfig.class);
        // 解析 connection-setting。
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
        // 解析 command。
        Element commandElement = (Element) element.getElementsByTagNameNS(
                TELQOS_NAMESPACE_URL, "command").item(0);
        ManagedList<BeanReference> commandBeanReferences = new ManagedList<>();
        registerDefaultCommand(commandBeanReferences, parserContext);
        if (Objects.nonNull(commandElement)) {
            NodeList commandImpls = element.getElementsByTagNameNS(TELQOS_NAMESPACE_URL, "command-impl");
            for (int i = 0; i < commandImpls.getLength(); i++) {
                Element commandImplElement = (Element) commandImpls.item(i);
                commandBeanReferences.addAll(parseCommandImpl(commandImplElement, parserContext));
            }
        }
        telqosConfigBuilder.addPropertyValue("commands", commandBeanReferences);
        // 解析 task-pool。
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
        // 注册 TelqosConfig。
        telqosConfigBuilder.setScope(BeanDefinition.SCOPE_SINGLETON);
        telqosConfigBuilder.setLazyInit(false);
        parserContext.getRegistry().registerBeanDefinition(configId, telqosConfigBuilder.getBeanDefinition());
        // 构造 TelqosService。
        BeanDefinitionBuilder telqosServiceBuilder = BeanDefinitionBuilder.rootBeanDefinition(TelqosServiceImpl.class);
        // TelqosService 参数赋值。
        telqosServiceBuilder.addPropertyValue("telqosConfig", new RuntimeBeanReference(configId));
        // 注册构造 TelqosService。
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

    private Set<BeanReference> parseCommandImpl(Element element, ParserContext parserContext) {
        // 展开元素中所有可能出现的属性。
        String id = ParserUtil.mayResolve(parserContext, element.getAttribute("id"));
        String clazz = ParserUtil.mayResolve(parserContext, element.getAttribute("class"));
        String ref = ParserUtil.mayResolve(parserContext, element.getAttribute("ref"));
        String packageScan = ParserUtil.mayResolve(parserContext, element.getAttribute("package-scan"));

        /*
         * 优先级：ref > package-scan > id-class
         */

        // 如果 ref 不为空，按照 ref 逻辑处理。
        if (StringUtils.isNotEmpty(ref)) {
            return Collections.singleton(new RuntimeBeanReference(ref));
        }
        // 如果 package-scan 不为空，按照 package-scan 逻辑处理。
        else if (StringUtils.isNotEmpty(packageScan)) {
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
        }
        // 否则，按照 id-class 逻辑处理。
        else {
            checkBeanDuplicated(parserContext, id);
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(clazz);
            builder.setScope(BeanDefinition.SCOPE_SINGLETON);
            builder.setLazyInit(false);
            parserContext.getRegistry().registerBeanDefinition(id, builder.getBeanDefinition());
            return Collections.singleton(new RuntimeBeanReference(id));
        }
    }

    private void registerDefaultCommand(ManagedList<BeanReference> commandBeanReferences, ParserContext parserContext) {
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
    }

    private String getAvailableBeanName(String baseName, ParserContext parserContext) {
        if (!parserContext.getRegistry().containsBeanDefinition(baseName)) {
            return baseName;
        }
        String actualName;
        int index = 1;
        do {
            actualName = baseName + (index++);
        } while (parserContext.getRegistry().containsBeanDefinition(actualName));
        return actualName;
    }
}
