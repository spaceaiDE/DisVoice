package de.spaceai.disvoice.discord;

import de.spaceai.disvoice.DisVoice;
import de.spaceai.disvoice.discord.listener.MessageListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import javax.security.auth.login.LoginException;

public class Discord {

    private final String token;
    private final DisVoice disVoice;
    private final JDABuilder jdaBuilder;
    private JDA jda;

    public Discord(DisVoice disVoice, String token) {

        this.disVoice = disVoice;
        this.token = token;

        this.jdaBuilder = JDABuilder.createDefault(this.token);
        this.jdaBuilder.addEventListeners(new MessageListener(this.disVoice));

    }

    public void connect() {
        try {
            this.jda = this.jdaBuilder.build();
        } catch (LoginException e) {
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
