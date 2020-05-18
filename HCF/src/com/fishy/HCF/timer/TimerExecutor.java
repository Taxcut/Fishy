package com.fishy.hcf.timer;

import com.fishy.hcf.HCF;
import com.fishy.hcf.timer.argument.TimerCheckArgument;
import com.fishy.hcf.timer.argument.TimerSetArgument;
import com.fishy.hcf.util.base.command.ArgumentExecutor;

/**
 * Handles the execution and tab completion of the timer command.
 */
public class TimerExecutor extends ArgumentExecutor {

    public TimerExecutor(HCF plugin) {
        super("timer");

        addArgument(new TimerCheckArgument(plugin));
        addArgument(new TimerSetArgument(plugin));
    }
}