package de.spaceai.disvoice.command;

import de.spaceai.disvoice.DisVoice;
import de.spaceai.disvoice.verification.VerificationCache;
import net.dv8tion.jda.api.entities.User;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LinkCommand implements CommandExecutor {

    private final DisVoice disVoice;
    private final VerificationCache verificationCache;

    public LinkCommand(DisVoice disVoice) {
        this.disVoice = disVoice;
        this.verificationCache = this.disVoice.getVerificationCache();
    }

    @Override
    public boolean onCommand( CommandSender commandSender, Command command, String s, String[] args) {

        if(!(commandSender instanceof Player))
            return true;

        Player player = (Player) commandSender;

        if(args.length == 0) {
            if(this.disVoice.getDatabase().hasElement("linkedAccounts", "uuid", player.getUniqueId().toString())) {
                player.sendMessage("§c§oIm sorry but you are already link with discord");
                return true;
            }

            if(this.verificationCache.existPendingVerification(player)) {
                player.sendMessage("§c§oIm sorry but you already requested a verification code");
                return true;
            }

            String code = this.verificationCache.addVerification(player);

            player.sendMessage("§7§oYour Verification Code is §a§o" + code);
        } else if(args.length == 1 && args[0].equalsIgnoreCase("info")) {
            if(!this.disVoice.getDatabase().hasElement("linkedAccounts", "uuid", player.getUniqueId().toString())) {
                player.sendMessage("§c§oIm sorry but you're not linked with discord");
                return true;
            }
            String id = this.disVoice.getDatabase().getSingleElement("SELECT * FROM linkedAccounts WHERE uuid='"+
                    player.getUniqueId().toString()+"'", "discordId").toString();
            User user = this.disVoice.getDiscord().getJda().getUserById(id);
            if(user == null) {
                player.sendMessage("§c§oIm sorry but i didnt find you linked account. Try again later!");
                return true;
            }
            player.sendMessage("§7§oYou're connected with: §a§o"+user.getAsTag());
        } else {
            player.sendMessage("§c§oPlease use /link");
        }

        return false;
    }
}
