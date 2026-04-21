/**
 * 内置的指令实现。
 *
 * <p>
 * 该包下的类实现了项目的内置的指令，在这个包下的所有指令都会在 TelqosHandlerImpl 启动时使用反射被自动注册。
 *
 * <p>
 * 本包开发规约：
 * <ol>
 *     <li>本包下的指令类必须保证标识互相不重复，TelqosHandlerImpl 启动时将不会检查内置指令的标识是否重复。</li>
 *     <li>本包下的指令类必须包含 <code>INSTANCE</code> 静态常量，且该常量必须是类的唯一实例。</li>
 *     <li>本包下的指令类必须是顶级类，内部类或匿名类不能实现指令接口。</li>
 * </ol>
 *
 * @since 1.2.0
 */
package com.dwarfeng.springtelqos.impl.command.builtin;
