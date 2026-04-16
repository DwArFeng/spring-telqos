package com.dwarfeng.springtelqos.sdk.util;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 指令标识符格式测试。
 *
 * @author DwArFeng
 * @since 1.2.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/application-context*.xml")
public class CommandIdentityFormatTest {

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
            Assert.assertTrue("应为合法标识符: " + identity, identity.matches(Constants.COMMAND_IDENTITY_FORMAT));
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
            Assert.assertFalse("应为非法标识符: " + identity, identity.matches(Constants.COMMAND_IDENTITY_FORMAT));
        }
    }
}
