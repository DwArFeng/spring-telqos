package com.dwarfeng.springtelqos.stack.naming;

import com.dwarfeng.springtelqos.stack.exception.NamingStrategyException;

import javax.annotation.Nonnull;

/**
 * 命名策略。
 *
 * <p>
 * 命名策略用于在项目中提供命名的策略支持，旨在使用一种规则对特点的命名进行映射。
 *
 * <p>
 * 如果没有特殊的命名规则需求，可以直接使用本征命名策略。
 *
 * @author DwArFeng
 * @since 2.0.0
 */
public interface NamingStrategy {

    /**
     * 转换指令标识。
     *
     * @param info 转换指令标识信息。
     * @return 基于指令标识策略最终转换得到的指令标识。
     * @throws NamingStrategyException 命名策略异常。
     */
    String toCommandIdentity(@Nonnull ToCommandIdentityInfo info) throws NamingStrategyException;
}
