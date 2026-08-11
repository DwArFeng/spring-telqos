package com.dwarfeng.springtelqos.base.stack.i18n;

import java.util.Objects;

/**
 * 消息目录。
 *
 * <p>
 * 消息目录将资源所属模块与资源基础名称绑定，供 Spring Telqos 内部国际化机制进行模块感知的资源查找。
 *
 * <p>
 * 该类型属于 Spring Telqos 内部集成协议，不承诺面向第三方的源码或二进制兼容性。
 *
 * @param module   资源所属模块。
 * @param baseName 资源基础名称。
 * @author DwArFeng
 * @since 3.0.0
 */
public record MessageCatalog(Module module, String baseName) {

    /**
     * 创建消息目录。
     */
    public MessageCatalog {
        Objects.requireNonNull(module, "module");
        Objects.requireNonNull(baseName, "baseName");
        if (baseName.isBlank()) {
            throw new IllegalArgumentException("baseName must not be blank");
        }
    }

    /**
     * 使用锚点类型所属模块创建消息目录。
     *
     * @param anchor   模块锚点类型。
     * @param baseName 资源基础名称。
     * @return 消息目录。
     */
    public static MessageCatalog of(Class<?> anchor, String baseName) {
        Objects.requireNonNull(anchor, "anchor");
        return new MessageCatalog(anchor.getModule(), baseName);
    }
}
