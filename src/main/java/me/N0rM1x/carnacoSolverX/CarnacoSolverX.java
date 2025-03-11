package me.N0rM1x.carnacoSolverX;

import me.N0rM1x.carnacoSolverX.additional.ChatManager;
import me.N0rM1x.carnacoSolverX.additional.EmojiManager;
import me.N0rM1x.carnacoSolverX.additional.PingUpdate;
import me.N0rM1x.carnacoSolverX.commands.ChatColorCMD;
import me.N0rM1x.carnacoSolverX.commands.PrefixCMD;
import me.N0rM1x.carnacoSolverX.commands.SuffixCMD;
import me.N0rM1x.carnacoSolverX.listeners.ChatListener;
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
        chatManager = new ChatManager(this);
        EmojiManager emojiManager = new EmojiManager(this);
        pingUpdate = new PingUpdate();
        getServer().getPluginManager().registerEvents(new ChatListener(emojiManager, chatManager), this);

        // Создаем экземпляр ChatManager и передаем ссылку на основной плагин
        Objects.requireNonNull(getCommand("prefix")).setExecutor(new PrefixCMD(chatManager));
        Objects.requireNonNull(getCommand("suffix")).setExecutor(new SuffixCMD(chatManager));
        Objects.requireNonNull(getCommand("chatcolor")).setExecutor(new ChatColorCMD(chatManager));
        pingUpdate.startPingUpdater(chatManager);
        getLogger().info("Плагин включён успешно!");
    }

    @Override
    public void onDisable() {
        chatManager.saveStylesConfig();
        pingUpdate.stopPingUpdater();
        getLogger().info("Плагин выключен успешно!");
    }
    public File getConfigFile() {
        return new File(getDataFolder(), "config.yml");
    }
    public static CarnacoSolverX getInstance() {
        return instance;
    }

}
