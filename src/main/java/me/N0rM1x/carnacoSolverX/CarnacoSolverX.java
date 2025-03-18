package me.N0rM1x.carnacoSolverX;

import me.N0rM1x.carnacoSolverX.additional.ChatManager;
import me.N0rM1x.carnacoSolverX.additional.EmojiManager;
import me.N0rM1x.carnacoSolverX.additional.PingUpdate;
import me.N0rM1x.carnacoSolverX.commands.ChatColorCMD;
import me.N0rM1x.carnacoSolverX.commands.PrefixCMD;
import me.N0rM1x.carnacoSolverX.commands.SuffixCMD;
import me.N0rM1x.carnacoSolverX.listeners.ChatListener;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Objects;

public final class CarnacoSolverX extends JavaPlugin {

    private ChatManager chatManager;
    private PingUpdate pingUpdate;
    private static CarnacoSolverX instance;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        File configFile = new File(getDataFolder(), "config.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        String lang = config.getString("lang") + ".yml";
        chatManager = new ChatManager(this, lang);
        EmojiManager emojiManager = new EmojiManager(this);
        pingUpdate = new PingUpdate();
        getServer().getPluginManager().registerEvents(new ChatListener(emojiManager, chatManager), this);
        // Создаем экземпляр ChatManager и передаем ссылку на основной плагин
        Objects.requireNonNull(getCommand("prefix")).setExecutor(new PrefixCMD(chatManager));
        Objects.requireNonNull(getCommand("suffix")).setExecutor(new SuffixCMD(chatManager));
        Objects.requireNonNull(getCommand("chatcolor")).setExecutor(new ChatColorCMD(chatManager));
        pingUpdate.startPingUpdater(chatManager);
        getLogger().info("Plugin enabled successfully!");
    }

    @Override
    public void onDisable() {
        chatManager.saveStylesConfig();
        pingUpdate.stopPingUpdater();
        getLogger().info("Plugin disabled successfully!");
    }
    public File getConfigFile() {
        return new File(getDataFolder(), "config.yml");
    }
    public static CarnacoSolverX getInstance() {
        return instance;
    }
}
