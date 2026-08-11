package com.dwarfeng.springtelqos.stack.struct;

import com.dwarfeng.springtelqos.stack.command.Command;
import com.dwarfeng.springtelqos.stack.naming.NamingStrategy;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TelqosConfig 兼容性测试。
 *
 * @author DwArFeng
 * @since 3.0.0
 */
class TelqosConfigTest {

    @Test
    void preservesConstructorAndJavaBeanGetters() {
        NamingStrategy namingStrategy = info -> "test";
        var commands = Collections.<Command>emptyList();

        TelqosConfig config = new TelqosConfig(
                23, "white", "black", "UTF-8", "classpath:banner.txt", commands, namingStrategy
        );

        assertFalse(TelqosConfig.class.isRecord());
        assertEquals(23, config.getPort());
        assertEquals("white", config.getWhitelistRegex());
        assertEquals("black", config.getBlacklistRegex());
        assertEquals("UTF-8", config.getCharset());
        assertEquals("classpath:banner.txt", config.getBannerUrl());
        assertSame(commands, config.getCommands());
        assertSame(namingStrategy, config.getNamingStrategy());
    }

    @Test
    void usesPackagedDefaultBannerResource() throws IOException {
        TelqosConfig config = new TelqosConfig.Builder().build();

        assertEquals(TelqosConfig.Builder.DEFAULT_BANNER_URL, config.getBannerUrl());
        String resourceName = config.getBannerUrl().substring("classpath:".length());
        try (InputStream inputStream = TelqosConfig.class.getModule().getResourceAsStream(resourceName)) {
            assertNotNull(inputStream);
        }
    }
}
