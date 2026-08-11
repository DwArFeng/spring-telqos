package com.dwarfeng.springtelqos.base.sdk.i18n;

import java.util.Locale;
import java.util.Objects;

/**
 * 消息语言环境上下文。
 *
 * <p>
 * 该工具使用 JDK 25 正式提供的 {@link ScopedValue} 保存调用范围内的语言环境，避免进程级可变全局状态。
 *
 * <p>
 * 该类型属于 Spring Telqos 内部集成机制。
 *
 * @author DwArFeng
 * @since 3.0.0
 */
public final class MessageContext {

    private static final ScopedValue<Locale> LOCALE = ScopedValue.newInstance();

    /**
     * 返回当前调用范围的语言环境；未绑定时返回 JVM 默认显示语言环境。
     *
     * @return 当前语言环境。
     */
    public static Locale currentLocale() {
        return LOCALE.orElse(Locale.getDefault(Locale.Category.DISPLAY));
    }

    /**
     * 返回当前调用范围是否绑定了语言环境。
     *
     * @return 是否已绑定。
     */
    public static boolean isBound() {
        return LOCALE.isBound();
    }

    /**
     * 在指定语言环境下执行操作。
     *
     * @param locale   语言环境。
     * @param runnable 待执行操作。
     */
    public static void run(Locale locale, Runnable runnable) {
        Objects.requireNonNull(locale, "locale");
        Objects.requireNonNull(runnable, "runnable");
        ScopedValue.where(LOCALE, locale).run(runnable);
    }

    /**
     * 在指定语言环境下调用操作。
     *
     * @param locale    语言环境。
     * @param operation 待调用操作。
     * @param <T>       返回值类型。
     * @param <X>       异常类型。
     * @return 操作返回值。
     * @throws X 操作抛出的异常。
     */
    public static <T, X extends Throwable> T call(
            Locale locale, ScopedValue.CallableOp<? extends T, X> operation
    ) throws X {
        Objects.requireNonNull(locale, "locale");
        Objects.requireNonNull(operation, "operation");
        return ScopedValue.where(LOCALE, locale).call(operation);
    }

    private MessageContext() {
        throw new AssertionError("No instances");
    }
}
