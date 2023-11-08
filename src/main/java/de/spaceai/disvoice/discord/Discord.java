package de.spaceai.disvoice.discord;

import de.spaceai.disvoice.DisVoice;
import de.spaceai.disvoice.discord.listener.MessageListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.security.auth.login.LoginException;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Discord {

    private final String token;
    private final DisVoice disVoice;
    private final JDABuilder jdaBuilder;
    private JDA jda;

    public Discord(DisVoice disVoice, String token) {

        this.disVoice = disVoice;
        this.token = token;

        this.jdaBuilder = JDABuilder.createDefault(this.token)
                .enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_VOICE_STATES);
        this.jdaBuilder.addEventListeners(new MessageListener(this.disVoice));

    }

    public void connect() {
        try {
            this.jda = this.jdaBuilder.build();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public JDABuilder getJdaBuilder() {
        return jdaBuilder;
    }

    public JDA getJda() {
        return jda;
    }
}
