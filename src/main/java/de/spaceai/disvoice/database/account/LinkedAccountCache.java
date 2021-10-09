package de.spaceai.disvoice.database.account;

import com.google.common.collect.Lists;
import de.spaceai.disvoice.DisVoice;
import de.spaceai.disvoice.logging.LogPriority;

import java.sql.ResultSet;
import java.util.List;
import java.util.UUID;

public class LinkedAccountCache {

    private List<LinkedAccount> linkedAccounts;

    private final DisVoice disVoice;

    public LinkedAccountCache(DisVoice disVoice) {
        this.disVoice = disVoice;
        this.linkedAccounts = Lists.newLinkedList();
    }

    public void refresh() {
        this.linkedAccounts.clear();
        this.load();
    }

    public void load() {
        ResultSet resultSet = this.disVoice.getDatabase().get("SELECT * FROM linkedAccounts");
        try {
            while (resultSet.next()) {
                this.addLinkedAccount(new LinkedAccount(UUID.fromString(resultSet.getString("uuid")
                ), resultSet.getString("discordId")));
            }
        } catch (Exception e) {}

        disVoice.getLog().log(LogPriority.DEBUG, "Loaded " + this.linkedAccounts.size() + " Linked Accounts");
    }

    public void addLinkedAccount(LinkedAccount linkedAccount) {
        this.linkedAccounts.add(linkedAccount);
    }

    public void removeLinkedAccount(LinkedAccount linkedAccount) {
        this.linkedAccounts.remove(linkedAccount);
    }

    public boolean existLinkedAccount(UUID uuid) {
        return this.linkedAccounts.stream().anyMatch(linkedAccount -> linkedAccount.uniqueId().equals(uuid));
    }

    public boolean existLinkedAccount(String discordId) {
        return this.linkedAccounts.stream().anyMatch(linkedAccount -> linkedAccount.discordId().equals(discordId));
    }

    public LinkedAccount getLinkedAccount(String discordId) {
        return this.linkedAccounts.stream().filter(linkedAccount -> linkedAccount.discordId().equals(discordId))
                .findFirst().get();
    }

    public LinkedAccount getLinkedAccount(UUID uuid) {
        return this.linkedAccounts.stream().filter(linkedAccount -> linkedAccount.uniqueId().equals(uuid))
                .findFirst().get();
    }

}
