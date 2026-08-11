package com.dwarfeng.springtelqos.base.sdk.i18n;

import com.dwarfeng.springtelqos.base.internal.i18n.ResourceMessageResolver;
import com.dwarfeng.springtelqos.base.stack.i18n.MessageCatalog;
import com.dwarfeng.springtelqos.base.stack.i18n.MessageResolver;

import java.util.*;

/**
 * 消息解析工具。
 *
 * <p>
 * 该工具为 Spring Telqos 各模块提供统一的语言环境调度、资源查找、格式化与缺失资源降级。
 *
 * <p>
 * 该类型属于 Spring Telqos 内部集成机制。
 *
 * @author DwArFeng
 * @since 3.0.0
 */
public final class Messages {

    private static final MessageResolver RESOLVER = new ResourceMessageResolver();

    /**
     * 使用当前上下文语言环境解析消息。
     *
     * @param catalog 消息目录。
     * @param key     消息键。
     * @param args    格式化参数。
     * @return 解析后的消息。
     */
    public static String resolve(MessageCatalog catalog, String key, Object... args) {
        return RESOLVER.resolve(catalog, key, locales(null), args);
    }

    /**
     * 使用显式语言环境解析消息。
     *
     * @param catalog 消息目录。
     * @param key     消息键。
     * @param locale  显式语言环境。
     * @param args    格式化参数。
     * @return 解析后的消息。
     */
    public static String resolve(MessageCatalog catalog, String key, Locale locale, Object... args) {
        Objects.requireNonNull(locale, "locale");
        return RESOLVER.resolve(catalog, key, locales(locale), args);
    }

    private static List<Locale> locales(Locale explicitLocale) {
        Set<Locale> locales = new LinkedHashSet<>();
        if (explicitLocale != null) {
            locales.add(explicitLocale);
        }
        if (MessageContext.isBound()) {
            locales.add(MessageContext.currentLocale());
        }
        locales.add(Locale.getDefault(Locale.Category.DISPLAY));
        locales.add(Locale.ROOT);
        return List.copyOf(new ArrayList<>(locales));
    }

    private Messages() {
        throw new AssertionError("No instances");
    }
}
