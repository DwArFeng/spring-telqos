package com.dwarfeng.springtelqos.sdk.exception;

import com.dwarfeng.springtelqos.base.sdk.i18n.MessageContext;
import com.dwarfeng.subgrade.basic.stack.exception.ServiceException;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

/**
 * Telqos 模块异常代码供应器测试。
 *
 * @author DwArFeng
 * @since 3.0.0
 */
public class ServiceExceptionCodeSuppliersTest {

    @Test
    public void shouldResolveTipForCurrentMessageContext() {
        ServiceException english = MessageContext.call(
                Locale.ENGLISH, () -> new ServiceException(ServiceExceptionCodeSuppliers.TELQOS_FAILED.get())
        );
        ServiceException chinese = MessageContext.call(
                Locale.SIMPLIFIED_CHINESE, () -> new ServiceException(ServiceExceptionCodeSuppliers.TELQOS_FAILED.get())
        );

        assertEquals("Telqos operation failed", english.getCode().getTip());
        assertEquals("Telqos 操作失败", chinese.getCode().getTip());
    }

    @Test
    public void shouldApplyUpdatedOffsetToNewCodesOnly() {
        int previousOffset = ServiceExceptionCodeSuppliers.getExceptionCodeOffset();
        ServiceException.Code original = ServiceExceptionCodeSuppliers.TELQOS_FAILED.get();
        try {
            ServiceExceptionCodeSuppliers.setExceptionCodeOffset(9000);
            ServiceException.Code updated = ServiceExceptionCodeSuppliers.TELQOS_FAILED.get();

            assertEquals(previousOffset, original.getCode());
            assertEquals(9000, updated.getCode());
            assertNotSame(original, updated);
        } finally {
            ServiceExceptionCodeSuppliers.setExceptionCodeOffset(previousOffset);
        }
    }
}
