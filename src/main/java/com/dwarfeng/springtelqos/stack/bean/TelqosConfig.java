package com.dwarfeng.springtelqos.stack.bean;

import java.io.Serializable;

/**
 * Telqos 的设置 bean。
 *
 * @author DwArFeng
 * @since 1.0.0
 */
public class TelqosConfig implements Serializable {

    private static final long serialVersionUID = 3485734126485661953L;

    private int port = 22;
    private String whitelistRegex = "";
    private String blacklistRegex = "";
    private String password = "";
    private String charset = "UTF-8";
    private int soBacklog = 1024;

    public TelqosConfig() {
    }

    public TelqosConfig(
            int port, String whitelistRegex, String blacklistRegex, String password, String charset, int soBacklog) {
        this.port = port;
        this.whitelistRegex = whitelistRegex;
        this.blacklistRegex = blacklistRegex;
        this.password = password;
        this.charset = charset;
        this.soBacklog = soBacklog;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public int getSoBacklog() {
        return soBacklog;
    }

    public void setSoBacklog(int soBacklog) {
        this.soBacklog = soBacklog;
    }

    @Override
    public String toString() {
        return "TelqosConfig{" +
                "port=" + port +
                ", whitelistRegex='" + whitelistRegex + '\'' +
                ", blacklistRegex='" + blacklistRegex + '\'' +
                ", password='" + password + '\'' +
                ", charset='" + charset + '\'' +
                ", soBacklog=" + soBacklog +
                '}';
    }
}
