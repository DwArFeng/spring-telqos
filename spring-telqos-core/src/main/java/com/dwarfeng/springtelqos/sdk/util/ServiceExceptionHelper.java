package com.dwarfeng.springtelqos.sdk.util;

import com.dwarfeng.springtelqos.stack.exception.CommandDescriptorException;
import com.dwarfeng.springtelqos.stack.exception.CommandException;
import com.dwarfeng.springtelqos.stack.exception.CommandExecutorException;
import com.dwarfeng.springtelqos.stack.exception.TelqosException;
import com.dwarfeng.subgrade.stack.exception.ServiceException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 异常的帮助工具类。
 *
 * @author DwArFeng
 * @since 2.0.0
 */
public final class ServiceExceptionHelper {

    /**
     * 向指定的映射中添加 spring-telqos 默认的目标映射。
     *
     * <p>
     * 该方法可以在配置类中快速的搭建目标映射。
     *
     * @param map 指定的映射，允许为 null。
     * @return 添加了默认目标的映射。
     */
    public static Map<Class<? extends Exception>, ServiceException.Code> putDefaultDestination(
            Map<Class<? extends Exception>, ServiceException.Code> map) {
        if (Objects.isNull(map)) {
            map = new HashMap<>();
        }

        map.put(TelqosException.class, ServiceExceptionCodes.TELQOS_FAILED);
        map.put(CommandException.class, ServiceExceptionCodes.COMMAND_FAILED);
        map.put(CommandDescriptorException.class, ServiceExceptionCodes.COMMAND_DESCRIPTOR_FAILED);
        map.put(CommandExecutorException.class, ServiceExceptionCodes.COMMAND_EXECUTOR_FAILED);

        return map;
    }

    private ServiceExceptionHelper() {
        throw new IllegalStateException("禁止外部实例化");
    }
}
