package com.dwarfeng.springtelqos.base.internal.i18n;

import java.util.Locale;
import java.util.Objects;

/**
 * 消息资源缓存键。
 *
 * @param module   资源所属模块。
 * @param baseName 资源基础名称。
 * @param locale   语言环境。
 * @author DwArFeng
 * @since 3.0.0
 */
public record BundleCacheKey(Module module, String baseName, Locale locale) {

    public BundleCacheKey {
        Objects.requireNonNull(module, "module");
        Objects.requireNonNull(baseName, "baseName");
        Objects.requireNonNull(locale, "locale");
    }
}
