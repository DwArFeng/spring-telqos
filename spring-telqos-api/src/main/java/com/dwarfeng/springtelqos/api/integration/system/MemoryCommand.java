package com.dwarfeng.springtelqos.api.integration.system;

import com.dwarfeng.dutil.basic.num.NumberUtil;
import com.dwarfeng.dutil.basic.num.unit.DataSize;
import com.dwarfeng.springtelqos.sdk.command.CliCommand;
import com.dwarfeng.springtelqos.sdk.util.CliCommandUtil;
import com.dwarfeng.springtelqos.stack.command.CommandExecutor;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.lang3.Strings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 内存监视命令。
 *
 * @author DwArFeng
 * @since 1.1.1
 */
public class MemoryCommand extends CliCommand {

    @SuppressWarnings({"SpellCheckingInspection", "GrazieInspectionRunner", "RedundantSuppression"})
    public static final String IDENTITY = "memory";

    // region 指令选项

    private static final String COMMAND_SUB_OPTION_UNIT = "u";

    // endregion

    private static final List<DataSize> DATA_SIZES = Collections.unmodifiableList(Arrays.asList(
            DataSize.EIB, DataSize.PIB, DataSize.TIB, DataSize.GIB, DataSize.MIB, DataSize.KIB, DataSize.BYTE
    ));
    private static final List<String> DATA_SIZE_LABELS = Collections.unmodifiableList(Arrays.asList(
            "EiB", "PiB", "TiB", "GiB", "MiB", "KiB", "B"
    ));

    public MemoryCommand() {
        super(IDENTITY);
    }

    @Override
    protected DescriptionProvider provideDescriptionProvider() {
        return ctx -> "内存监视";
    }

    @Override
    protected CliSyntaxProvider provideCliSyntaxProvider() {
        return context -> context.getRuntimeIdentity() + " [" +
                CliCommandUtil.concatOptionPrefix(COMMAND_SUB_OPTION_UNIT) + " unit]";
    }

    @Override
    protected List<Option> provideOptions() {
        List<Option> list = new ArrayList<>();
        list.add(Option.builder(COMMAND_SUB_OPTION_UNIT).optionalArg(true).hasArg(true).desc("显示单位").build());
        return list;
    }

    @Override
    protected void executeWithCmd(CommandExecutor.Context context, CommandLine cmd) throws Exception {
        Runtime runtime = Runtime.getRuntime();
        long freeMemory = runtime.freeMemory();
        long maxMemory = runtime.maxMemory();
        long totalMemory = runtime.totalMemory();
        StringBuilder stringBuilder = new StringBuilder();
        if (cmd.hasOption(COMMAND_SUB_OPTION_UNIT)) {
            String unit = cmd.getOptionValue(COMMAND_SUB_OPTION_UNIT);
            int index = -1;
            for (int i = 0; i < DATA_SIZE_LABELS.size(); i++) {
                String testUnit = DATA_SIZE_LABELS.get(i);
                if (Strings.CI.equals(unit, testUnit)) {
                    index = i;
                    break;
                }
            }
            if (index < 0) {
                stringBuilder.append("-u 后接单位不合法，可用的单位如下:").append(System.lineSeparator());
                for (int i = 0; i < DATA_SIZE_LABELS.size(); i++) {
                    if (i != 0) {
                        stringBuilder.append(", ");
                    }
                    stringBuilder.append(DATA_SIZE_LABELS.get(i));
                }
            } else {
                renderUnitValue(stringBuilder, "JVM 最大内存:", maxMemory, index);
                stringBuilder.append(System.lineSeparator());
                renderUnitValue(stringBuilder, "JVM 分配内存:", totalMemory, index);
                stringBuilder.append(System.lineSeparator());
                renderUnitValue(stringBuilder, "JVM 可用内存:", freeMemory, index);
            }
        } else {
            renderUnitValue(stringBuilder, "JVM 最大内存:", maxMemory, humanReadable(maxMemory));
            stringBuilder.append(System.lineSeparator());
            renderUnitValue(stringBuilder, "JVM 分配内存:", totalMemory, humanReadable(totalMemory));
            stringBuilder.append(System.lineSeparator());
            renderUnitValue(stringBuilder, "JVM 可用内存:", freeMemory, humanReadable(freeMemory));
        }
        context.sendMessage(stringBuilder.toString());
    }

    private void renderUnitValue(StringBuilder stringBuilder, String prefix, long byteSize, int index) {
        String label = DATA_SIZE_LABELS.get(index);
        if (index == DATA_SIZE_LABELS.size() - 1) {
            stringBuilder.append(String.format("%s %d%s", prefix, byteSize, label));
        } else {
            double value = NumberUtil.unitTrans(byteSize, DataSize.BYTE, DATA_SIZES.get(index)).doubleValue();
            stringBuilder.append(String.format("%s %.2f%s", prefix, value, label));
        }
    }

    private int humanReadable(long byteSize) {
        if (byteSize == 0) {
            return DATA_SIZE_LABELS.size() - 1;
        }
        int index = 0;
        double value;
        do {
            value = NumberUtil.unitTrans(byteSize, DataSize.BYTE, DATA_SIZES.get(index++)).doubleValue();
        } while (value < 1);
        return index - 1;
    }
}
