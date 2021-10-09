package de.spaceai.disvoice.listener;

import de.spaceai.disvoice.DisVoice;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private final DisVoice disVoice;

    public PlayerJoinListener(DisVoice disVoice) {
        this.disVoice = disVoice;
    }

    @EventHandler
    public void playerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

    }
}
