package com.dwarfeng.springtelqos.sdk.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CliCommandUtil 兼容性测试。
 *
 * @author DwArFeng
 * @since 3.0.0
 */
class CliCommandUtilTest {

    @Test
    void preservesCropResultJavaBeanGetters() {
        CliCommandUtil.CropResult cropResult = new CliCommandUtil.CropResult(3, 8, true);

        assertEquals(3, cropResult.beginIndex());
        assertEquals(8, cropResult.endIndex());
        assertTrue(cropResult.exitFlag());
        assertFalse(new CliCommandUtil.CropResult(0, 0, false).exitFlag());
    }
}
