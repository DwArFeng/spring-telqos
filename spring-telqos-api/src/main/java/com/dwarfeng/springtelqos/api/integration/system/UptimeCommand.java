package com.dwarfeng.springtelqos.api.integration.system;

import com.dwarfeng.springtelqos.sdk.command.CliCommand;
import com.dwarfeng.springtelqos.stack.command.Context;
import com.dwarfeng.springtelqos.stack.exception.TelqosException;
import org.apache.commons.cli.CommandLine;

import java.lang.management.ManagementFactory;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * 系统运行时间命令。
 *
 * @author DwArFeng
 * @since 1.1.15
 */
public class UptimeCommand extends CliCommand {

    private static final String IDENTITY = "uptime";
    private static final String DESCRIPTION = "系统运行时间";
    private static final String CMD_LINE_SYNTAX = "uptime";

    private static final String TIME_PATTERN = "HH:mm:ss";
    private static final long MILLIS_PER_SECOND = 1000L;
    private static final long SECONDS_PER_MINUTE = 60L;
    private static final long SECONDS_PER_HOUR = 60L * SECONDS_PER_MINUTE;
    private static final long SECONDS_PER_DAY = 24L * SECONDS_PER_HOUR;
    private static final String OUTPUT_FORMAT = "%s up %d days, %02d:%02d";

    public UptimeCommand() {
        super(IDENTITY, DESCRIPTION, CMD_LINE_SYNTAX);
    }

    @SuppressWarnings({"ExtractMethodRecommender", "RedundantSuppression"})
    @Override
    protected void executeWithCmd(Context context, CommandLine cmd) throws TelqosException {
        try {
            // 获取当前时间并格式化为 HH:mm:ss。
            LocalTime currentTime = LocalTime.now();
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(TIME_PATTERN);
            String formattedTime = currentTime.format(timeFormatter);

            // 获取 JVM 运行时间（毫秒）。
            long uptimeMillis = ManagementFactory.getRuntimeMXBean().getUptime();

            // 计算天数、小时、分钟。
            long totalSeconds = uptimeMillis / MILLIS_PER_SECOND;
            long days = totalSeconds / SECONDS_PER_DAY;
            long remainingSeconds = totalSeconds % SECONDS_PER_DAY;
            long hours = remainingSeconds / SECONDS_PER_HOUR;
            long minutes = remainingSeconds % SECONDS_PER_HOUR / SECONDS_PER_MINUTE;

            // 格式化输出：HH:mm:ss up X days, HH:MM。
            String output = String.format(OUTPUT_FORMAT, formattedTime, days, hours, minutes);

            context.sendMessage(output);
        } catch (Exception e) {
            throw new TelqosException(e);
        }
    }
}

