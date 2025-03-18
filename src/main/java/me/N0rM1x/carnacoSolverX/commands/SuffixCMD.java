package me.N0rM1x.carnacoSolverX.commands;

import me.N0rM1x.carnacoSolverX.additional.ChatManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

import static me.N0rM1x.carnacoSolverX.additional.ChatManager.MessageType.SUFFIX;

public class SuffixCMD implements CommandExecutor {
    private final ChatManager chatManager;

    public SuffixCMD(ChatManager chatManager) {
        this.chatManager = chatManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        ConfigurationSection messagesConfig = chatManager.getLangYaml("suffix_command");
        if (args.length == 0) {
            sender.sendMessage(chatManager.color(ChatManager.placeHolder(messagesConfig.getString("no_arguments"), SUFFIX, "", "")));
            return false;
        }

        if (sender instanceof Player) {
            if (!(args[0].equals("set") || args[0].equals("clear"))) {
                sender.sendMessage(chatManager.color(ChatManager.placeHolder(messagesConfig.getString("usage"), SUFFIX, "", "")));
                return false;
            }

            if (args.length < 2) {
                sender.sendMessage(chatManager.color(ChatManager.placeHolder(messagesConfig.getString("no_playername"), SUFFIX, "", "")));
                return false;
            }

            String playerName = args[1];

            if (args[0].equals("set")) {
                if (args.length < 3) {
                    sender.sendMessage(chatManager.color(ChatManager.placeHolder(messagesConfig.getString("no_content"), SUFFIX, "", "")));
                    return false;
                }
                String suffix = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
                boolean success = ChatManager.setPlayerSuffix(playerName, suffix);
                if (!success) {
                    sender.sendMessage(chatManager.color(ChatManager.placeHolder(messagesConfig.getString("set_failed"), SUFFIX, "", "")));
                    return false;
                } else {
                    Player player = Bukkit.getPlayer(playerName);
                    chatManager.saveStylesConfig();
                    if (player != null) {
                        chatManager.reloadTabList(player);
                    }
                    sender.sendMessage(chatManager.color(ChatManager.placeHolder(messagesConfig.getString("set_success"), SUFFIX, playerName, suffix)));
                    return true;
                }
            }
            else {
                boolean success = ChatManager.clearPlayerSuffix(playerName);
                if (!success) {
                    sender.sendMessage(chatManager.color(ChatManager.placeHolder(messagesConfig.getString("clear_failed"), SUFFIX, "", "")));
                    return false;
                } else {
                    Player player = Bukkit.getPlayer(playerName);
                    chatManager.saveStylesConfig();
                    if (player != null) {
                        chatManager.reloadTabList(player);
                    }
                    sender.sendMessage(chatManager.color(ChatManager.placeHolder(messagesConfig.getString("clear_success"), SUFFIX, playerName, "")));
                    return true;
                }
            }
        }

        return false;
    }
}
