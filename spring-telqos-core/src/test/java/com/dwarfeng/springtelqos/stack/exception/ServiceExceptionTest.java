package com.dwarfeng.springtelqos.stack.exception;

import com.dwarfeng.springtelqos.sdk.exception.ServiceExceptionCodeSuppliers;
import com.dwarfeng.subgrade.basic.stack.exception.ServiceException;
import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 服务异常序列化测试。
 *
 * @author DwArFeng
 * @since 3.0.0
 */
public class ServiceExceptionTest {

    @Test
    public void shouldRetainCodeTipAndMessageAfterSerialization() throws IOException, ClassNotFoundException {
        ServiceException original = new ServiceException(ServiceExceptionCodeSuppliers.TELQOS_FAILED.get());
        byte[] bytes;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream)) {
            objectOutputStream.writeObject(original);
            bytes = outputStream.toByteArray();
        }

        ServiceException restored;
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
             ObjectInputStream objectInputStream = new ObjectInputStream(inputStream)) {
            restored = (ServiceException) objectInputStream.readObject();
        }

        assertEquals(original.getCode().getCode(), restored.getCode().getCode());
        assertEquals(original.getCode().getTip(), restored.getCode().getTip());
        assertEquals(original.getMessage(), restored.getMessage());
    }
}
