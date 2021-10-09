package de.spaceai.disvoice.verification;

import org.bukkit.entity.Player;

public class PendingVerification {

    private final Player player;
    private final String code;

    public PendingVerification(Player player, String code) {
        this.player = player;
        this.code = code;
    }

    public Player getPlayer() {
        return player;
    }

    public String getCode() {
        return code;
    }
}
