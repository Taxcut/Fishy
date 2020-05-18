package com.fishy.hcf.events.conquest;

import com.fishy.hcf.HCF;
import com.fishy.hcf.util.base.command.ArgumentExecutor;

public class ConquestExecutor extends ArgumentExecutor {

    public ConquestExecutor(HCF plugin) {
        super("conquest");
        addArgument(new ConquestSetpointsArgument(plugin));
    }
}
