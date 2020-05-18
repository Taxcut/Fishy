package com.fishy.hcf.deathban.lives;

import com.fishy.hcf.HCF;
import com.fishy.hcf.deathban.lives.argument.LivesCheckArgument;
import com.fishy.hcf.deathban.lives.argument.LivesCheckDeathbanArgument;
import com.fishy.hcf.deathban.lives.argument.LivesClearDeathbansArgument;
import com.fishy.hcf.deathban.lives.argument.LivesGiveArgument;
import com.fishy.hcf.deathban.lives.argument.LivesReviveArgument;
import com.fishy.hcf.deathban.lives.argument.LivesSetArgument;
import com.fishy.hcf.util.base.command.ArgumentExecutor;

/**
 * Handles the execution and tab completion of the lives command.
 */
public class LivesExecutor extends ArgumentExecutor {

    public LivesExecutor(HCF plugin) {
        super("lives");

        addArgument(new LivesCheckArgument(plugin));
        addArgument(new LivesCheckDeathbanArgument(plugin));
        addArgument(new LivesClearDeathbansArgument(plugin));
        addArgument(new LivesGiveArgument(plugin));
        addArgument(new LivesReviveArgument(plugin));
        addArgument(new LivesSetArgument(plugin));
    }
}