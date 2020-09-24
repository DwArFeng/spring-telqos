package com.dwarfeng.springtelqos.sdk.serialize;

import com.alibaba.fastjson.JSON;
import com.dwarfeng.springtelqos.stack.exception.TelqosException;
import com.dwarfeng.springtelqos.stack.serialize.Serializer;

/**
 * FastJson 序列化器。
 *
 * @author DwArFeng
 * @since 1.0.0
 */
public class FastJsonSerializer implements Serializer {

    @Override
    public <T> T serialize(String string, Class<T> clazz) throws TelqosException {
        try {
            return JSON.parseObject(string, clazz);
        } catch (Exception e) {
            throw new TelqosException(e);
        }
    }
}
