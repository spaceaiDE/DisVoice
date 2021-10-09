package de.spaceai.disvoice;

import com.google.common.collect.Maps;
import de.spaceai.disvoice.command.LinkCommand;
import de.spaceai.disvoice.command.UnlinkCommand;
import de.spaceai.disvoice.config.PluginConfig;
import de.spaceai.disvoice.database.Database;
import de.spaceai.disvoice.database.account.LinkedAccountCache;
import de.spaceai.disvoice.discord.Discord;
import de.spaceai.disvoice.listener.PlayerMoveListener;
import de.spaceai.disvoice.logging.ILogger;
import de.spaceai.disvoice.logging.impl.Logger;
import de.spaceai.disvoice.verification.VerificationCache;
import de.spaceai.disvoice.voicechat.VoiceModule;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class DisVoice extends JavaPlugin {

    private PluginConfig pluginConfig;
    private VerificationCache verificationCache;
    private LinkedAccountCache linkedAccountCache;
    private Database database;
    private Discord discord;
    private ILogger logger;
    private VoiceModule voiceModule;

    @Override
    public void onEnable() {

        this.logger = new Logger();

        this.verificationCache = new VerificationCache(this);
        this.pluginConfig = new PluginConfig(new File("plugins/DisVoice/config.yml"));

        this.database = new Database(this, this.pluginConfig.getObject("mysql.host"),
                this.pluginConfig.getObject("mysql.port"), this.pluginConfig.getObject("mysql.database"),
                this.pluginConfig.getObject("mysql.user"), this.pluginConfig.getObject("mysql.password"));

        this.database.connect();

        this.database.createTable("linkedAccounts", "id INT NOT NULL AUTO_INCREMENT","uuid TEXT", "discordId TEXT", "PRIMARY KEY(id)");

        this.linkedAccountCache = new LinkedAccountCache(this);
        this.linkedAccountCache.load();

        this.discord = new Discord(this, this.pluginConfig.getObject("discord.token"));
        this.discord.connect();

        this.voiceModule = new VoiceModule(this);
        this.voiceModule.start();

        getServer().getPluginManager().registerEvents(new PlayerMoveListener(this), this);
        getCommand("link").setExecutor(new LinkCommand(this));
        getCommand("unlink").setExecutor(new UnlinkCommand(this));

    }

    @Override
    public void onDisable() {
        this.voiceModule.shutdown();
        if(this.database.getConnection() != null && this.database.isConnected()) {
            this.database.disconnect();
        }
    }

    public PluginConfig getPluginConfig() {
        return pluginConfig;
    }

    public Discord getDiscord() {
        return discord;
    }

    public ILogger getLog() {
        return logger;
    }

    public VerificationCache getVerificationCache() {
        return verificationCache;
    }

    public Database getDatabase() {
        return database;
    }

    public VoiceModule getVoiceModule() {
        return voiceModule;
    }

    public LinkedAccountCache getLinkedAccountCache() {
        return linkedAccountCache;
    }
}
