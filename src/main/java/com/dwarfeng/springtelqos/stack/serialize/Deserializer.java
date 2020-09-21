package com.dwarfeng.springtelqos.stack.serialize;

import com.dwarfeng.springtelqos.stack.exception.TelqosException;

/**
 * 反序列化器。
 *
 * @author DwArFeng
 * @since 1.0.0
 */
public interface Deserializer {

    /**
     * 反序列化。
     *
     * @param object 指定的对象。
     * @return 指定的对象反序列化后生成的字符串。
     * @throws TelqosException Telqos异常。
     */
    String deserialize(Object object) throws TelqosException;
}
