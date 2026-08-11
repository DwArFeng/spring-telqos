package com.dwarfeng.springtelqos.base.stack.i18n;

import java.util.List;
import java.util.Locale;

/**
 * 消息解析器。
 *
 * <p>
 * 该协议供 Spring Telqos 内部模块解析各自维护的静态消息资源，不是通用国际化框架。
 *
 * @author DwArFeng
 * @since 3.0.0
 */
@FunctionalInterface
public interface MessageResolver {

    /**
     * 按候选语言环境顺序解析并格式化消息。
     *
     * @param catalog 消息目录。
     * @param key     消息键。
     * @param locales 候选语言环境。
     * @param args    格式化参数。
     * @return 解析后的消息；找不到消息时返回安全降级文本。
     */
    String resolve(MessageCatalog catalog, String key, List<Locale> locales, Object... args);
}
