package com.dwarfeng.springtelqos.impl.handler;

import java.util.Arrays;

/**
 * 命令行解析结果。
 *
 * @author DwArFeng
 * @since 1.0.0
 */
// 该类被设计为符合 JavaBean 规范，因此部分方法即使未被使用，也需要提供，故忽略未使用警告。
@SuppressWarnings("unused")
class CommandLineParseResult {

    private String identity;
    private String option;
    private boolean validFlag;
    private String[] invalidDescriptions;

    public CommandLineParseResult() {
    }

    public CommandLineParseResult(String identity, String option, boolean validFlag, String[] invalidDescriptions) {
        this.identity = identity;
        this.option = option;
        this.validFlag = validFlag;
        this.invalidDescriptions = invalidDescriptions;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public boolean isValidFlag() {
        return validFlag;
    }

    public void setValidFlag(boolean validFlag) {
        this.validFlag = validFlag;
    }

    public String[] getInvalidDescriptions() {
        return invalidDescriptions;
    }

    public void setInvalidDescriptions(String[] invalidDescriptions) {
        this.invalidDescriptions = invalidDescriptions;
    }

    @Override
    public String toString() {
        return "CommandLineParseResult{" +
                "identity='" + identity + '\'' +
                ", option='" + option + '\'' +
                ", validFlag=" + validFlag +
                ", invalidDescriptions=" + Arrays.toString(invalidDescriptions) +
                '}';
    }
}
