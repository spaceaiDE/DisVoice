package de.spaceai.disvoice.verification;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import de.spaceai.disvoice.DisVoice;
import org.bukkit.entity.Player;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class VerificationCache {

    private Cache<UUID, PendingVerification> pendingVerificationCache;
    private final DisVoice disVoice;

    public VerificationCache(DisVoice disVoice) {
        this.disVoice = disVoice;
        this.pendingVerificationCache = CacheBuilder.newBuilder().expireAfterWrite(20, TimeUnit.SECONDS)
                .build();
    }

    public String addVerification(Player player) {
        char[] chars = "abcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
        StringBuilder code = new StringBuilder();
        for(int length = 0; length < 5; length++)
            code.append(chars[new Random().nextInt(chars.length)]);
        this.pendingVerificationCache.put(player.getUniqueId(), new PendingVerification(player, code.toString()));
        return code.toString();
    }

    public void addPendingVerification(PendingVerification pendingVerification) {
        this.pendingVerificationCache.put(pendingVerification.getPlayer().getUniqueId(), pendingVerification);
    }

    public PendingVerification getPendingVerification(String code) {
        return this.pendingVerificationCache.asMap().values().stream().filter(pendingVerification -> pendingVerification.getCode().equals(code))
                .findFirst().get();
    }

    public void removePendingVerification(PendingVerification pendingVerification) {
        this.pendingVerificationCache.invalidate(pendingVerification);
    }

    public PendingVerification getPendingVerification(Player player) {
        return this.pendingVerificationCache.getIfPresent(player.getUniqueId());
    }

    public boolean existPendingVerification(String code) {
        return this.pendingVerificationCache.asMap().values().stream().anyMatch(pendingVerification -> pendingVerification.getCode().equals(code));
    }

    public boolean existPendingVerification(Player player) {
        return this.pendingVerificationCache.asMap().containsKey(player.getUniqueId());
    }

}
