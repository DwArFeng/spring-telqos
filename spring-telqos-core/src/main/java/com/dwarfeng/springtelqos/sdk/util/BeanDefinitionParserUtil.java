package com.dwarfeng.springtelqos.sdk.util;

import org.springframework.beans.factory.xml.ParserContext;

/**
 * Spring XML 命名空间解析器工具类。
 *
 * @author DwArFeng
 * @since 1.0.0
 */
public final class BeanDefinitionParserUtil {

    /**
     * 如果指定的属性是一个 placeHolder，则解析它，否则返回原属性。
     *
     * @param parserContext Parser 上下文。
     * @param attribute     指定的属性。
     * @return 也许被解析的属性。
     */
    public static String mayResolvePlaceholder(ParserContext parserContext, String attribute) {
        return parserContext.getReaderContext().getEnvironment().resolvePlaceholders(attribute);
    }

    /**
     * 若指定 id 已注册为 BeanDefinition，则抛出异常。
     *
     * @param parserContext 解析上下文。
     * @param id            bean 名称。
     */
    public static void makeSureBeanNameNotDuplicated(ParserContext parserContext, String id) {
        if (parserContext.getRegistry().containsBeanDefinition(id)) {
            throw new IllegalStateException("Duplicated spring bean id " + id);
        }
    }

    /**
     * 解析可用的 bean 名称。
     *
     * <p>
     * 如果基名已被占用，则在基名后追加序号，直到找到一个未被占用的 bean 名称。
     *
     * @param parserContext 解析上下文。
     * @param baseName      基名。
     * @return 未占用的 bean 名称。
     */
    public static String parseAvailableBeanName(ParserContext parserContext, String baseName) {
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

    private BeanDefinitionParserUtil() {
        throw new IllegalStateException("禁止实例化");
    }
}
