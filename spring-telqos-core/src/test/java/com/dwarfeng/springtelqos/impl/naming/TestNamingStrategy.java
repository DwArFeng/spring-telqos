package com.dwarfeng.springtelqos.impl.naming;

import com.dwarfeng.springtelqos.stack.naming.NamingStrategy;
import com.dwarfeng.springtelqos.stack.naming.ToCommandIdentityInfo;
import org.jetbrains.annotations.NotNull;

/**
 * 测试指令命名策略。
 *
 * @author DwArFeng
 * @since 2.0.0
 */
public class TestNamingStrategy implements NamingStrategy {

    public static final NamingStrategy INSTANCE = new TestNamingStrategy();

    @Override
    public String toCommandIdentity(@NotNull ToCommandIdentityInfo info) {
        return "test:" + info.commandInfo().identify();
    }

    @Override
    public String toString() {
        return "TestNamingStrategy{}";
    }
}
