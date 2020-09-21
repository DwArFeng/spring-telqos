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
    private Object[] params;
    private boolean validFlag;
    private String[] invalidDescriptions;

    public CommandStruct() {
    }

    public CommandStruct(String identity, Object[] params, boolean validFlag, String[] invalidDescriptions) {
        this.identity = identity;
        this.params = params;
        this.validFlag = validFlag;
        this.invalidDescriptions = invalidDescriptions;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
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
                ", params=" + Arrays.toString(params) +
                ", validFlag=" + validFlag +
                ", invalidDescriptions=" + Arrays.toString(invalidDescriptions) +
                '}';
    }
}
