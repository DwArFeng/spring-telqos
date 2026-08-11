package com.dwarfeng.springtelqos.api.internal.i18n;

import static com.dwarfeng.springtelqos.api.internal.i18n.ApiMessages.Catalog.API;

/**
 * API 模块消息键。
 *
 * @author DwArFeng
 * @since 3.0.0
 */
public enum ApiMessageKey {

    COMMAND_DUBBO_DESCRIPTION(API, "command.dubbo.description"),
    COMMAND_DUBBO_ONLINE_OPTION(API, "command.dubbo.online_option"),
    COMMAND_DUBBO_OFFLINE_OPTION(API, "command.dubbo.offline_option"),
    COMMAND_DUBBO_LIST_OPTION(API, "command.dubbo.list_option"),
    COMMAND_LOG4J2_DESCRIPTION(API, "command.log4j2.description"),
    COMMAND_LOG4J2_RECONFIGURE_OPTION(API, "command.log4j2.reconfigure_option"),
    COMMAND_LOG4J2_RECONFIGURED(API, "command.log4j2.reconfigured"),
    COMMAND_MEMORY_DESCRIPTION(API, "command.memory.description"),
    COMMAND_MEMORY_UNIT_OPTION(API, "command.memory.unit_option"),
    COMMAND_MEMORY_INVALID_UNIT(API, "command.memory.invalid_unit"),
    COMMAND_MEMORY_MAX(API, "command.memory.max"),
    COMMAND_MEMORY_TOTAL(API, "command.memory.total"),
    COMMAND_MEMORY_FREE(API, "command.memory.free"),
    COMMAND_UPTIME_DESCRIPTION(API, "command.uptime.description"),
    COMMAND_JMX_DESCRIPTION(API, "command.jmx.description"),
    COMMAND_JMX_START_OPTION(API, "command.jmx.start_option"),
    COMMAND_JMX_STOP_OPTION(API, "command.jmx.stop_option"),
    COMMAND_JMX_STATUS_OPTION(API, "command.jmx.status_option"),
    COMMAND_JMX_PORT_OPTION(API, "command.jmx.port_option"),
    COMMAND_JMX_SYSTEM_PROPERTY_ENABLED(API, "command.jmx.system_property_enabled"),
    COMMAND_JMX_NO_ACTION(API, "command.jmx.no_action"),
    COMMAND_JMX_ALREADY_RUNNING(API, "command.jmx.already_running"),
    COMMAND_JMX_STARTED(API, "command.jmx.started"),
    COMMAND_JMX_STOPPED(API, "command.jmx.stopped"),
    COMMAND_JMX_NOT_STARTED(API, "command.jmx.not_started"),
    COMMAND_JMX_STATUS_SYSTEM_PROPERTY(API, "command.jmx.status_system_property"),
    COMMAND_JMX_STATUS_RUNNING(API, "command.jmx.status_running"),
    COMMAND_JMX_SYSTEM_PROPERTY(API, "command.jmx.system_property"),
    COMMAND_JMX_SYSTEM_PROPERTY_INVALID(API, "command.jmx.system_property_invalid"),
    COMMAND_JMX_PORT(API, "command.jmx.port"),
    COMMAND_JMX_SERVICE_URL(API, "command.jmx.service_url"),
    COMMAND_JMX_PORT_PROMPT(API, "command.jmx.port_prompt"),
    COMMAND_JMX_INVALID_PORT(API, "command.jmx.invalid_port");

    private final ApiMessages.Catalog catalog;
    private final String key;

    ApiMessageKey(ApiMessages.Catalog catalog, String key) {
        this.catalog = catalog;
        this.key = key;
    }

    ApiMessages.Catalog catalog() {
        return catalog;
    }

    /**
     * 返回资源键。
     *
     * @return 资源键。
     */
    public String key() {
        return key;
    }
}
