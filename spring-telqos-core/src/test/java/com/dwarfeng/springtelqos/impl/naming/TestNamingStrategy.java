package com.dwarfeng.springtelqos.impl.naming;

import com.dwarfeng.springtelqos.stack.naming.NamingStrategy;
import com.dwarfeng.springtelqos.stack.naming.ToCommandIdentityInfo;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * 测试指令命名策略。
 *
 * @author DwArFeng
 * @since 2.0.0
 */
public class TestNamingStrategy implements NamingStrategy {

    public static final NamingStrategy INSTANCE = new TestNamingStrategy();

    @Override
    public String toCommandIdentity(@NonNull ToCommandIdentityInfo info) {
        return "test:" + info.getCommandInfo().getIdentify();
    }

    @Override
    public String toString() {
        return "TestNamingStrategy{}";
    }
}
