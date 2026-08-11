package com.dwarfeng.springtelqos.api.internal.i18n;

import com.dwarfeng.springtelqos.base.sdk.i18n.MessageContext;
import com.dwarfeng.springtelqos.base.sdk.i18n.Messages;
import com.dwarfeng.springtelqos.base.stack.i18n.MessageCatalog;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * API 模块消息测试。
 *
 * @author DwArFeng
 * @since 3.0.0
 */
public class ApiMessagesTest {

    @Test
    public void resolvesSimplifiedChineseMessage() {
        assertEquals(
                "Log4j2 已重新配置!",
                ApiMessages.message(Locale.SIMPLIFIED_CHINESE, ApiMessageKey.COMMAND_LOG4J2_RECONFIGURED)
        );
    }

    @Test
    public void resolvesEnglishFallbackMessage() {
        assertEquals(
                "Log4j2 has been reconfigured!",
                resolveWith(Locale.US, ApiMessageKey.COMMAND_LOG4J2_RECONFIGURED)
        );
    }

    @Test
    public void degradesMissingMessageKeyForEveryCatalog() {
        for (ApiMessages.Catalog catalog : ApiMessages.Catalog.values()) {
            assertEquals(
                    "!unknown.message.key!",
                    Messages.resolve(catalog.messageCatalog(), "unknown.message.key", Locale.US)
            );
        }
    }

    @Test
    public void keepsMessageKeyResourcesAlignedByCatalog() throws IOException {
        for (ApiMessages.Catalog catalog : ApiMessages.Catalog.values()) {
            List<ApiMessageKey> catalogKeys = Arrays.stream(ApiMessageKey.values())
                    .filter(key -> key.catalog() == catalog)
                    .toList();
            Set<String> enumKeys = catalogKeys.stream()
                    .map(ApiMessageKey::key)
                    .collect(Collectors.toUnmodifiableSet());
            assertEquals(catalogKeys.size(), enumKeys.size(), () -> "Duplicate message key in catalog: " + catalog);
            assertEquals(enumKeys, loadKeys(catalog, ".properties"));
            assertEquals(enumKeys, loadKeys(catalog, "_zh_CN.properties"));
        }
    }

    @Test
    public void cachesOneMessageCatalogPerCatalog() {
        for (ApiMessages.Catalog catalog : ApiMessages.Catalog.values()) {
            MessageCatalog messageCatalog = catalog.messageCatalog();
            Arrays.stream(ApiMessageKey.values())
                    .filter(key -> key.catalog() == catalog)
                    .forEach(key -> assertSame(messageCatalog, key.catalog().messageCatalog()));
        }
    }

    private String resolveWith(Locale locale, ApiMessageKey key, Object... args) {
        AtomicReference<String> reference = new AtomicReference<>();
        MessageContext.run(locale, () -> reference.set(ApiMessages.message(key, args)));
        return reference.get();
    }

    private Set<String> loadKeys(ApiMessages.Catalog catalog, String resourceSuffix) throws IOException {
        String resourceName = catalog.messageCatalog().baseName().replace('.', '/') + resourceSuffix;
        try (InputStream inputStream = ApiMessages.class.getModule().getResourceAsStream(resourceName)) {
            assertNotNull(inputStream, () -> "Missing message resource: " + resourceName);
            Properties properties = new Properties();
            properties.load(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            return properties.stringPropertyNames();
        }
    }
}
