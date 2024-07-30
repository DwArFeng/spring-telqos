package com.dwarfeng.springtelqos.stack.bean;

import com.dwarfeng.springtelqos.stack.command.Command;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Collection;

/**
 * Telqos配置。
 *
 * @author DwArFeng
 * @since 1.0.0
 */
public class TelqosConfig {

    private int port;
    private String whitelistRegex;
    private String blacklistRegex;
    private String charset;
    private String bannerUrl;
    private String defaultNamespace;
    private Collection<Command> commands;
    private ThreadPoolTaskExecutor executor;

    public TelqosConfig() {
    }

    public TelqosConfig(
            int port, String whitelistRegex, String blacklistRegex, String charset, String bannerUrl,
            String defaultNamespace, Collection<Command> commands, ThreadPoolTaskExecutor executor
    ) {
        this.port = port;
        this.whitelistRegex = whitelistRegex;
        this.blacklistRegex = blacklistRegex;
        this.charset = charset;
        this.bannerUrl = bannerUrl;
        this.defaultNamespace = defaultNamespace;
        this.commands = commands;
        this.executor = executor;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getWhitelistRegex() {
        return whitelistRegex;
    }

    public void setWhitelistRegex(String whitelistRegex) {
        this.whitelistRegex = whitelistRegex;
    }

    public String getBlacklistRegex() {
        return blacklistRegex;
    }

    public void setBlacklistRegex(String blacklistRegex) {
        this.blacklistRegex = blacklistRegex;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String getBannerUrl() {
        return bannerUrl;
    }

    public void setBannerUrl(String bannerUrl) {
        this.bannerUrl = bannerUrl;
    }

    public String getDefaultNamespace() {
        return defaultNamespace;
    }

    public void setDefaultNamespace(String defaultNamespace) {
        this.defaultNamespace = defaultNamespace;
    }

    public Collection<Command> getCommands() {
        return commands;
    }

    public void setCommands(Collection<Command> commands) {
        this.commands = commands;
    }

    public ThreadPoolTaskExecutor getExecutor() {
        return executor;
    }

    public void setExecutor(ThreadPoolTaskExecutor executor) {
        this.executor = executor;
    }

    @Override
    public String toString() {
        return "TelqosConfig{" +
                "port=" + port +
                ", whitelistRegex='" + whitelistRegex + '\'' +
                ", blacklistRegex='" + blacklistRegex + '\'' +
                ", charset='" + charset + '\'' +
                ", bannerUrl='" + bannerUrl + '\'' +
                ", defaultNamespace='" + defaultNamespace + '\'' +
                ", commands=" + commands +
                ", executor=" + executor +
                '}';
    }
}
