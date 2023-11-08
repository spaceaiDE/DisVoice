package de.spaceai.disvoice.discord.listener;

import de.spaceai.disvoice.DisVoice;
import de.spaceai.disvoice.database.account.LinkedAccount;
import de.spaceai.disvoice.verification.PendingVerification;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class MessageListener extends ListenerAdapter {

    private final DisVoice disVoice;

    public MessageListener(DisVoice disVoice) {
        this.disVoice = disVoice;
    }


    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if(event.getAuthor().isBot())
            return;
        String content = event.getMessage().getContentRaw();
        if(!this.disVoice.getVerificationCache().existPendingVerification(content)) {
            event.getChannel().sendMessage("Hey! I could not find this code. Please try again!")
                    .queue();
            return;
        }

        PendingVerification pendingVerification = this.disVoice.getVerificationCache().getPendingVerification(content);

        event.getChannel().sendMessage("Success! You are now linked to "+
                        pendingVerification.getPlayer().getName()+" ( "+pendingVerification.getPlayer().getUniqueId().toString()+" )")
                .queue();

        this.disVoice.getVerificationCache().removePendingVerification(pendingVerification);

        this.disVoice.getLinkedAccountCache().addLinkedAccount(new LinkedAccount(pendingVerification.getPlayer().getUniqueId(),
                event.getAuthor().getId()));

        this.disVoice.getDatabase().update("INSERT INTO linkedAccounts(uuid, discordId) VALUES ('"+
                pendingVerification.getPlayer().getUniqueId().toString() +"', '" + event.getAuthor().getId() + "')");
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        this.disVoice.getVoiceModule().initialize();
        this.disVoice.getVoiceModule().start();
    }
}
