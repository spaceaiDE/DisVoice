package de.spaceai.disvoice.command;

import de.spaceai.disvoice.DisVoice;
import de.spaceai.disvoice.database.account.LinkedAccount;
import de.spaceai.disvoice.verification.VerificationCache;
import net.dv8tion.jda.api.entities.User;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UnlinkCommand implements CommandExecutor {

    private final DisVoice disVoice;
    private final VerificationCache verificationCache;

    public UnlinkCommand(DisVoice disVoice) {
        this.disVoice = disVoice;
        this.verificationCache = this.disVoice.getVerificationCache();
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        if(!(commandSender instanceof Player))
            return true;

        Player player = (Player) commandSender;

        if(args.length == 0) {
            if(!this.disVoice.getDatabase().hasElement("linkedAccounts", "uuid", player.getUniqueId().toString())) {
                player.sendMessage("§c§oIm sorry but you are not linked with discord");
                return true;
            }
            LinkedAccount linkedAccount = this.disVoice.getLinkedAccountCache().getLinkedAccount(player.getUniqueId());
            this.disVoice.getLinkedAccountCache().removeLinkedAccount(linkedAccount);
            this.disVoice.getDatabase().update("DELETE FROM linkedAccounts WHERE uuid='"+player.getUniqueId().toString()+"'");
            player.sendMessage("§a§oSucessfully unlinked your account!");
        } else {
            player.sendMessage("§c§oPlease use /unlink");
        }

        return false;
    }

}
