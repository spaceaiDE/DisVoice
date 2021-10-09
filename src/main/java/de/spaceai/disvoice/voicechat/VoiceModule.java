package de.spaceai.disvoice.voicechat;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import de.spaceai.disvoice.DisVoice;
import de.spaceai.disvoice.config.PluginConfig;
import de.spaceai.disvoice.database.Database;
import de.spaceai.disvoice.database.account.LinkedAccountCache;
import de.spaceai.disvoice.discord.Discord;
import de.spaceai.disvoice.util.ObjectQueue;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class VoiceModule {

    private final DisVoice disVoice;
    private final PluginConfig pluginConfig;
    private final Database database;
    private final Discord discord;

    private List<VoiceChatData> voiceChannels;
    private ObjectQueue<Player> playerQueue;

    private Guild guild;
    private Category voiceCategory;
    private VoiceChannel lobbyVoiceChannel;
    private LinkedAccountCache linkedAccountCache;

    public VoiceModule(DisVoice disVoice) {

        this.disVoice = disVoice;
        this.pluginConfig = this.disVoice.getPluginConfig();
        this.database = this.disVoice.getDatabase();
        this.discord = this.disVoice.getDiscord();

        this.voiceChannels = Lists.newArrayList();
        this.playerQueue = new ObjectQueue<>();
        this.linkedAccountCache = this.disVoice.getLinkedAccountCache();
    }

    public void initialize() {
        this.guild = this.discord.getJda().getGuildById(this.pluginConfig.getObject("discord.guildId")
                .toString());
        this.voiceCategory = this.guild.getCategoryById(this.pluginConfig.getObject("discord.voiceCategoryId").toString());
        this.lobbyVoiceChannel = this.guild.getVoiceChannelById(this.pluginConfig.getObject("discord.lobbyChannelId")
                .toString());
    }

    public void shutdown() {
        this.discord.getJda().shutdownNow();
    }

    public List<VoiceChatData> getVoiceChatData(VoiceChannel voiceChannel) {
        return this.voiceChannels.stream().filter(chatData -> chatData.getVoiceChannel().equals(voiceChannel))
                .collect(Collectors.toList());
    }

    public boolean existsVoiceChatData(VoiceChannel voiceChannel) {
        return this.voiceChannels.stream().anyMatch(chatData -> chatData.getVoiceChannel().equals(voiceChannel));
    }

    public boolean existsVoiceChatData(Player player) {
        return this.voiceChannels.stream().anyMatch(chatData -> chatData.getPlayer().
                getUniqueId().toString().equals(player.getUniqueId().toString()));
    }

    public VoiceChatData getVoiceChatData(Player player) {
        return this.voiceChannels.stream().filter(chatData -> chatData.getPlayer().equals(player))
                .findFirst().get();
    }

    public void start() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this.disVoice, () -> {

            Bukkit.getOnlinePlayers().stream().filter(player -> !this.playerQueue.contains(player))
                    .forEach(player -> this.playerQueue.add(player));

            if(this.playerQueue.size() == 0)
                return;

            Player player = this.playerQueue.getAndRemove();

            this.updatePlayerVoiceChannel(player);

        }, 40, 10);
    }

    public void updatePlayerVoiceChannel(Player player) {
        if(this.guild == null)
            return;
        if (!this.linkedAccountCache.existLinkedAccount(player.getUniqueId()))
            return;
        String id = this.linkedAccountCache.getLinkedAccount(player.getUniqueId())
                .discordId();
        Member member = this.guild.getMemberById(id);
        if (member == null)
            return;
        if (member.getVoiceState() == null)
            return;
        if (!member.getVoiceState().inVoiceChannel())
            return;
        if (!member.getVoiceState().getChannel().getParent().equals(this.voiceCategory))
            return;
        /**
         * Search for players in near
         */
        List<Player> nearByPlayers = Bukkit.getOnlinePlayers()
                .stream().filter(p -> !p.getName().equals(player.getName()))
                .filter(p -> p.getLocation().distance(player.getLocation()) < 8)
                .filter(p -> this.disVoice.getLinkedAccountCache().existLinkedAccount(p.getUniqueId()))
                .filter(this::voiceChatActive)
                .collect(Collectors.toList());

        /**
         * If there is no player in near
         */
        if (nearByPlayers.size() == 0) {
            if(!member.getVoiceState().getChannel().equals(this.lobbyVoiceChannel)) {
                VoiceChannel voiceChannel = member.getVoiceState().getChannel();
                this.guild.moveVoiceMember(member, this.lobbyVoiceChannel).queue();
                if(existsVoiceChatData(player)) {
                    VoiceChatData voiceChatData = getVoiceChatData(player);
                    this.voiceChannels.remove(voiceChatData);
                }
                if(getVoiceChatData(voiceChannel).size() == 1) {
                    if(existsVoiceChatData(voiceChannel))
                        getVoiceChatData(voiceChannel).forEach(this.voiceChannels::remove);
                    voiceChannel.getMembers().forEach(member1 -> this.guild.moveVoiceMember(member1, this.lobbyVoiceChannel)
                            .queue());
                    Bukkit.getScheduler().runTaskLater(this.disVoice, () -> {
                        voiceChannel.delete().queue();
                    }, 60);
                }
            }
            return;
        }

        /**
         * Check if player is a voiceChannel currently
         */
        if(existsVoiceChatData(player))
            return;

        /**
         * if players nearby is greater than 0
         */
        if (nearByPlayers.size() > 0 && !member.getVoiceState().getChannel().equals(this.lobbyVoiceChannel))
            return;

        /**
         * Search if a player is in a channel
         */
        Optional<Player> optionalPlayer = nearByPlayers
                .stream().filter(this::existsVoiceChatData)
                .findFirst();

        /**
         * Check if a player is in a channel
         */
        if(optionalPlayer.isPresent()) {
            /**
             * If someone is already connected then join him
             */
            VoiceChatData voiceChatData = getVoiceChatData(optionalPlayer.get());
            this.voiceChannels.add(new VoiceChatData(player, voiceChatData.getVoiceChannel()));
            this.guild.moveVoiceMember(member, voiceChatData.getVoiceChannel()).queue();
        } else {
            /**
             * If nobody is connected to a voice Channel than create one and move them
             */
            VoiceChannel voiceChannel =
                    this.voiceCategory.createVoiceChannel(UUID.randomUUID().toString())
                            .addPermissionOverride(this.guild.getPublicRole(),
                                    Lists.newArrayList(Permission.VOICE_SPEAK),
                                    Lists.newArrayList(Permission.VIEW_CHANNEL)).complete();
            this.voiceChannels.add(new VoiceChatData(player, voiceChannel));
            guild.moveVoiceMember(member, voiceChannel).queue();
            nearByPlayers.forEach(p -> {
                Member playerMemeber = this.guild.getMemberById(this.linkedAccountCache
                        .getLinkedAccount(p.getUniqueId()).discordId());
                this.voiceChannels.add(new VoiceChatData(p, voiceChannel));
                this.guild.moveVoiceMember(playerMemeber, voiceChannel).queue();
            });
        }
    }

    private boolean voiceChatActive(Player player) {
        if(this.linkedAccountCache.existLinkedAccount(player.getUniqueId())) {
            Member playerMemeber = this.guild.getMemberById(this.linkedAccountCache
                    .getLinkedAccount(player.getUniqueId()).discordId());
            if(playerMemeber.getVoiceState() == null)
                return false;
            if(!playerMemeber.getVoiceState().inVoiceChannel())
                return false;
            if(!playerMemeber.getVoiceState().getChannel().getParent().equals(this.voiceCategory))
                return false;
            return true;
        }
        return false;
    }

    public Guild getGuild() {
        return guild;
    }

    public Category getVoiceCategory() {
        return voiceCategory;
    }

    public VoiceChannel getLobbyVoiceChannel() {
        return lobbyVoiceChannel;
    }
}
