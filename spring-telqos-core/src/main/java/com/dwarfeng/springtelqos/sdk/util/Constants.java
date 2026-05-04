package com.dwarfeng.springtelqos.sdk.util;

/**
 * 常量。
 *
 * @author DwArFeng
 * @since 1.0.0
 */
public final class Constants {

    public static final char MULTI_LINE_COMMAND_INDICATOR = '\\';

    /**
     * 指令标识符格式。
     *
     * <p>
     * 规则如下：
     * <ul>
     *     <li>首字符必须为英文字母或下划线。</li>
     *     <li>后续字符允许字母、数字、下划线。</li>
     *     <li>允许使用分隔符 `:`、`.`、`-` 进行分段，且分隔符后必须跟随至少一个合法字符。</li>
     * </ul>
     *
     * <p>
     * 合法示例：
     * <ul>
     *     <li>`lc`</li>
     *     <li>`ops:status`</li>
     *     <li>`module.user-list`</li>
     *     <li>`api_v2:reload`</li>
     * </ul>
     * 非法示例：
     * <ul>
     *     <li>空字符串</li>
     *     <li>`1abc`</li>
     *     <li>`abc..def`</li>
     *     <li>`abc*def`</li>
     * </ul>
     *
     * @since 2.0.0
     */
    public static final String COMMAND_IDENTITY_FORMAT = "^[a-zA-Z_][0-9a-zA-Z_]*(?:[:.-][0-9a-zA-Z_]+)*$";

    /**
     * @since 2.0.0
     */
    @SuppressWarnings({"SpellCheckingInspection", "GrazieInspectionRunner", "RedundantSuppression"})
    public static final String COMMAND_IDENTITY_LIST_COMMAND = "lc";

    /**
     * @since 2.0.0
     */
    @SuppressWarnings({"SpellCheckingInspection", "GrazieInspectionRunner", "RedundantSuppression"})
    public static final String COMMAND_IDENTITY_MANUAL = "man";

    /**
     * @since 2.0.0
     */
    @SuppressWarnings({"SpellCheckingInspection", "GrazieInspectionRunner", "RedundantSuppression"})
    public static final String COMMAND_IDENTITY_QUIT = "quit";

    /**
     * @since 2.0.0
     */
    public static final String XSD_DEFAULT_TELQOS_CONFIG_NAME = "telqosConfig";

    /**
     * @since 2.0.0
     */
    public static final String XSD_DEFAULT_TELQOS_HANDLER_NAME = "telqosHandler";

    /**
     * @since 2.0.0
     */
    public static final String XSD_DEFAULT_THREAD_POOL_TASK_EXECUTOR_NAME = "executor";

    /**
     * @since 2.0.0
     */
    public static final String XSD_DEFAULT_TELQOS_QOS_SERVICE_NAME = "telqosService";

    /**
     * @since 2.0.0
     */
    public static final String XSD_DEFAULT_SERVICE_EXCEPTION_MAPPER_NAME = "mapServiceExceptionMapper";

    /**
     * @since 2.0.0
     */
    public static final String XSD_DEFAULT_TELQOS_QOS_SERVICE_AUTO_START_VALUE = "true";

    private Constants() {
        throw new IllegalStateException("禁止实例化");
    }
}
