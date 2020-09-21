package com.dwarfeng.springtelqos.sdk.serialize;

import com.dwarfeng.springtelqos.stack.serialize.Deserializer;

import java.util.Objects;

/**
 * FastJson 反序列化器。
 *
 * @author DwArFeng
 * @since 1.0.0
 */
public class FastJsonDeserializer implements Deserializer {

    @Override
    public String deserialize(Object object) {
        return Objects.toString(object);
    }
}
