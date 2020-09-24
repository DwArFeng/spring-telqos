package com.dwarfeng.springtelqos.sdk.serialize;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.dwarfeng.springtelqos.stack.exception.TelqosException;
import com.dwarfeng.springtelqos.stack.serialize.Deserializer;

/**
 * FastJson 反序列化器。
 *
 * @author DwArFeng
 * @since 1.0.0
 */
public class FastJsonDeserializer implements Deserializer {

    @Override
    public String deserialize(Object object) throws TelqosException {
        try {
            return JSON.toJSONString(object,
                    SerializerFeature.WriteClassName,
                    SerializerFeature.DisableCircularReferenceDetect,
                    SerializerFeature.WriteMapNullValue);
        } catch (Exception e) {
            throw new TelqosException(e);
        }
    }
}
