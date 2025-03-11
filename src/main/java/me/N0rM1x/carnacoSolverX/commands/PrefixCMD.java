package me.N0rM1x.carnacoSolverX.commands;

import me.N0rM1x.carnacoSolverX.additional.ChatManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class PrefixCMD implements CommandExecutor {
    private final ChatManager chatManager;

    public PrefixCMD(ChatManager chatManager) {
        this.chatManager = chatManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("Вы должны указать действие (set/clear).");
            return false;
        }

        if (sender instanceof Player) {
            if (!(args[0].equals("set") || args[0].equals("clear"))) {
                sender.sendMessage("§cИспользование: /prefix <set/clear> <игрок> [префикс]");
                return false;
            }

            if (args.length < 2) {
                sender.sendMessage("§cВы должны указать имя игрока.");
                return false;
            }

            String playerName = args[1];

            if (args[0].equals("set")) {
                if (args.length < 3) {
                    sender.sendMessage("§cВы должны указать префикс для игрока.");
                    return false;
                }
                String prefix = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
                boolean success = ChatManager.setPlayerPrefix(playerName, prefix);
                if (!success) {
                    sender.sendMessage("§cНе удалось установить префикс!");
                    return false;
                } else {
                    Player player = Bukkit.getPlayer(playerName);
                    chatManager.saveStylesConfig();
                    chatManager.reloadTabList(player);
                    sender.sendMessage(chatManager.color("§aИгроку§f " + playerName + "§a успешно установлен префикс:§r " + prefix + playerName + "§a!"));
                    return true;
                }
            }
            else {
                boolean success = ChatManager.clearPlayerPrefix(playerName);
                if (!success) {
                    sender.sendMessage("§cНе удалось убрать префикс!");
                    return false;
                } else {
                    Player player = Bukkit.getPlayer(playerName);
                    chatManager.saveStylesConfig();
                    chatManager.reloadTabList(player);
                    sender.sendMessage("§aИгроку§f " + playerName + "§a успешно убран префикс!");
                    return true;
                }
            }
        }

        return false;
    }
}
