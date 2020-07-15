package com.dwarfeng.springtelqos.impl.service;

import com.dwarfeng.springterminator.sdk.util.ApplicationUtil;
import org.junit.Test;

public class TelqosServiceImplTest {

    @Test
    public void test() {
        ApplicationUtil.launch("classpath:spring/application-context*.xml");
    }
}
