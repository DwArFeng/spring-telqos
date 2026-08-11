module com.dwarfeng.springtelqos.api {

    requires com.dwarfeng.springtelqos.base;
    requires transitive com.dwarfeng.springtelqos.core;
    requires com.dwarfeng.dutil.basic;
    requires dubbo;
    requires java.management;
    requires java.rmi;
    requires org.apache.commons.cli;
    requires org.apache.commons.lang3;
    requires org.apache.logging.log4j.core;
    requires org.slf4j;

    exports com.dwarfeng.springtelqos.api.integration.dubbo;
    exports com.dwarfeng.springtelqos.api.integration.log4j2;
    exports com.dwarfeng.springtelqos.api.integration.system;

    opens com.dwarfeng.springtelqos.api.i18n to com.dwarfeng.springtelqos.base;
}
