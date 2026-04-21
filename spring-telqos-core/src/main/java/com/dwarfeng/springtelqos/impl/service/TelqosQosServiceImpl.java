package com.dwarfeng.springtelqos.impl.service;

import com.dwarfeng.springtelqos.stack.handler.TelqosHandler;
import com.dwarfeng.springtelqos.stack.service.TelqosQosService;
import com.dwarfeng.subgrade.sdk.exception.ServiceExceptionHelper;
import com.dwarfeng.subgrade.stack.exception.ServiceException;
import com.dwarfeng.subgrade.stack.exception.ServiceExceptionMapper;
import com.dwarfeng.subgrade.stack.log.LogLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PreDestroy;

public class TelqosQosServiceImpl implements TelqosQosService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TelqosQosServiceImpl.class);

    private final TelqosHandler telqosHandler;
    private final ServiceExceptionMapper sem;

    public TelqosQosServiceImpl(TelqosHandler telqosHandler, ServiceExceptionMapper sem) {
        this.telqosHandler = telqosHandler;
        this.sem = sem;
    }

    @PreDestroy
    public void preDestroy() {
        try {
            telqosHandler.stop();
        } catch (Exception e) {
            LOGGER.warn("容器销毁时停止 Telqos 服务失败，将忽略该异常", e);
        }
    }

    @Override
    public boolean isStarted() throws ServiceException {
        try {
            return telqosHandler.isStarted();
        } catch (Exception e) {
            throw ServiceExceptionHelper.logParse("返回服务是否启动时发生异常", LogLevel.WARN, e, sem);
        }
    }

    @Override
    public void start() throws ServiceException {
        try {
            telqosHandler.start();
        } catch (Exception e) {
            throw ServiceExceptionHelper.logParse("启动服务时发生异常", LogLevel.WARN, e, sem);
        }
    }

    @Override
    public void stop() throws ServiceException {
        try {
            telqosHandler.stop();
        } catch (Exception e) {
            throw ServiceExceptionHelper.logParse("停止服务时发生异常", LogLevel.WARN, e, sem);
        }
    }
}
