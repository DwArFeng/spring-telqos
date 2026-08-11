package com.dwarfeng.springtelqos.sdk.configuration;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultBeanNameGenerator;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.env.Environment;
import org.springframework.core.type.filter.TypeFilter;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Command 类路径 Bean 定义扫描器。
 *
 * @author DwArFeng
 * @since 1.1.7
 */
public final class CommandClassPathBeanDefinitionScanner extends ClassPathBeanDefinitionScanner {

    private final Set<String> scannedBeanNames = new LinkedHashSet<>();

    public CommandClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry, Environment environment) {
        super(registry, false, environment);
        super.addIncludeFilter(CommandTypeFilter.INSTANCE);
        super.setBeanNameGenerator(DefaultBeanNameGenerator.INSTANCE);
    }

    @Override
    public void addIncludeFilter(@NotNull TypeFilter includeFilter) {
        throw new UnsupportedOperationException("不支持的操作");
    }

    @Override
    public void addExcludeFilter(@NotNull TypeFilter excludeFilter) {
        throw new UnsupportedOperationException("不支持的操作");
    }

    @Override
    protected void postProcessBeanDefinition(
            @NotNull AbstractBeanDefinition beanDefinition, @NotNull String beanName
    ) {
        scannedBeanNames.add(beanName);
    }

    public Set<String> getScannedBeanNames() {
        return scannedBeanNames;
    }

    @Override
    public String toString() {
        return "CommandClassPathBeanDefinitionScanner{" +
                "scannedBeanNames=" + scannedBeanNames +
                '}';
    }
}
