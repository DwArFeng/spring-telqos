package com.dwarfeng.springtelqos.base.internal.i18n;

import com.dwarfeng.springtelqos.base.stack.i18n.MessageCatalog;
import com.dwarfeng.springtelqos.base.stack.i18n.MessageResolver;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 基于模块资源的消息解析器。
 *
 * <p>
 * 该实现使用 JDK 标准候选语言环境顺序读取资源所属模块中的 UTF-8 properties，并缓存不可变资源映射。
 * 资源不存在时允许继续回退；已存在资源无法读取或格式非法时视为内部完整性错误。
 *
 * @author DwArFeng
 * @since 3.0.0
 */
public final class ResourceMessageResolver implements MessageResolver {

    private static final ResourceBundle.Control CONTROL =
            ResourceBundle.Control.getControl(ResourceBundle.Control.FORMAT_PROPERTIES);

    private final ConcurrentMap<BundleCacheKey, Optional<Map<String, String>>> cache = new ConcurrentHashMap<>();

    @Override
    public String resolve(MessageCatalog catalog, String key, List<Locale> locales, Object... args) {
        Objects.requireNonNull(catalog, "catalog");
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(locales, "locales");

        for (Locale locale : candidates(catalog, locales)) {
            Optional<Map<String, String>> optionalMessages = cache.computeIfAbsent(
                    new BundleCacheKey(catalog.module(), catalog.baseName(), locale), this::loadMessages
            );
            if (optionalMessages.isEmpty()) {
                continue;
            }
            String pattern = optionalMessages.orElseThrow().get(key);
            if (pattern != null) {
                return new MessageFormat(pattern, locale).format(args == null ? new Object[0] : args);
            }
        }
        return "!" + key + "!";
    }

    private List<Locale> candidates(MessageCatalog catalog, List<Locale> locales) {
        Set<Locale> candidates = new LinkedHashSet<>();
        for (Locale locale : locales) {
            if (locale != null) {
                candidates.addAll(CONTROL.getCandidateLocales(catalog.baseName(), locale));
            }
        }
        candidates.add(Locale.ROOT);
        return List.copyOf(new ArrayList<>(candidates));
    }

    private Optional<Map<String, String>> loadMessages(BundleCacheKey cacheKey) {
        String resourceName = CONTROL.toResourceName(
                CONTROL.toBundleName(cacheKey.baseName(), cacheKey.locale()), "properties"
        );
        try (InputStream inputStream = cacheKey.module().getResourceAsStream(resourceName)) {
            if (inputStream == null) {
                return Optional.empty();
            }
            Properties properties = new Properties();
            properties.load(new InputStreamReader(
                    inputStream,
                    StandardCharsets.UTF_8.newDecoder()
                            .onMalformedInput(CodingErrorAction.REPORT)
                            .onUnmappableCharacter(CodingErrorAction.REPORT)
            ));
            Map<String, String> messages = new java.util.LinkedHashMap<>();
            properties.stringPropertyNames().forEach(key -> messages.put(key, properties.getProperty(key)));
            return Optional.of(Map.copyOf(messages));
        } catch (IOException | IllegalArgumentException exception) {
            throw new IllegalStateException("Failed to load message resource: " + resourceName, exception);
        }
    }
}
