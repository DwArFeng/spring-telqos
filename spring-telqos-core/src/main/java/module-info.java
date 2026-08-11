module com.dwarfeng.springtelqos.core {

    requires com.dwarfeng.springtelqos.base;
    requires transitive com.dwarfeng.dutil.basic;
    requires com.dwarfeng.subgrade.aop;
    requires transitive com.dwarfeng.subgrade.basic;
    requires transitive com.dwarfeng.subgrade.lifecycle;
    requires io.netty.transport;
    requires io.netty.codec;
    requires jakarta.annotation;
    requires transitive org.apache.commons.cli;
    requires transitive org.apache.commons.lang3;
    requires org.slf4j;
    requires spring.beans;
    requires spring.context;
    requires spring.core;
    requires spring.expression;
    requires static org.jetbrains.annotations;
    requires transitive java.xml;

    exports com.dwarfeng.springtelqos.impl.command.builtin;
    exports com.dwarfeng.springtelqos.impl.handler;
    exports com.dwarfeng.springtelqos.impl.naming;
    exports com.dwarfeng.springtelqos.impl.service;
    exports com.dwarfeng.springtelqos.node.configuration;
    exports com.dwarfeng.springtelqos.sdk.command;
    exports com.dwarfeng.springtelqos.sdk.configuration;
    exports com.dwarfeng.springtelqos.sdk.exception;
    exports com.dwarfeng.springtelqos.sdk.naming;
    exports com.dwarfeng.springtelqos.sdk.util;
    exports com.dwarfeng.springtelqos.stack.command;
    exports com.dwarfeng.springtelqos.stack.exception;
    exports com.dwarfeng.springtelqos.stack.handler;
    exports com.dwarfeng.springtelqos.stack.naming;
    exports com.dwarfeng.springtelqos.stack.service;
    exports com.dwarfeng.springtelqos.stack.struct;
    exports com.dwarfeng.springtelqos.stack.util;

    opens com.dwarfeng.springtelqos.sdk.i18n to com.dwarfeng.springtelqos.base;
    opens com.dwarfeng.springtelqos.impl.i18n to com.dwarfeng.springtelqos.base;
}
