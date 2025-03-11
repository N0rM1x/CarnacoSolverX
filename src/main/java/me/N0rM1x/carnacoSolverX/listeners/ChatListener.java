package me.N0rM1x.carnacoSolverX.listeners;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.N0rM1x.carnacoSolverX.additional.EmojiManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.entity.Player;
import me.N0rM1x.carnacoSolverX.additional.ChatManager;
import org.bukkit.event.player.PlayerJoinEvent;

public class ChatListener implements Listener {

    private final ChatManager chatManager;
    private final EmojiManager emojiManager;

    public ChatListener(EmojiManager emojiManager, ChatManager chatManager) {
        this.chatManager = chatManager;
        this.emojiManager = emojiManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        chatManager.reloadTabList(player);
    }
    @EventHandler
    public void onPlayerChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        Component messageComponent = event.message();
        String message = ((TextComponent) messageComponent).content();
        String emojiMessage = emojiManager.replaceEmojis(message);
        Component formattedMessage = chatManager.getFormattedMessage(player, emojiMessage);
        event.setCancelled(true);
        event.getPlayer().getServer().broadcast(formattedMessage);
    }

}
