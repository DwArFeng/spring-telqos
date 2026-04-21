package com.dwarfeng.springtelqos.sdk.util;

import com.dwarfeng.springtelqos.stack.command.CommandExecutor;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.StringJoiner;

/**
 * CLI 指令工具类。
 *
 * @author DwArFeng
 * @since 2.0.0
 */
public final class CliCommandUtil {

    // region 内部常量

    // region 数据分片

    private static final String CROP_DATA_CROP_ALL_COMMAND = "all";
    private static final String CROP_DATA_CROP_RANGE_ARG_NAME_BEGIN = "begin";
    private static final String CROP_DATA_CROP_RANGE_ARG_NAME_END = "end";
    private static final String CROP_DATA_CROP_RANGE_SEPARATOR = "-";
    private static final String CROP_DATA_QUIT_COMMAND = "q";

    // endregion

    // endregion

    // region 句法格式

    /**
     * 拼接选项的前缀，用于生成选项说明书。
     *
     * <p>
     * <ul>
     *     <li><code>online</code> -> <code>--online</code></li>
     *     <li><code>dump-file</code> -> <code>--dump-file</code></li>
     * </ul>
     *
     * @param commandOption 指定的选项。
     * @return 拼接前缀之后的选项。
     */
    public static String concatOptionPrefix(@Nonnull String commandOption) {
        if (commandOption.contains("-")) {
            return "--" + commandOption;
        }
        return "-" + commandOption;
    }

    /**
     * 拼接 CLI 指令的语法说明。
     *
     * @param patterns 指定的 CLI 指令语法模式。
     * @return 拼接之后的 CLI 指令语法说明。
     */
    public static String cliSyntax(@Nonnull String... patterns) {
        StringJoiner sj = new StringJoiner(System.lineSeparator());
        for (String pattern : patterns) {
            sj.add(pattern);
        }
        return sj.toString();
    }

    // endregion

    // region 指令解析

    /**
     * 分析 CLI 指令的选项。
     *
     * <p>
     * 该方法会分析指定的 CLI 指令选项，并返回一个二元组，其中第一个元素为 CLI 指令选项中存在的选项（如果存在多个，则返回最后一个），
     * 第二个元素为 CLI 指令选项中存在的选项的数量。
     *
     * @param commandLine    CLI 指令。
     * @param commandOptions 需要分析的 CLI 指令选项。
     * @return 分析结果。
     */
    public static Pair<String, Integer> analyseCommand(
            @Nonnull CommandLine commandLine, @Nonnull String... commandOptions
    ) {
        int i = 0;
        String subCmd = null;
        for (String commandOption : commandOptions) {
            if (commandLine.hasOption(commandOption)) {
                i++;
                subCmd = commandOption;
            }
        }
        return Pair.of(subCmd, i);
    }

    /**
     * 生成选项不匹配的提示信息。
     *
     * @param patterns 选项模式。
     * @return 选项不匹配的提示信息。
     */
    public static String optionMismatchMessage(@Nonnull String... patterns) {
        StringJoiner sj = new StringJoiner(", ", "下列选项必须且只能含有一个: ", "");
        for (String pattern : patterns) {
            sj.add(concatOptionPrefix(pattern));
        }
        return sj.toString();
    }

    // endregion

    // region 数据分片

    /**
     * 分片数据输出。
     *
     * @param context       上下文。
     * @param originData    原始数据列表。
     * @param bannerMessage 横幅消息。
     * @param <T>           数据类型。
     * @return 分片结果。
     * @throws Exception 处理过程中发生的异常。
     */
    public static <T> CropResult cropData(
            @Nonnull CommandExecutor.Context context, @Nonnull List<T> originData, @Nonnull String bannerMessage
    ) throws Exception {
        final CropDataQuitPromptProvider quitPromptProvider = command -> "输入 " + command + " 退出";
        return cropData(context, originData, bannerMessage, quitPromptProvider);
    }

    /**
     * 分片数据输出。
     *
     * @param context            上下文。
     * @param originData         原始数据列表。
     * @param bannerMessage      横幅消息。
     * @param quitPromptProvider 选择退出的提示提供器。
     * @param <T>                数据类型。
     * @return 分片结果。
     * @throws Exception 处理过程中发生的异常。
     */
    public static <T> CropResult cropData(
            @Nonnull CommandExecutor.Context context, @Nonnull List<T> originData, @Nonnull String bannerMessage,
            @Nonnull CropDataQuitPromptProvider quitPromptProvider
    ) throws Exception {
        final CropDataCropAllPromptProvider cropAllPromptProvider = command -> "输入 " + command + " 查看所有数据";
        final CropDataCropRangePromptProvider cropRangePromptProvider = (argNameBegin, argNameEnd, separator) ->
                "输入 " + argNameBegin + separator + argNameEnd + " 查看指定范围的数据(开始于 0)";
        return cropData(
                context, originData, bannerMessage, cropAllPromptProvider, cropRangePromptProvider, quitPromptProvider
        );
    }

    /**
     * 分片数据输出。
     *
     * @param context                 上下文。
     * @param originData              原始数据列表。
     * @param bannerMessage           横幅消息。
     * @param cropAllPromptProvider   选择分片全部数据的提示提供器。
     * @param cropRangePromptProvider 选择分片范围数据的提示提供器。
     * @param quitPromptProvider      选择退出的提示提供器。
     * @param <T>                     数据类型。
     * @return 分片结果。
     * @throws Exception 处理过程中发生的异常。
     */
    public static <T> CropResult cropData(
            @Nonnull CommandExecutor.Context context, @Nonnull List<T> originData, @Nonnull String bannerMessage,
            @Nonnull CropDataCropAllPromptProvider cropAllPromptProvider,
            @Nonnull CropDataCropRangePromptProvider cropRangePromptProvider,
            @Nonnull CropDataQuitPromptProvider quitPromptProvider
    ) throws Exception {
        int beginIndex;
        int endIndex;

        while (true) {
            context.sendMessage(bannerMessage);
            context.sendMessage("");
            context.sendMessage(cropAllPromptProvider.provide(CROP_DATA_CROP_ALL_COMMAND));
            context.sendMessage(cropRangePromptProvider.provide(
                    CROP_DATA_CROP_RANGE_ARG_NAME_BEGIN, CROP_DATA_CROP_RANGE_ARG_NAME_END,
                    CROP_DATA_CROP_RANGE_SEPARATOR
            ));
            context.sendMessage(quitPromptProvider.provide(CROP_DATA_QUIT_COMMAND));
            context.sendMessage("");

            String message = context.receiveMessage();

            if (Strings.CI.equals(message, CROP_DATA_QUIT_COMMAND)) {
                return new CropResult(-1, -1, true);
            } else if (Strings.CI.equals(message, CROP_DATA_CROP_ALL_COMMAND)) {
                beginIndex = 0;
                endIndex = originData.size();
            } else {
                String[] split = StringUtils.split(message, CROP_DATA_CROP_RANGE_SEPARATOR);
                if (split.length != 2) {
                    context.sendMessage("输入格式错误");
                    context.sendMessage("");
                    continue;
                }
                try {
                    beginIndex = Integer.parseInt(split[0]);
                    endIndex = Integer.parseInt(split[1]);
                } catch (NumberFormatException e) {
                    context.sendMessage("输入格式错误");
                    context.sendMessage("");
                    continue;
                }
                if (beginIndex < 0 || endIndex > originData.size() || beginIndex >= endIndex) {
                    String errorMessage = "输入范围错误，" + CROP_DATA_CROP_RANGE_ARG_NAME_BEGIN + " 和 " +
                            CROP_DATA_CROP_RANGE_ARG_NAME_END + " 均应介于 [0, " + originData.size() + "] 之间，且 " +
                            CROP_DATA_CROP_RANGE_ARG_NAME_BEGIN + " 应小于 " + CROP_DATA_CROP_RANGE_ARG_NAME_END;
                    context.sendMessage(errorMessage);
                    context.sendMessage("");
                    continue;
                }
            }
            break;
        }

        return new CropResult(beginIndex, endIndex, false);
    }

    // endregion

    private CliCommandUtil() {
        throw new IllegalStateException("禁止实例化");
    }

    // region 嵌套类/接口

    // region 数据分片

    /**
     * 选择分片全部数据的提示提供器。
     *
     * @author DwArFeng
     * @since 2.0.0
     */
    public interface CropDataCropAllPromptProvider {

        String provide(String command);
    }

    /**
     * 选择分片范围数据的提示提供器。
     *
     * @author DwArFeng
     * @since 2.0.0
     */
    public interface CropDataCropRangePromptProvider {

        String provide(String argNameBegin, String argNameEnd, String separator);
    }

    /**
     * 选择退出的提示提供器。
     *
     * @author DwArFeng
     * @since 2.0.0
     */
    public interface CropDataQuitPromptProvider {

        String provide(String command);
    }

    /**
     * 分片数据输出结果。
     *
     * @author DwArFeng
     * @since 2.0.0
     */
    public static final class CropResult {

        private final int beginIndex;
        private final int endIndex;
        private final boolean exitFlag;

        public CropResult(int beginIndex, int endIndex, boolean exitFlag) {
            this.beginIndex = beginIndex;
            this.endIndex = endIndex;
            this.exitFlag = exitFlag;
        }

        public int getBeginIndex() {
            return beginIndex;
        }

        public int getEndIndex() {
            return endIndex;
        }

        public boolean isExitFlag() {
            return exitFlag;
        }

        @Override
        public String toString() {
            return "CropResult{" +
                    "beginIndex=" + beginIndex +
                    ", endIndex=" + endIndex +
                    ", exitFlag=" + exitFlag +
                    '}';
        }
    }

    // endregion

    // endregion
}
