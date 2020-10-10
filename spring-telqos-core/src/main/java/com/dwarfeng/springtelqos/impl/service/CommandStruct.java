package com.dwarfeng.springtelqos.impl.service;

import java.util.Arrays;

/**
 * 命令结构。
 *
 * @author DwArFeng
 * @since 1.0.0
 */
class CommandStruct {

    private String identity;
    private String option;
    private boolean validFlag;
    private String[] invalidDescriptions;

    public CommandStruct() {
    }

    public CommandStruct(String identity, String option, boolean validFlag, String[] invalidDescriptions) {
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
        return "CommandStruct{" +
                "identity='" + identity + '\'' +
                ", option='" + option + '\'' +
                ", validFlag=" + validFlag +
                ", invalidDescriptions=" + Arrays.toString(invalidDescriptions) +
                '}';
    }
}
