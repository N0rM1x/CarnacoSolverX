package me.N0rM1x.carnacoSolverX.additional;

import me.N0rM1x.carnacoSolverX.CarnacoSolverX;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatManager implements Listener {
    private static Logger logger;

    static File configFile;
    static FileConfiguration config;
    static String tablistConfigStyle;
    static String chatConfigStyle;
    static String defaultPrefixStyle;
    static String defaultSuffixStyle;
    static String defaultChatColorStyle;

    private static File stylesConfigFile;
    private static FileConfiguration stylesConfig;

    private final Map<String, String> colorMap = new HashMap<>();

    public ChatManager(CarnacoSolverX carnacoSolverX) {
        logger = carnacoSolverX.getLogger();
        configFile = carnacoSolverX.getConfigFile();
        config = YamlConfiguration.loadConfiguration(configFile);
        stylesConfigFile = new File(carnacoSolverX.getDataFolder(), "player-styles.yml");
        if (!stylesConfigFile.exists()) {
            try {
                boolean fileCreated = stylesConfigFile.createNewFile();
                if (fileCreated) {
                    logger.info("Файл успешно создан!");
                } else {
                    logger.severe("Файл уже существует.");
                }
                stylesConfig = YamlConfiguration.loadConfiguration(stylesConfigFile);
                stylesConfig.createSection("player-prefixes");
                stylesConfig.createSection("player-suffixes");
                stylesConfig.createSection("player-chat-color");
                saveStylesConfig();
            } catch (IOException e) {
                logger.severe("Не удалось создать player-styles.yml!");
                logger.severe("Произошла ошибка: " + e.getMessage());
            }
        } else {
            stylesConfig = YamlConfiguration.loadConfiguration(stylesConfigFile);
            logger.info("Конфигурация стилей загружена успешно!");
        }
        tablistConfigStyle = carnacoSolverX.getConfig().getString("tablist.default.style", "&c[ERR]&r %playername%");
        chatConfigStyle = carnacoSolverX.getConfig().getString("chats.default.style", "&c[ERR]&r %playername% » %message%");
        defaultPrefixStyle = config.getString("prefixes.default.prefix");
        defaultSuffixStyle = config.getString("suffixes.default.suffix");
        defaultChatColorStyle = config.getString("chatcolor.default.color");

        File colorConfigFile = new File(carnacoSolverX.getDataFolder(), "colors_config.yml");
        if (!colorConfigFile.exists()) {
            carnacoSolverX.saveResource("colors_config.yml", false);
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(colorConfigFile);
        for (String key : config.getKeys(false)) {
            colorMap.put(key, config.getString(key));
        }
    }

    public void saveStylesConfig() {
        try {
            stylesConfig.save(stylesConfigFile);
        } catch (IOException e) {
            logger.severe("Не удалось сохранить player-styles.yml!");
            logger.severe("Произошла ошибка: " + e.getMessage());
        }
    }

    public void reloadStylesConfig() {
        saveStylesConfig();
        stylesConfig = YamlConfiguration.loadConfiguration(stylesConfigFile);
    }

    public void setTabName(Player player, Component style) {
        player.playerListName(style);
    }

    public Component color(String from) {
        Pattern hexPattern = Pattern.compile("&#([0-9A-Fa-f]{6})");
        Pattern oldPattern = Pattern.compile("&(.)");

        Matcher hexMatcher = hexPattern.matcher(from);
        StringBuilder buffer = new StringBuilder();

        while (hexMatcher.find()) {
            String hexCode = hexMatcher.group(1);
            hexMatcher.appendReplacement(buffer, "<#" + hexCode + ">");
        }
        hexMatcher.appendTail(buffer);
        from = buffer.toString();

        Matcher oldMatcher = oldPattern.matcher(from);
        buffer.setLength(0);

        while (oldMatcher.find()) {
            String colorCode = oldMatcher.group(1);
            String replacement = colorMap.getOrDefault(colorCode, "&" + colorCode); // Безопасное получение значения
            oldMatcher.appendReplacement(buffer, replacement);
        }
        oldMatcher.appendTail(buffer);
        from = buffer.toString();

        MiniMessage miniMessage = MiniMessage.miniMessage();
        return miniMessage.deserialize(from);
    }

    public Component getFormattedMessage(Player player, String message) {
        reloadStylesConfig();
        if (stylesConfig == null) {
            logger.severe("Конфигурация стилей не была загружена!");
            return color(message);
        }
        String playerName = player.getName();

        String prefix = stylesConfig.getString("player-prefixes." + playerName, defaultPrefixStyle);
        String suffix = stylesConfig.getString("player-suffixes." + playerName, defaultSuffixStyle);
        String chatColor = stylesConfig.getString("player-chat-color." + playerName, defaultChatColorStyle);

        String formattedMessage = chatConfigStyle
                .replace("%prefix%", prefix)
                .replace("%player%", playerName)
                .replace("%suffix%", suffix)
                .replace("%message%", message)
                .replace("%chatcolor%", chatColor);

        return color(formattedMessage);
    }

    public void reloadTabList(Player player) {
        reloadStylesConfig();
        if (stylesConfig == null) {
            logger.severe("Конфигурация стилей не была загружена!");
        }
        String playerName = player.getName();


        String prefix = stylesConfig.getString("player-prefixes." + playerName, defaultPrefixStyle);
        String suffix = stylesConfig.getString("player-suffixes." + playerName, defaultSuffixStyle);
        String tablistStyle = tablistConfigStyle;

        String formattedTablist = tablistStyle
                .replace("%prefix%", prefix)
                .replace("%player%", playerName)
                .replace("%suffix%", suffix
                );

        setTabName(player, color(formattedTablist));
    }

    public Component getTablistStyles(Player player) {
        String playerName = player.getName();

        String prefix = stylesConfig.getString("player-prefixes." + playerName, defaultPrefixStyle);
        String suffix = stylesConfig.getString("player-suffixes." + playerName, defaultSuffixStyle);
        String tablistStyle = tablistConfigStyle

                .replace("%prefix%", prefix)
                .replace("%player%", playerName)
                .replace("%suffix%", suffix);

        return color(tablistStyle);
    }

    public static boolean setPlayerPrefix(String playerName, String prefix) {
        if (prefix != null && playerName != null) {
            stylesConfig.set("player-prefixes." + playerName, prefix);
            return true;
        }
        else {
            return false;
        }
    }
    public static boolean clearPlayerPrefix(String playerName) {
        if (playerName != null) {
            stylesConfig.set("player-prefixes." + playerName, defaultPrefixStyle);
            return true;
        }
        else {
            return false;
        }
    }

    public static boolean setPlayerSuffix(String playerName, String suffix) {
        if (suffix != null && playerName != null) {
            stylesConfig.set("player-suffixes." + playerName, suffix);
            return true;
        }
        else {
            return false;
        }
    }
    public static boolean clearPlayerSuffix(String playerName) {
        if (playerName != null) {
            stylesConfig.set("player-suffixes." + playerName, defaultSuffixStyle);
            return true;
        }
        else {
            return false;
        }
    }

    public static boolean setPlayerChatColor(String playerName, String chatColor) {
        if (chatColor != null && playerName != null) {
            stylesConfig.set("player-chat-color." + playerName, chatColor);
            return true;
        }
        else {
            return false;
        }
    }
    public static boolean clearPlayerChatColor(String playerName) {
        if (playerName != null) {
            stylesConfig.set("player-chat-color." + playerName, defaultChatColorStyle);
            return true;
        }
        else {
            return false;
        }
    }
}
