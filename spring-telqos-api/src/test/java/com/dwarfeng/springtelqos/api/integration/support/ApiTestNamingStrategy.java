package com.dwarfeng.springtelqos.api.integration.support;

import com.dwarfeng.springtelqos.stack.naming.NamingStrategy;
import com.dwarfeng.springtelqos.stack.naming.ToCommandIdentityInfo;
import org.jspecify.annotations.NonNull;

/**
 * API 模块测试指令命名策略。
 *
 * @author DwArFeng
 * @since 3.0.0
 */
public class ApiTestNamingStrategy implements NamingStrategy {

    public static final NamingStrategy INSTANCE = new ApiTestNamingStrategy();

    @Override
    public String toCommandIdentity(@NonNull ToCommandIdentityInfo info) {
        return "test:" + info.commandInfo().identify();
    }

    @Override
    public String toString() {
        return "ApiTestNamingStrategy{}";
    }
}
