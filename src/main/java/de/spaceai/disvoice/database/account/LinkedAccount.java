package de.spaceai.disvoice.database.account;

import java.util.UUID;

public class LinkedAccount {

    private UUID uniqueId;
    private String discordId;

    public LinkedAccount(UUID uuid, String discordId) {
        this.uniqueId = uuid;
        this.discordId = discordId;
    }

    public UUID uniqueId() {
        return this.uniqueId;
    }

    public String discordId() {
        return this.discordId;
    }

}
