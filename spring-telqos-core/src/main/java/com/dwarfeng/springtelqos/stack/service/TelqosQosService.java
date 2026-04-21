package com.dwarfeng.springtelqos.stack.service;

import com.dwarfeng.subgrade.stack.exception.ServiceException;
import com.dwarfeng.subgrade.stack.service.Service;

/**
 * Telqos QoS 服务。
 *
 * @author DwArFeng
 * @since 2.0.0
 */
public interface TelqosQosService extends Service {

    /**
     * 返回服务是否启动。
     *
     * @return 服务是否启动。
     * @throws ServiceException 服务异常。
     */
    boolean isStarted() throws ServiceException;

    /**
     * 启动服务。
     *
     * @throws ServiceException 服务异常。
     */
    void start() throws ServiceException;

    /**
     * 停止服务。
     *
     * @throws ServiceException 服务异常。
     */
    void stop() throws ServiceException;
}
