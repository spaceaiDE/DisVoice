package de.spaceai.disvoice.listener;

import de.spaceai.disvoice.DisVoice;
import net.dv8tion.jda.api.entities.Member;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener {

    private final DisVoice disVoice;

    public PlayerMoveListener(DisVoice disVoice) {
        this.disVoice = disVoice;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {

        Player player = event.getPlayer();

        if(!this.disVoice.getLinkedAccountCache().existLinkedAccount(player.getUniqueId())) {
            player.teleport(event.getFrom());
            event.setCancelled(true);
            player.sendTitle("§cYour are not linked!", "§7Use §c/link");
            return;
        }

        if(this.disVoice.getVoiceModule().getGuild() == null)
            return;

        Member member = this.disVoice.getVoiceModule().getGuild().getMemberById(this.disVoice
                .getLinkedAccountCache().getLinkedAccount(player.getUniqueId()).discordId());

        if(member.getVoiceState() == null || !member.getVoiceState().inVoiceChannel() ||
        !member.getVoiceState().getChannel().getParent().equals(this.disVoice.getVoiceModule().getVoiceCategory())) {
            if(player.hasPermission("disvoice.bypass")) return;
            player.teleport(event.getFrom());
            event.setCancelled(true);
            player.sendTitle("§cError", "§7Please connect to voice chat!");
            return;
        }

    }

}
