package com.dwarfeng.springtelqos.sdk.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;

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
     * 如果指定的属性是一个 SpEL 表达式，则解析它，否则返回原属性。
     *
     * <p>
     * 需要注意的是，由于 SpEL 解析后的结果可能是一个对象，因此该方法的返回值类型为 Object。
     * 对象的类型需要进行约定，例如约定 SpEL 表达式解析后的结果必须是一个字符串，
     * 或者约定 SpEL 表达式解析后的结果必须是一个特定类型的对象。
     *
     * <p>
     * 需要注意的是，使用本项目解析 SpEL 表达式时，Spring 的状态仍处于初始化阶段，
     * 因此无法使用 Spring 的 BeanFactory 来解析 SpEL 表达式。<br>
     * 本方法是在 Spring 的解析机制之外，额外创建了一个 SpEL 解析器来解析 SpEL 表达式的。<br>
     * 因此，使用本方法解析 SpEL 表达式时，无法使用 Spring 的 BeanFactory 来解析 SpEL 表达式中的占位符，
     * 如无法使用 Spring 的 BeanFactory 提供的 {@link org.springframework.expression.ParserContext}。<br>
     * 因此，在任何可能的情况下，建议先使用 {@link #mayResolvePlaceholder(ParserContext, String)} 方法解析占位符。
     * 比如：
     * <blockquote><pre>
     * BeanDefinition.getPropertyValues().add(propertyName, propertyValue)
     * </pre></blockquote>
     * 其中，<code>propertyValue</code> 既可以直接使用 SpEL 表达式本身，也可以解析 SpEL 表达式后直接使用解析结果。
     * 此时更推荐直接使用 SpEL 表达式本身。
     *
     * @param parserContext Parser 上下文。
     * @param attribute     指定的属性。
     * @return 也许被解析的属性。
     */
    public static Object mayResolveSpel(ParserContext parserContext, String attribute) {
        String resolvedAttribute = parserContext.getReaderContext().getEnvironment().resolvePlaceholders(attribute);

        String trimmed = StringUtils.trim(resolvedAttribute);
        if (StringUtils.isEmpty(trimmed) || !trimmed.contains("#{") || !trimmed.contains("}")) {
            // 如果解析后的字符串不包含 SpEL 表达式的标志，则直接返回解析后的字符串。
            return resolvedAttribute;
        }

        ExpressionParser parser = new SpelExpressionParser();
        Expression expression = parser.parseExpression(resolvedAttribute, new TemplateParserContext());
        return expression.getValue();
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
