package com.dwarfeng.springtelqos.api.integration.dubbo;

import org.springframework.stereotype.Component;

@Component
public class DubboTestServiceImpl implements DubboTestService {

    @Override
    public String echo(String content) {
        return content;
    }
}
