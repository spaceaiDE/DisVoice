package de.spaceai.disvoice.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class PluginConfig {

    private final File file;

    public PluginConfig(File file) {
        this.file = file;
        if(!file.exists()) {
            FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(file);
            fileConfiguration.set("discord.token", "test");
            fileConfiguration.set("voicechat.distance", 8);
            fileConfiguration.set("discord.voiceCategoryId", "test");
            fileConfiguration.set("discord.lobbyChannelId", "test");
            fileConfiguration.set("discord.guildId", "test");
            fileConfiguration.set("mysql.user", "root");
            fileConfiguration.set("mysql.password", "example");
            fileConfiguration.set("mysql.database", "example");
            fileConfiguration.set("mysql.host", "example.com");
            fileConfiguration.set("mysql.port", 3306);
            try {
                fileConfiguration.save(this.file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public <T>T getObject(String path) {
        FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(file);
        return (T) fileConfiguration.get(path);
    }

    public void set(String path, Object object) {
        FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(file);
        fileConfiguration.set(path, object);
        try {
            fileConfiguration.save(this.file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
