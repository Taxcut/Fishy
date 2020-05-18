package com.fishy.hcf.events.koth;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.fishy.hcf.HCF;
import com.fishy.hcf.events.koth.argument.KothScheduleArgument;
import com.fishy.hcf.util.base.command.ArgumentExecutor;

public class KothExecutor extends ArgumentExecutor {

    private KothScheduleArgument eventScheduleArgument;

    public KothExecutor(HCF plugin) {
        super("koth");
        addArgument(eventScheduleArgument = new KothScheduleArgument(plugin));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            this.eventScheduleArgument.onCommand(sender, command, label, args);
            return true;
        }

        return super.onCommand(sender, command, label, args);
    }
}
