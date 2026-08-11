package com.dwarfeng.springtelqos.sdk.exception;

import com.dwarfeng.springtelqos.core.internal.i18n.CoreMessageKey;
import com.dwarfeng.springtelqos.core.internal.i18n.CoreMessages;
import com.dwarfeng.subgrade.basic.stack.exception.ServiceException;

import java.util.function.Supplier;

/**
 * Telqos 模块服务异常代码供应器。
 *
 * @author DwArFeng
 * @since 3.0.0
 */
public final class ServiceExceptionCodeSuppliers {

    private static volatile int EXCEPTION_CODE_OFFSET = 21000;

    /**
     * Telqos 操作失败。
     */
    public static final Supplier<ServiceException.Code> TELQOS_FAILED =
            () -> new ServiceException.Code(
                    offset(0), CoreMessages.message(CoreMessageKey.SERVICE_EXCEPTION_TELQOS_FAILED)
            );

    /**
     * 指令处理失败。
     */
    public static final Supplier<ServiceException.Code> COMMAND_FAILED =
            () -> new ServiceException.Code(
                    offset(10), CoreMessages.message(CoreMessageKey.SERVICE_EXCEPTION_COMMAND_FAILED)
            );

    /**
     * 指令描述器处理失败。
     */
    public static final Supplier<ServiceException.Code> COMMAND_DESCRIPTOR_FAILED =
            () -> new ServiceException.Code(
                    offset(11), CoreMessages.message(CoreMessageKey.SERVICE_EXCEPTION_COMMAND_DESCRIPTOR_FAILED)
            );

    /**
     * 指令执行器处理失败。
     */
    public static final Supplier<ServiceException.Code> COMMAND_EXECUTOR_FAILED =
            () -> new ServiceException.Code(
                    offset(12), CoreMessages.message(CoreMessageKey.SERVICE_EXCEPTION_COMMAND_EXECUTOR_FAILED)
            );

    /**
     * 命名策略处理失败。
     */
    public static final Supplier<ServiceException.Code> NAMING_STRATEGY_FAILED =
            () -> new ServiceException.Code(
                    offset(20), CoreMessages.message(CoreMessageKey.SERVICE_EXCEPTION_NAMING_STRATEGY_FAILED)
            );

    private static int offset(int i) {
        return EXCEPTION_CODE_OFFSET + i;
    }

    /**
     * 获取异常代码的偏移量。
     *
     * @return 异常代码的偏移量。
     */
    public static int getExceptionCodeOffset() {
        return EXCEPTION_CODE_OFFSET;
    }

    /**
     * 设置异常代码的偏移量。
     *
     * <p>
     * 该方法只更新后续生成异常代码所使用的偏移量，已经创建的异常代码保持不变。
     *
     * @param exceptionCodeOffset 指定的异常代码偏移量。
     */
    public static void setExceptionCodeOffset(int exceptionCodeOffset) {
        EXCEPTION_CODE_OFFSET = exceptionCodeOffset;
    }

    private ServiceExceptionCodeSuppliers() {
        throw new IllegalStateException("禁止实例化");
    }
}
