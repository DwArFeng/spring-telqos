package com.dwarfeng.springtelqos.sdk.util;

import com.dwarfeng.subgrade.stack.exception.ServiceException;

/**
 * Telqos 服务异常代码。
 *
 * @author DwArFeng
 * @since 2.0.0
 */
public final class ServiceExceptionCodes {

    private static int EXCEPTION_CODE_OFFSET = 21000;

    public static final ServiceException.Code TELQOS_FAILED =
            new ServiceException.Code(offset(0), "telqos failed");

    public static final ServiceException.Code COMMAND_FAILED =
            new ServiceException.Code(offset(10), "command failed");
    public static final ServiceException.Code COMMAND_DESCRIPTOR_FAILED =
            new ServiceException.Code(offset(11), "command descriptor failed");
    public static final ServiceException.Code COMMAND_EXECUTOR_FAILED =
            new ServiceException.Code(offset(12), "command executor failed");

    public static final ServiceException.Code NAMING_STRATEGY_FAILED =
            new ServiceException.Code(offset(20), "naming strategy failed");

    @SuppressWarnings("SameParameterValue")
    private static int offset(int i) {
        return EXCEPTION_CODE_OFFSET + i;
    }

    /**
     * 获取异常代号的偏移量。
     *
     * @return 异常代号的偏移量
     */
    public static int getExceptionCodeOffset() {
        return EXCEPTION_CODE_OFFSET;
    }

    /**
     * 设置异常代号的偏移量。
     *
     * <p>
     * 设置后会按新基准同步更新已声明的 {@link ServiceException.Code} 实例代号，
     * 与 ftp/datamark 工程中的同名方法行为一致。
     *
     * @param exceptionCodeOffset 指定的异常代号偏移量
     */
    public static void setExceptionCodeOffset(int exceptionCodeOffset) {
        EXCEPTION_CODE_OFFSET = exceptionCodeOffset;
        TELQOS_FAILED.setCode(offset(0));
        COMMAND_FAILED.setCode(offset(10));
        COMMAND_DESCRIPTOR_FAILED.setCode(offset(11));
        COMMAND_EXECUTOR_FAILED.setCode(offset(12));
        NAMING_STRATEGY_FAILED.setCode(offset(20));
    }

    private ServiceExceptionCodes() {
        throw new IllegalStateException("禁止实例化");
    }
}
