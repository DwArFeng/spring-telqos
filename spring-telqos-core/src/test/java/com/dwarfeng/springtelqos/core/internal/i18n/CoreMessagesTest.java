package com.dwarfeng.springtelqos.core.internal.i18n;

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
 * Core 模块消息测试。
 *
 * @author DwArFeng
 * @since 3.0.0
 */
public class CoreMessagesTest {

    @Test
    public void resolvesSimplifiedChineseMessagesFromEveryCatalog() {
        assertEquals(
                "指令选项不正确，请检查指令选项: test",
                resolveWith(Locale.SIMPLIFIED_CHINESE, CoreMessageKey.COMMAND_OPTION_INVALID, "test")
        );
        assertEquals(
                "共 3 条",
                resolveWith(Locale.SIMPLIFIED_CHINESE, CoreMessageKey.COMMAND_LIST_TOTAL, 3)
        );
    }

    @Test
    public void resolvesEnglishFallbackMessagesFromEveryCatalog() {
        assertEquals(
                "The command options are invalid. Check the command options: test",
                CoreMessages.message(Locale.US, CoreMessageKey.COMMAND_OPTION_INVALID, "test")
        );
        assertEquals("Total: 3", resolveWith(Locale.US, CoreMessageKey.COMMAND_LIST_TOTAL, 3));
    }

    @Test
    public void degradesMissingMessageKeyForEveryCatalog() {
        for (CoreMessages.Catalog catalog : CoreMessages.Catalog.values()) {
            assertEquals(
                    "!unknown.message.key!",
                    Messages.resolve(catalog.messageCatalog(), "unknown.message.key", Locale.US)
            );
        }
    }

    @Test
    public void keepsMessageKeyResourcesAlignedByCatalog() throws IOException {
        for (CoreMessages.Catalog catalog : CoreMessages.Catalog.values()) {
            List<CoreMessageKey> catalogKeys = Arrays.stream(CoreMessageKey.values())
                    .filter(key -> key.catalog() == catalog)
                    .toList();
            Set<String> enumKeys = catalogKeys.stream()
                    .map(CoreMessageKey::key)
                    .collect(Collectors.toUnmodifiableSet());
            assertEquals(catalogKeys.size(), enumKeys.size(), () -> "Duplicate message key in catalog: " + catalog);
            assertEquals(enumKeys, loadKeys(catalog, ".properties"));
            assertEquals(enumKeys, loadKeys(catalog, "_zh_CN.properties"));
        }
    }

    @Test
    public void cachesOneMessageCatalogPerCatalog() {
        for (CoreMessages.Catalog catalog : CoreMessages.Catalog.values()) {
            MessageCatalog messageCatalog = catalog.messageCatalog();
            Arrays.stream(CoreMessageKey.values())
                    .filter(key -> key.catalog() == catalog)
                    .forEach(key -> assertSame(messageCatalog, key.catalog().messageCatalog()));
        }
    }

    private String resolveWith(Locale locale, CoreMessageKey key, Object... args) {
        AtomicReference<String> reference = new AtomicReference<>();
        MessageContext.run(locale, () -> reference.set(CoreMessages.message(key, args)));
        return reference.get();
    }

    private Set<String> loadKeys(CoreMessages.Catalog catalog, String resourceSuffix) throws IOException {
        String resourceName = catalog.messageCatalog().baseName().replace('.', '/') + resourceSuffix;
        try (InputStream inputStream = CoreMessages.class.getModule().getResourceAsStream(resourceName)) {
            assertNotNull(inputStream, () -> "Missing message resource: " + resourceName);
            Properties properties = new Properties();
            properties.load(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            return properties.stringPropertyNames();
        }
    }
}
