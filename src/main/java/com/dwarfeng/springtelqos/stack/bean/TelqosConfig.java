package com.dwarfeng.springtelqos.stack.bean;

import com.dwarfeng.springtelqos.stack.command.Command;
import com.dwarfeng.springtelqos.stack.serialize.Deserializer;
import com.dwarfeng.springtelqos.stack.serialize.Serializer;

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
    private Serializer serializer;
    private Deserializer deserializer;
    private String defaultNamespace;
    private Collection<Command> commands;

    public TelqosConfig() {
    }

    public TelqosConfig(
            int port, String whitelistRegex, String blacklistRegex, String charset, String bannerUrl,
            Serializer serializer, Deserializer deserializer, String defaultNamespace,
            Collection<Command> commands) {
        this.port = port;
        this.whitelistRegex = whitelistRegex;
        this.blacklistRegex = blacklistRegex;
        this.charset = charset;
        this.bannerUrl = bannerUrl;
        this.serializer = serializer;
        this.deserializer = deserializer;
        this.defaultNamespace = defaultNamespace;
        this.commands = commands;
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

    public Serializer getSerializer() {
        return serializer;
    }

    public void setSerializer(Serializer serializer) {
        this.serializer = serializer;
    }

    public Deserializer getDeserializer() {
        return deserializer;
    }

    public void setDeserializer(Deserializer deserializer) {
        this.deserializer = deserializer;
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

    @Override
    public String toString() {
        return "TelqosConfig{" +
                "port=" + port +
                ", whitelistRegex='" + whitelistRegex + '\'' +
                ", blacklistRegex='" + blacklistRegex + '\'' +
                ", charset='" + charset + '\'' +
                ", bannerUrl='" + bannerUrl + '\'' +
                ", serializer=" + serializer +
                ", deserializer=" + deserializer +
                ", defaultNamespace='" + defaultNamespace + '\'' +
                ", commands=" + commands +
                '}';
    }
}
