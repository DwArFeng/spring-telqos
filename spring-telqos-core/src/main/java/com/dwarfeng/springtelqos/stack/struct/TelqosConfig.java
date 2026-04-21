package com.dwarfeng.springtelqos.stack.struct;

import com.dwarfeng.dutil.basic.prog.Buildable;
import com.dwarfeng.springtelqos.stack.command.Command;
import com.dwarfeng.springtelqos.stack.util.TelqosConfigUtil;

import java.util.Collection;
import java.util.Collections;

/**
 * Telqos 配置。
 *
 * @author DwArFeng
 * @since 2.0.0
 */
public final class TelqosConfig {

    /**
     * Telnet 服务监听端口。
     */
    private final int port;

    /**
     * 连接白名单正则表达式；空字符串表示不启用白名单。
     */
    private final String whitelistRegex;

    /**
     * 连接黑名单正则表达式；空字符串表示不启用黑名单。
     */
    private final String blacklistRegex;

    /**
     * 服务端与客户端交互使用的字符集名称。
     */
    private final String charset;

    /**
     * Banner 资源定位符（支持 Spring Resource 语义）；空字符串表示不显示 Banner。
     */
    private final String bannerUrl;

    /**
     * 启动时需要注册的指令集合。
     *
     * <p>
     * 调用者有义务保证不调用该集合的修改方法（如 {@link Collection#add} 等），否则可能导致不可预期的行为。
     *
     * @see Command
     */
    private final Collection<Command> commands;

    public TelqosConfig(
            int port, String whitelistRegex, String blacklistRegex, String charset, String bannerUrl,
            Collection<Command> commands
    ) {
        this(port, whitelistRegex, blacklistRegex, charset, bannerUrl, commands, false);
    }

    private TelqosConfig(
            int port, String whitelistRegex, String blacklistRegex, String charset, String bannerUrl,
            Collection<Command> commands, boolean paramReliable
    ) {
        // 如果参数不可靠，则检查参数。
        if (!paramReliable) {
            TelqosConfigUtil.checkPort(port);
            TelqosConfigUtil.checkWhitelistRegex(whitelistRegex);
            TelqosConfigUtil.checkBlacklistRegex(blacklistRegex);
            TelqosConfigUtil.checkCharset(charset);
            TelqosConfigUtil.checkBannerUrl(bannerUrl);
            TelqosConfigUtil.checkCommands(commands);
        }
        // 设置值。
        this.port = port;
        this.whitelistRegex = whitelistRegex;
        this.blacklistRegex = blacklistRegex;
        this.charset = charset;
        this.bannerUrl = bannerUrl;
        this.commands = commands;
    }

    public int getPort() {
        return port;
    }

    public String getWhitelistRegex() {
        return whitelistRegex;
    }

    public String getBlacklistRegex() {
        return blacklistRegex;
    }

    public String getCharset() {
        return charset;
    }

    public String getBannerUrl() {
        return bannerUrl;
    }

    public Collection<Command> getCommands() {
        return commands;
    }

    @Override
    public String toString() {
        return "TelqosConfig{" +
                "port=" + port +
                ", whitelistRegex='" + whitelistRegex + '\'' +
                ", blacklistRegex='" + blacklistRegex + '\'' +
                ", charset='" + charset + '\'' +
                ", bannerUrl='" + bannerUrl + '\'' +
                ", commands=" + commands +
                '}';
    }

    /**
     * Telqos 配置构造器。
     *
     * @author DwArFeng
     * @since 2.0.0
     */
    public static final class Builder implements Buildable<TelqosConfig> {

        public static final int DEFAULT_PORT = 23;
        public static final String DEFAULT_WHITELIST_REGEX = "";
        public static final String DEFAULT_BLACKLIST_REGEX = "";
        public static final String DEFAULT_CHARSET = "UTF-8";
        public static final String DEFAULT_BANNER_URL = "classpath:telqos/banner.txt";
        public static final Collection<Command> DEFAULT_COMMANDS = Collections.emptySet();

        private int port = DEFAULT_PORT;
        private String whitelistRegex = DEFAULT_WHITELIST_REGEX;
        private String blacklistRegex = DEFAULT_BLACKLIST_REGEX;
        private String charset = DEFAULT_CHARSET;
        private String bannerUrl = DEFAULT_BANNER_URL;
        private Collection<Command> commands = DEFAULT_COMMANDS;

        public Builder() {
        }

        public Builder setPort(int port) {
            this.port = port;
            return this;
        }

        public Builder setWhitelistRegex(String whitelistRegex) {
            this.whitelistRegex = whitelistRegex;
            return this;
        }

        public Builder setBlacklistRegex(String blacklistRegex) {
            this.blacklistRegex = blacklistRegex;
            return this;
        }

        public Builder setCharset(String charset) {
            this.charset = charset;
            return this;
        }

        public Builder setBannerUrl(String bannerUrl) {
            this.bannerUrl = bannerUrl;
            return this;
        }

        public Builder setCommands(Collection<Command> commands) {
            this.commands = commands;
            return this;
        }

        @Override
        public TelqosConfig build() {
            // 检查参数。
            TelqosConfigUtil.checkPort(port);
            TelqosConfigUtil.checkWhitelistRegex(whitelistRegex);
            TelqosConfigUtil.checkBlacklistRegex(blacklistRegex);
            TelqosConfigUtil.checkCharset(charset);
            TelqosConfigUtil.checkBannerUrl(bannerUrl);
            TelqosConfigUtil.checkCommands(commands);

            // 构造并返回配置。
            return new TelqosConfig(
                    port, whitelistRegex, blacklistRegex, charset, bannerUrl, commands, true
            );
        }

        @Override
        public String toString() {
            return "Builder{" +
                    "port=" + port +
                    ", whitelistRegex='" + whitelistRegex + '\'' +
                    ", blacklistRegex='" + blacklistRegex + '\'' +
                    ", charset='" + charset + '\'' +
                    ", bannerUrl='" + bannerUrl + '\'' +
                    ", commands=" + commands +
                    '}';
        }
    }
}
