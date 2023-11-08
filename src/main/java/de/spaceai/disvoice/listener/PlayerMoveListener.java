package de.spaceai.disvoice.listener;

import de.spaceai.disvoice.DisVoice;
import de.spaceai.disvoice.database.account.LinkedAccount;
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

        LinkedAccount linkedAccount = this.disVoice.getLinkedAccountCache().getLinkedAccount(player.getUniqueId());
        Member member = this.disVoice.getVoiceModule().getGuild().getMemberById(linkedAccount.discordId());

        if(member == null) {
            this.disVoice.getVoiceModule().getGuild().retrieveMemberById(linkedAccount.discordId());
            return;
        }

        if(member.getVoiceState() == null || !member.getVoiceState().inAudioChannel() ||
        !member.getVoiceState().getChannel().getParentCategory().equals(this.disVoice.getVoiceModule().getVoiceCategory())) {
            if(player.hasPermission("disvoice.bypass")) return;
            player.teleport(event.getFrom());
            event.setCancelled(true);
            player.sendTitle("§cError", "§7Please connect to voice chat!");
            return;
        }

    }

}
