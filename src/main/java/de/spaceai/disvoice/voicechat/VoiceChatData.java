package de.spaceai.disvoice.voicechat;

import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import org.bukkit.entity.Player;

public class VoiceChatData {

    private final Player player;
    private final VoiceChannel voiceChannel;

    public VoiceChatData(Player player, VoiceChannel voiceChannel) {
        this.player = player;
        this.voiceChannel = voiceChannel;
    }

    public Player getPlayer() {
        return player;
    }

    public VoiceChannel getVoiceChannel() {
        return voiceChannel;
    }
}
