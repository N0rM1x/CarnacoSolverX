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

import static me.N0rM1x.carnacoSolverX.additional.ChatManager.MessageType.PREFIX;

public class PrefixCMD implements CommandExecutor {
    private final ChatManager chatManager;

    public PrefixCMD(ChatManager chatManager) {
        this.chatManager = chatManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        ConfigurationSection messagesConfig = chatManager.getLangYaml("prefix_command");
        if (args.length == 0) {
            sender.sendMessage(chatManager.color(ChatManager.placeHolder(messagesConfig.getString("no_arguments"), PREFIX, "", "")));
            return false;
        }

        if (sender instanceof Player) {
            if (!(args[0].equals("set") || args[0].equals("clear"))) {
                sender.sendMessage(chatManager.color(ChatManager.placeHolder(messagesConfig.getString("usage"), PREFIX, "", "")));
                return false;
            }

            if (args.length < 2) {
                sender.sendMessage(chatManager.color(ChatManager.placeHolder(messagesConfig.getString("no_playername"), PREFIX, "", "")));
                return false;
            }

            String playerName = args[1];

            if (args[0].equals("set")) {
                if (args.length < 3) {
                    sender.sendMessage(chatManager.color(ChatManager.placeHolder(messagesConfig.getString("no_content"), PREFIX, "", "")));
                    return false;
                }
                String prefix = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
                boolean success = ChatManager.setPlayerPrefix(playerName, prefix);
                if (!success) {
                    sender.sendMessage(chatManager.color(ChatManager.placeHolder(messagesConfig.getString("set_failed"), PREFIX, "", "")));
                    return false;
                } else {
                    Player player = Bukkit.getPlayer(playerName);
                    chatManager.saveStylesConfig();
                    if (player != null) {
                        chatManager.reloadTabList(player);
                    }
                    sender.sendMessage(chatManager.color(ChatManager.placeHolder(messagesConfig.getString("set_success"), PREFIX, playerName, prefix)));
                    return true;
                }
            }
            else {
                boolean success = ChatManager.clearPlayerPrefix(playerName);
                if (!success) {
                    sender.sendMessage(chatManager.color(ChatManager.placeHolder(messagesConfig.getString("clear_failed"), PREFIX, "", "")));
                    return false;
                } else {
                    Player player = Bukkit.getPlayer(playerName);
                    chatManager.saveStylesConfig();
                    if (player != null) {
                        chatManager.reloadTabList(player);
                    }
                    sender.sendMessage(chatManager.color(ChatManager.placeHolder(messagesConfig.getString("clear_success"), PREFIX, playerName, "")));
                    return true;
                }
            }
        }

        return false;
    }
}
