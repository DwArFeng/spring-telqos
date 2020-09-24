package com.dwarfeng.springtelqos.stack.serialize;

import com.dwarfeng.springtelqos.stack.exception.TelqosException;

/**
 * 序列化器。
 *
 * @author DwArFeng
 * @since 1.0.0
 */
public interface Serializer {

    /**
     * 序列化。
     *
     * @param string 指定的问题。
     * @param clazz  序列化的目标对象的类。
     * @param <T>    序列化的目标对象的类型。
     * @return 序列化之后的对象。
     */
    <T> T serialize(String string, Class<T> clazz) throws TelqosException;
}
