package com.dwarfeng.springtelqos.sdk.naming;

import com.dwarfeng.springtelqos.stack.exception.NamingStrategyException;
import com.dwarfeng.springtelqos.stack.naming.NamingStrategy;
import com.dwarfeng.springtelqos.stack.naming.ToCommandIdentityInfo;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * 命名策略的抽象实现。
 *
 * @author DwArFeng
 * @since 2.0.0
 */
public abstract class AbstractNamingStrategy implements NamingStrategy {

    @Override
    public String toCommandIdentity(@NonNull ToCommandIdentityInfo info) throws NamingStrategyException {
        try {
            return doToCommandIdentity(info);
        } catch (NamingStrategyException e) {
            throw e;
        } catch (Exception e) {
            throw new NamingStrategyException(e);
        }
    }

    protected abstract String doToCommandIdentity(@NonNull ToCommandIdentityInfo info) throws Exception;

    @Override
    public String toString() {
        return "AbstractNamingStrategy{}";
    }
}
