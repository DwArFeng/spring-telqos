package com.dwarfeng.springtelqos.core.internal.i18n;

import static com.dwarfeng.springtelqos.core.internal.i18n.CoreMessages.Catalog.IMPL;
import static com.dwarfeng.springtelqos.core.internal.i18n.CoreMessages.Catalog.SDK;

/**
 * Core 模块消息键。
 *
 * @author DwArFeng
 * @since 3.0.0
 */
public enum CoreMessageKey {

    SERVICE_EXCEPTION_TELQOS_FAILED(SDK, "service_exception.telqos_failed"),
    SERVICE_EXCEPTION_COMMAND_FAILED(SDK, "service_exception.command_failed"),
    SERVICE_EXCEPTION_COMMAND_DESCRIPTOR_FAILED(SDK, "service_exception.command_descriptor_failed"),
    SERVICE_EXCEPTION_COMMAND_EXECUTOR_FAILED(SDK, "service_exception.command_executor_failed"),
    SERVICE_EXCEPTION_NAMING_STRATEGY_FAILED(SDK, "service_exception.naming_strategy_failed"),
    COMMAND_MANUAL_UNAVAILABLE(SDK, "command.manual.unavailable"),
    COMMAND_OPTION_INVALID(SDK, "command.option.invalid"),
    CLI_OPTION_MISMATCH(SDK, "cli.option_mismatch"),
    CLI_CROP_QUIT_PROMPT(SDK, "cli.crop.quit_prompt"),
    CLI_CROP_ALL_PROMPT(SDK, "cli.crop.all_prompt"),
    CLI_CROP_RANGE_PROMPT(SDK, "cli.crop.range_prompt"),
    CLI_INPUT_FORMAT_INVALID(SDK, "cli.input_format_invalid"),
    CLI_INPUT_RANGE_INVALID(SDK, "cli.input_range_invalid"),
    COMMAND_LIST_DESCRIPTION(IMPL, "command.list.description"),
    COMMAND_LIST_PREFIX_OPTION(IMPL, "command.list.prefix_option"),
    COMMAND_LIST_TOTAL(IMPL, "command.list.total"),
    COMMAND_MANUAL_DESCRIPTION(IMPL, "command.manual.description"),
    COMMAND_MANUAL_NOT_FOUND(IMPL, "command.manual.not_found"),
    COMMAND_QUIT_DESCRIPTION(IMPL, "command.quit.description"),
    HANDLER_SERVER_DISCONNECTED(IMPL, "handler.server_disconnected"),
    HANDLER_GOODBYE(IMPL, "handler.goodbye"),
    HANDLER_INVALID_COMMAND(IMPL, "handler.invalid_command"),
    HANDLER_UNKNOWN_COMMAND(IMPL, "handler.unknown_command"),
    HANDLER_LIST_COMMAND_HINT(IMPL, "handler.list_command_hint"),
    HANDLER_SYSTEM_BUSY(IMPL, "handler.system_busy"),
    HANDLER_INVALID_IDENTITY(IMPL, "handler.invalid_identity"),
    HANDLER_ACCESS_DENIED(IMPL, "handler.access_denied"),
    HANDLER_WELCOME(IMPL, "handler.welcome"),
    HANDLER_DUPLICATE_LOGIN(IMPL, "handler.duplicate_login"),
    HANDLER_UNEXPECTED_EXCEPTION(IMPL, "handler.unexpected_exception"),
    HANDLER_OK(IMPL, "handler.ok"),
    HANDLER_CONNECTION_INTERRUPTED(IMPL, "handler.connection_interrupted");

    private final CoreMessages.Catalog catalog;
    private final String key;

    CoreMessageKey(CoreMessages.Catalog catalog, String key) {
        this.catalog = catalog;
        this.key = key;
    }

    CoreMessages.Catalog catalog() {
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
