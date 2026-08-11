package com.dwarfeng.springtelqos.sdk.exception;

import com.dwarfeng.springtelqos.stack.exception.TelqosException;
import com.dwarfeng.subgrade.basic.impl.exception.MapServiceExceptionMapper;
import com.dwarfeng.subgrade.basic.stack.exception.ServiceException;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Telqos 模块服务异常帮助类测试。
 *
 * @author DwArFeng
 * @since 3.0.0
 */
public class ServiceExceptionHelperTest {

    @Test
    public void shouldProvideDefaultDestinationForMapper() {
        Map<Class<? extends Exception>, Supplier<ServiceException.Code>> destination =
                ServiceExceptionHelper.putDefaultDestination(null);
        MapServiceExceptionMapper mapper = new MapServiceExceptionMapper(
                destination, com.dwarfeng.subgrade.basic.sdk.exception.ServiceExceptionCodeSuppliers.UNDEFINED
        );

        ServiceException first = mapper.map(new TelqosException());
        ServiceException second = mapper.map(new TelqosException());

        assertSame(ServiceExceptionCodeSuppliers.TELQOS_FAILED, destination.get(TelqosException.class));
        assertEquals(ServiceExceptionCodeSuppliers.getExceptionCodeOffset(), first.getCode().getCode());
        assertEquals(ServiceExceptionCodeSuppliers.getExceptionCodeOffset(), second.getCode().getCode());
        assertNotSame(first.getCode(), second.getCode());
    }
}
