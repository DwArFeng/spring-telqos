package com.dwarfeng.springtelqos.impl.naming;

import com.dwarfeng.springtelqos.sdk.naming.AbstractNamingStrategy;
import com.dwarfeng.springtelqos.stack.naming.NamingStrategy;
import com.dwarfeng.springtelqos.stack.naming.ToCommandIdentityInfo;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * 本征指令命名策略。
 *
 * @author DwArFeng
 * @since 2.0.0
 */
public class IdentityNamingStrategy extends AbstractNamingStrategy {

    public static final NamingStrategy INSTANCE = new IdentityNamingStrategy();

    @Override
    protected String doToCommandIdentity(@NonNull ToCommandIdentityInfo info) {
        return info.getCommandInfo().getIdentify();
    }

    @Override
    public String toString() {
        return "IdentityNamingStrategy{}";
    }
}
