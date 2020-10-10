package com.dwarfeng.springtelqos.node.config;

import org.springframework.beans.factory.xml.ParserContext;

/**
 * Parser 工具类。
 *
 * @author DwArFeng
 * @since 1.0.0
 */
public final class ParserUtil {

    /**
     * 如果指定的属性是一个 placeHolder，则解析它，否则返回原属性。
     *
     * @param parserContext Parser上下文。
     * @param attribute     指定的属性。
     * @return 也许被解析的属性。
     */
    public static String mayResolve(ParserContext parserContext, String attribute) {
        return parserContext.getReaderContext().getEnvironment().resolvePlaceholders(attribute);
    }
}
