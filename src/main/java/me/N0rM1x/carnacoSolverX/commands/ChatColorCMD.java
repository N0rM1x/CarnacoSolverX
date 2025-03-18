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

import static me.N0rM1x.carnacoSolverX.additional.ChatManager.MessageType.CHATCOLOR;

public class ChatColorCMD implements CommandExecutor {
    private final ChatManager chatManager;

    public ChatColorCMD(ChatManager chatManager) {
        this.chatManager = chatManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        ConfigurationSection messagesConfig = chatManager.getLangYaml("chatcolor_command");
        if (args.length == 0) {
            sender.sendMessage(chatManager.color(messagesConfig.getString("no_arguments")));
            return false;
        }

        if (sender instanceof Player) {
            if (!(args[0].equals("set") || args[0].equals("clear"))) {
                sender.sendMessage(chatManager.color(ChatManager.placeHolder(messagesConfig.getString("usage"), CHATCOLOR, "", "")));
                return false;
            }

            if (args.length < 2) {
                sender.sendMessage(chatManager.color(ChatManager.placeHolder(messagesConfig.getString("no_playername"), CHATCOLOR, "", "")));
                return false;
            }

            String playerName = args[1];

            if (args[0].equals("set")) {
                if (args.length < 3) {
                    sender.sendMessage(chatManager.color(ChatManager.placeHolder(messagesConfig.getString("no_content"), CHATCOLOR, "", "")));
                    return false;
                }
                String chatColor = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
                boolean success = ChatManager.setPlayerChatColor(playerName, chatColor);
                if (!success) {
                    sender.sendMessage(chatManager.color(ChatManager.placeHolder(messagesConfig.getString("set_failed"), CHATCOLOR, "", "")));
                    return false;
                } else {
                    Player player = Bukkit.getPlayer(playerName);
                    chatManager.saveStylesConfig();
                    if (player != null) {
                        chatManager.reloadTabList(player);
                    }
                    sender.sendMessage(chatManager.color(ChatManager.placeHolder(messagesConfig.getString("set_success"), CHATCOLOR, playerName, chatColor)));
                    return true;
                }
            }
            else {
                boolean success = ChatManager.clearPlayerChatColor(playerName);
                if (!success) {
                    sender.sendMessage(chatManager.color(ChatManager.placeHolder(messagesConfig.getString("clear_failed"), CHATCOLOR, "", "")));
                    return false;
                } else {
                    Player player = Bukkit.getPlayer(playerName);
                    chatManager.saveStylesConfig();
                    if (player != null) {
                        chatManager.reloadTabList(player);
                    }
                    sender.sendMessage(chatManager.color(ChatManager.placeHolder(messagesConfig.getString("clear_success"), CHATCOLOR, playerName, "")));
                    return true;
                }
            }
        }

        return false;
    }
}
