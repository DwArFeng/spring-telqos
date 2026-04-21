package com.dwarfeng.springtelqos.stack.util;

import com.dwarfeng.springtelqos.stack.command.Command;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Telqos 配置工具类。
 *
 * @author DwArFeng
 * @since 2.0.0
 */
public final class TelqosConfigUtil {

    /**
     * 检查指定的端口是否合法。
     *
     * <p>
     * 合法范围为 <code>[0, 65535]</code>，与 Java 套接字端口范围一致；Telnet 服务绑定行为由运行时决定，此处仅做数值边界校验。
     *
     * @param port 指定的端口。
     * @throws IllegalArgumentException 若端口超出范围。
     */
    public static void checkPort(int port) {
        if (port < 0 || port > 65535) {
            throw new IllegalArgumentException("端口号必须在 0 - 65535 之间");
        }
    }

    /**
     * 检查指定的白名单正则表达式是否合法。
     *
     * <p>
     * 允许为空字符串，表示不启用白名单过滤；非空时须为 {@link Pattern} 可编译的正则表达式。
     *
     * @param whitelistRegex 指定的白名单正则表达式。
     * @throws NullPointerException     若为 <code>null</code>。
     * @throws IllegalArgumentException 若非空但正则语法非法。
     */
    public static void checkWhitelistRegex(String whitelistRegex) {
        checkNullableRegex("白名单正则表达式", whitelistRegex);
    }

    /**
     * 检查指定的黑名单正则表达式是否合法。
     *
     * <p>
     * 允许为空字符串，表示不启用黑名单过滤；非空时须为 {@link Pattern} 可编译的正则表达式。
     *
     * @param blacklistRegex 指定的黑名单正则表达式。
     * @throws NullPointerException     若为 <code>null</code>。
     * @throws IllegalArgumentException 若非空但正则语法非法。
     */
    public static void checkBlacklistRegex(String blacklistRegex) {
        checkNullableRegex("黑名单正则表达式", blacklistRegex);
    }

    /**
     * 校验“可为空串的正则”字段。
     *
     * <p>
     * <code>null</code> 非法；空串跳过；非空则尝试 {@link Pattern#compile(String)}。
     *
     * @param label 异常信息中的字段说明。
     * @param regex 正则字符串。
     * @throws NullPointerException     若 <code>regex == null</code>。
     * @throws IllegalArgumentException 若非空且编译失败。
     */
    private static void checkNullableRegex(String label, String regex) {
        if (Objects.isNull(regex)) {
            throw new NullPointerException(label + "不能为 null");
        }
        if (regex.isEmpty()) {
            return;
        }
        try {
            Pattern.compile(regex);
        } catch (PatternSyntaxException e) {
            throw new IllegalArgumentException(label + "不是合法的正则表达式", e);
        }
    }

    /**
     * 检查指定的字符集名称是否合法。
     *
     * <p>
     * 不允许为 <code>null</code> 或空字符串；须能被 {@link java.nio.charset.Charset#forName(String)} 识别。
     *
     * @param charset 指定的字符集名称。
     * @throws NullPointerException     若为 <code>null</code>。
     * @throws IllegalArgumentException 若为空串或 JVM 不支持该字符集名。
     */
    public static void checkCharset(String charset) {
        if (Objects.isNull(charset)) {
            throw new NullPointerException("字符集不能为 null");
        }
        if (charset.isEmpty()) {
            throw new IllegalArgumentException("字符集不能为空字符串");
        }
        try {
            java.nio.charset.Charset.forName(charset);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("字符集不合法", e);
        }
    }

    /**
     * 检查指定的 Banner 资源路径是否合法。
     *
     * <p>
     * 不允许为 <code>null</code>；允许为空字符串，用于按文档描述取消 Banner 输出（具体是否读取资源由 Handler 实现决定）。
     *
     * @param bannerUrl 指定的 Banner 资源路径。
     * @throws NullPointerException 若为 <code>null</code>。
     */
    public static void checkBannerUrl(String bannerUrl) {
        if (Objects.isNull(bannerUrl)) {
            throw new NullPointerException("Banner 资源路径不能为 null");
        }
    }

    /**
     * 检查指定的命令集合是否合法。
     *
     * <p>
     * 约束包括：
     * <ul>
     *     <li>集合本身不能为 <code>null</code>。</li>
     *     <li>元素不能为 <code>null</code>。</li>
     *     <li>每个 {@link Command#getIdentity()} 不能为空。</li>
     * </ul>
     *
     * @param commands 指定的命令集合。
     * @throws NullPointerException     若集合为 <code>null</code>。
     * @throws IllegalArgumentException 若元素、标识符非法或重复。
     */
    public static void checkCommands(Collection<Command> commands) {
        if (Objects.isNull(commands)) {
            throw new NullPointerException("命令集合不能为 null");
        }
        for (Command command : commands) {
            if (Objects.isNull(command)) {
                throw new IllegalArgumentException("命令集合中不能包含 null 元素");
            }
            String identity = command.getIdentity();
            if (StringUtils.isBlank(identity)) {
                throw new IllegalArgumentException("命令的 identity 不能为空");
            }
        }
    }

    private TelqosConfigUtil() {
        throw new IllegalStateException("禁止实例化");
    }
}
