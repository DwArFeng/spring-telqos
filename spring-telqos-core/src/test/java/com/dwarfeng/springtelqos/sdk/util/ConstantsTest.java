package com.dwarfeng.springtelqos.sdk.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 指令标识符格式测试。
 *
 * @author DwArFeng
 * @since 1.2.0
 */
public class ConstantsTest {

    @Test
    public void testConstantsCommandIdentityFormat() {
        String[] legalIdentities = new String[]{
                "lc",
                "man",
                "quit",
                "ops:status",
                "module.user-list",
                "api_v2:reload",
                "_internal.v1",
                "a-b-c"
        };
        for (String identity : legalIdentities) {
            assertTrue(identity.matches(Constants.COMMAND_IDENTITY_FORMAT), "应为合法标识符: " + identity);
        }

        String[] illegalIdentities = new String[]{
                "",
                "1abc",
                " abc",
                "abc ",
                "abc def",
                "abc\tdef",
                "abc*def",
                "abc..def",
                "abc::def",
                ".abc",
                "abc-"
        };
        for (String identity : illegalIdentities) {
            assertFalse(identity.matches(Constants.COMMAND_IDENTITY_FORMAT), "应为非法标识符: " + identity);
        }
    }
}
