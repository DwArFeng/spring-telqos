package com.dwarfeng.springtelqos.sdk.util;

/**
 * 常量。
 *
 * @author DwArFeng
 * @since 1.0.0
 */
public final class Constants {

    public static final char MULTI_LINE_COMMAND_INDICATOR = '\\';
    public static final String COMMAND_IDENTITY_FORMAT = "^[a-zA-Z_][0-9a-zA-Z_]*$";

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

    private Constants() {
        throw new IllegalStateException("禁止实例化");
    }
}
