package com.fishy.hcf.faction;

import com.fishy.hcf.faction.argument.staff.*;
import com.fishy.hcf.util.base.command.ArgumentExecutor;
import com.fishy.hcf.util.base.command.CommandArgument;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.fishy.hcf.HCF;
import com.fishy.hcf.faction.argument.FactionAcceptArgument;
import com.fishy.hcf.faction.argument.FactionAllyArgument;
import com.fishy.hcf.faction.argument.FactionAnnouncementArgument;
import com.fishy.hcf.faction.argument.FactionChatArgument;
import com.fishy.hcf.faction.argument.FactionClaimArgument;
import com.fishy.hcf.faction.argument.FactionClaimChunkArgument;
import com.fishy.hcf.faction.argument.FactionClaimsArgument;
import com.fishy.hcf.faction.argument.FactionCreateArgument;
import com.fishy.hcf.faction.argument.FactionDemoteArgument;
import com.fishy.hcf.faction.argument.FactionDepositArgument;
import com.fishy.hcf.faction.argument.FactionDisbandArgument;
import com.fishy.hcf.faction.argument.FactionHelpArgument;
import com.fishy.hcf.faction.argument.FactionHomeArgument;
import com.fishy.hcf.faction.argument.FactionInviteArgument;
import com.fishy.hcf.faction.argument.FactionInvitesArgument;
import com.fishy.hcf.faction.argument.FactionKickArgument;
import com.fishy.hcf.faction.argument.FactionLeaderArgument;
import com.fishy.hcf.faction.argument.FactionLeaveArgument;
import com.fishy.hcf.faction.argument.FactionListArgument;
import com.fishy.hcf.faction.argument.FactionMapArgument;
import com.fishy.hcf.faction.argument.FactionMessageArgument;
import com.fishy.hcf.faction.argument.FactionOpenArgument;
import com.fishy.hcf.faction.argument.FactionPromoteArgument;
import com.fishy.hcf.faction.argument.FactionRallyArgument;
import com.fishy.hcf.faction.argument.FactionRenameArgument;
import com.fishy.hcf.faction.argument.FactionSetHomeArgument;
import com.fishy.hcf.faction.argument.FactionShowArgument;
import com.fishy.hcf.faction.argument.FactionStuckArgument;
import com.fishy.hcf.faction.argument.FactionSubclaimArgumentExecutor;
import com.fishy.hcf.faction.argument.FactionTopArgument;
import com.fishy.hcf.faction.argument.FactionUnallyArgument;
import com.fishy.hcf.faction.argument.FactionUnclaimArgument;
import com.fishy.hcf.faction.argument.FactionUninviteArgument;
import com.fishy.hcf.faction.argument.FactionUnsubclaimArgument;
import com.fishy.hcf.faction.argument.FactionWithdrawArgument;

/**
 * Class to handle the command and tab completion for the faction command.
 */
public class FactionExecutor extends ArgumentExecutor {

    private final CommandArgument helpArgument;

    public FactionExecutor(HCF plugin) {
        super("faction");

        addArgument(new FactionAcceptArgument(plugin));
        addArgument(new FactionRallyArgument(plugin));
        addArgument(new FactionAllyArgument(plugin));
        addArgument(new FactionAnnouncementArgument(plugin));
        //addArgument(new FactionBuildArgument(plugin));
        addArgument(new FactionChatArgument(plugin));
        addArgument(new FactionChatSpyArgument(plugin));
        addArgument(new FactionClaimArgument(plugin));
        addArgument(new FactionClaimChunkArgument(plugin));
        addArgument(new FactionClaimForArgument(plugin));
        addArgument(new FactionClaimsArgument(plugin));
        addArgument(new FactionClearClaimsArgument(plugin));
        addArgument(new FactionCreateArgument(plugin));
        addArgument(new FactionDemoteArgument(plugin));
        addArgument(new FactionDepositArgument(plugin));
        addArgument(new FactionDisbandArgument(plugin));
        addArgument(new FactionSetDtrRegenArgument(plugin));
        addArgument(new FactionForceDemoteArgument(plugin));
        addArgument(new FactionForceJoinArgument(plugin));
        addArgument(new FactionForceKickArgument(plugin));
        addArgument(new FactionForceLeaderArgument(plugin));
        addArgument(new FactionForcePromoteArgument(plugin));
        addArgument(new FactionForceUnclaimHereArgument(plugin));
        addArgument(helpArgument = new FactionHelpArgument(this));
        addArgument(new FactionHomeArgument(this, plugin));
        addArgument(new FactionInviteArgument(plugin));
        addArgument(new FactionInvitesArgument(plugin));
        addArgument(new FactionKickArgument(plugin));
        addArgument(new FactionLeaderArgument(plugin));
        addArgument(new FactionLeaveArgument(plugin));
        addArgument(new FactionListArgument(plugin));
        addArgument(new FactionMapArgument(plugin));
        addArgument(new FactionMessageArgument(plugin));
        addArgument(new FactionMuteArgument(plugin));
        addArgument(new FactionOpenArgument(plugin));
        addArgument(new FactionRemoveArgument(plugin));
        addArgument(new FactionRenameArgument(plugin));
        addArgument(new FactionPromoteArgument(plugin));
        addArgument(new FactionSetDtrArgument(plugin));
        addArgument(new FactionSetDeathbanMultiplierArgument(plugin));
        addArgument(new FactionSetHomeArgument(plugin));
        addArgument(new FactionShowArgument(plugin));
        addArgument(new FactionStuckArgument(plugin));
        addArgument(new FactionSubclaimArgumentExecutor(plugin));
        addArgument(new FactionUnclaimArgument(plugin));
        addArgument(new FactionUnallyArgument(plugin));
        addArgument(new FactionUninviteArgument(plugin));
        addArgument(new FactionUnsubclaimArgument(plugin));
        addArgument(new FactionWithdrawArgument(plugin));
        addArgument(new FactionTopArgument(plugin));
        addArgument(new FactionAddPointsArgument(plugin));
        addArgument(new FactionsRemovePointsArgument(plugin));
        addArgument(new FactionsSetPointsArgument(plugin));
        addArgument(new FactionBanArgument(plugin));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            helpArgument.onCommand(sender, command, label, args);
            return true;
        }

        CommandArgument argument = getArgument(args[0]);
        if (argument != null) {
            String permission = argument.getPermission();
            if (permission == null || sender.hasPermission(permission)) {
                argument.onCommand(sender, command, label, args);
                return true;
            }
        }

        helpArgument.onCommand(sender, command, label, args);
        return true;
    }
}
