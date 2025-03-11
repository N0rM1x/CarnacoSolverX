package me.N0rM1x.carnacoSolverX.additional;

import me.N0rM1x.carnacoSolverX.CarnacoSolverX;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class PingUpdate {
    private static BukkitTask pingTask = null;

    public PingUpdate() {
    }

    public void startPingUpdater(ChatManager chatManager) {
        if (pingTask != null && !pingTask.isCancelled()) {
            return; // Уже запущено
        }

        pingTask = new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    double ping = player.getPing();
                    Component tablistTemp = chatManager.getTablistStyles(player);
                    double normalizedPing = (ping - 50) / (1000 - 50);
                    normalizedPing = Math.min(Math.max(normalizedPing, 0), 1);
                    normalizedPing = Math.pow(normalizedPing, 0.5);

                    int red, green = 255;
                    if (normalizedPing <= 0.5) {
                        red = (int) (normalizedPing * 2 * 255);
                    } else {
                        red = 255;
                        green = (int) ((1 - normalizedPing) * 2 * 255);
                    }

                    Color color = Color.fromRGB(red, green, 0);
                    String displayPing = ("   &" + String.format("#%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue()) + (int) ping + "&#ccccccms");

                    Bukkit.getScheduler().runTask(CarnacoSolverX.getInstance(), () ->
                            chatManager.setTabName(player, tablistTemp.append(chatManager.color(displayPing)))
                    );
                }
            }
        }.runTaskTimer(CarnacoSolverX.getInstance(), 0L, 100L);
    }

    public void stopPingUpdater() {
        if (pingTask != null && !pingTask.isCancelled()) {
            pingTask.cancel();
        }
        pingTask = null;
    }
}
