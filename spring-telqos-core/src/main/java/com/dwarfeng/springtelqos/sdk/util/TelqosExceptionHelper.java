package com.dwarfeng.springtelqos.sdk.util;

import com.dwarfeng.springtelqos.stack.exception.TelqosException;
import com.dwarfeng.subgrade.basic.stack.exception.HandlerException;

import org.jetbrains.annotations.NotNull;

/**
 * Telqos 异常帮助类。
 *
 * @author DwArFeng
 * @since 1.4.4
 */
public final class TelqosExceptionHelper {

    /**
     * 将指定的异常转化为处理器异常。
     *
     * @param e 指定的异常。
     * @return 解析后得到的处理器异常。
     */
    public static HandlerException parse(@NotNull Exception e) {
        if (e instanceof HandlerException) {
            return (HandlerException) e;
        }
        return new TelqosException(e);
    }

    private TelqosExceptionHelper() {
        throw new IllegalStateException("禁止实例化");
    }
}
