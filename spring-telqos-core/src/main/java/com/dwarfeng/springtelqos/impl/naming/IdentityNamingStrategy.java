package com.dwarfeng.springtelqos.impl.naming;

import com.dwarfeng.springtelqos.sdk.naming.AbstractNamingStrategy;
import com.dwarfeng.springtelqos.stack.naming.NamingStrategy;
import com.dwarfeng.springtelqos.stack.naming.ToCommandIdentityInfo;
import org.jetbrains.annotations.NotNull;

/**
 * 本征指令命名策略。
 *
 * @author DwArFeng
 * @since 2.0.0
 */
public class IdentityNamingStrategy extends AbstractNamingStrategy {

    public static final NamingStrategy INSTANCE = new IdentityNamingStrategy();

    @Override
    protected String doToCommandIdentity(@NotNull ToCommandIdentityInfo info) {
        return info.commandInfo().identify();
    }

    @Override
    public String toString() {
        return "IdentityNamingStrategy{}";
    }
}
