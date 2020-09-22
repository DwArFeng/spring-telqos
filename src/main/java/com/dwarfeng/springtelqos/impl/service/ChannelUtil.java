package com.dwarfeng.springtelqos.impl.service;

import io.netty.channel.Channel;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * Netty Channel 工具。
 *
 * @author DwArFeng
 * @since 1.0.0
 */
final class ChannelUtil {

    /**
     * 获取指定 Channel 的地址。
     *
     * @param channel 指定的 Channel。
     * @return 指定 Channel 的地址。
     */
    public static String getAddress(Channel channel) {
        InetSocketAddress socketAddress = (InetSocketAddress) channel.remoteAddress();
        InetAddress inetAddress = socketAddress.getAddress();
        if (inetAddress instanceof Inet4Address) {
            return String.format("%s:%d", inetAddress.getHostAddress(), socketAddress.getPort());
        } else if (inetAddress instanceof Inet6Address) {
            return String.format("[%s]:%d", inetAddress.getHostAddress(), socketAddress.getPort());
        } else {
            throw new IllegalStateException(
                    "未知的 ip 地址类型: " + inetAddress.getClass().getCanonicalName());
        }
    }

    public static String line(String message) {
        return message + System.lineSeparator();
    }
}
