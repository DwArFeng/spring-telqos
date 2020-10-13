package com.dwarfeng.springtelqos.api.launcher;

import com.dwarfeng.springterminator.sdk.util.ApplicationUtil;

/**
 * 程序启动器。
 *
 * @author DwArFeng
 * @since 1.1.1.a
 */
public class TestLauncher {

    public static void main(String[] args) {
        ApplicationUtil.launch("classpath:spring/application-context*.xml");
    }
}
