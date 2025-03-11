package me.N0rM1x.carnacoSolverX.additional;

import me.N0rM1x.carnacoSolverX.CarnacoSolverX;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class EmojiManager {
    private final Map<String, String> emojiMap = new HashMap<>();
    File emojisFile;


    public EmojiManager(CarnacoSolverX carnacoSolverX) {
        emojisFile = new File(carnacoSolverX.getDataFolder(), "emojis.yml");
        if (!emojisFile.exists()) {
            carnacoSolverX.saveResource("emojis.yml", false);
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(emojisFile);
        for (String key : config.getKeys(false)) {
            emojiMap.put(":" + key + ":", config.getString(key));
        }
    }

    public String replaceEmojis(String message) {
        for (Map.Entry<String, String> entry : emojiMap.entrySet()) {
            message = message.replace(entry.getKey(), entry.getValue());
        }
        return message;
    }
}
