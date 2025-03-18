package me.N0rM1x.carnacoSolverX.additional;

import me.N0rM1x.carnacoSolverX.CarnacoSolverX;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.yaml.snakeyaml.Yaml;

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

    private static YamlConfiguration messageConfig;
    private static  String lang;

    public ChatManager(CarnacoSolverX carnacoSolverX, String lang) {
        logger = carnacoSolverX.getLogger();
        configFile = carnacoSolverX.getConfigFile();
        config = YamlConfiguration.loadConfiguration(configFile);
        stylesConfigFile = new File(carnacoSolverX.getDataFolder(), "player-styles.yml");
        if (!stylesConfigFile.exists()) {
            try {
                boolean fileCreated = stylesConfigFile.createNewFile();
                if (fileCreated) {
                    logger.info("File created successfully!");
                } else {
                    logger.severe("File already exists!.");
                }
                stylesConfig = YamlConfiguration.loadConfiguration(stylesConfigFile);
                stylesConfig.createSection("player-prefixes");
                stylesConfig.createSection("player-suffixes");
                stylesConfig.createSection("player-chat-color");
                saveStylesConfig();
            } catch (IOException e) {
                logger.severe("Failed to create player-styles.yml!");
                logger.severe(e.getMessage());
            }
        } else {
            stylesConfig = YamlConfiguration.loadConfiguration(stylesConfigFile);
            logger.info("Styles config loaded successfully!");
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
        File messagesFile = new File(carnacoSolverX.getDataFolder() + "/message_lang/", lang);
        if (!messagesFile.exists()) {
            carnacoSolverX.saveResource("message_lang/en-US.yml", false);
            carnacoSolverX.saveResource("message_lang/ru-RU.yml", false);
            carnacoSolverX.saveResource("message_lang/ua-UA.yml", false);
            carnacoSolverX.getLogger().warning("Resource " + lang + " not found in the plugin resources. Switching to EN-us.yml");
            config.set("lang", "EN-us");
            carnacoSolverX.saveConfig();
        }
        messageConfig = YamlConfiguration.loadConfiguration(messagesFile);
    }

    public enum MessageType {
        CHATCOLOR,
        PREFIX,
        SUFFIX,
        CONSOLE
    }

    public ConfigurationSection getLangYaml(String target) {
        return messageConfig.getConfigurationSection(target);
    }

    public void saveStylesConfig() {
        try {
            stylesConfig.save(stylesConfigFile);
        } catch (IOException e) {
            logger.severe("Failed to save player-styles.yml!");
            logger.severe(e.getMessage());
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

    public static String placeHolder(String from, MessageType type, String playerName, String placeHolder) {
        String to = from;
        if (type == MessageType.CHATCOLOR) {
            to = from
                    .replace("%chatColor%", placeHolder)
                    .replace("%playerName%", playerName);
        }
        else if (type == MessageType.PREFIX) {
            to = from
                    .replace("%prefix%", placeHolder)
                    .replace("%playerName%", playerName);
        }
        else if (type == MessageType.SUFFIX) {
            to = from
                    .replace("%suffix%", placeHolder)
                    .replace("%playerName%", playerName);
        }
        else {
            logger.severe("Invalid message type!");
        }
        return to;
    }

    public Component getFormattedMessage(Player player, String message) {
        reloadStylesConfig();
        if (stylesConfig == null) {
            logger.severe("Styles config has not been loaded yet!");
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
            logger.severe("Styles config has not been loaded yet!");
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
