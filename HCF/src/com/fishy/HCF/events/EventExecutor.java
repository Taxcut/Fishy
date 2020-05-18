package com.fishy.hcf.events;

import com.fishy.hcf.HCF;
import com.fishy.hcf.events.argument.EventCancelArgument;
import com.fishy.hcf.events.argument.EventCreateArgument;
import com.fishy.hcf.events.argument.EventDeleteArgument;
import com.fishy.hcf.events.argument.EventRenameArgument;
import com.fishy.hcf.events.argument.EventSetAreaArgument;
import com.fishy.hcf.events.argument.EventSetCapDelayArgument;
import com.fishy.hcf.events.argument.EventSetCapzoneArgument;
import com.fishy.hcf.events.argument.EventStartArgument;
import com.fishy.hcf.events.argument.EventUptimeArgument;
import com.fishy.hcf.util.base.command.ArgumentExecutor;

public class EventExecutor extends ArgumentExecutor {

    public EventExecutor(HCF plugin) {
        super("event");

        addArgument(new EventCancelArgument(plugin));
        addArgument(new EventCreateArgument(plugin));
        addArgument(new EventDeleteArgument(plugin));
        addArgument(new EventRenameArgument(plugin));
        addArgument(new EventSetAreaArgument(plugin));
        addArgument(new EventSetCapzoneArgument(plugin));
        addArgument(new EventStartArgument(plugin));
        addArgument(new EventSetCapDelayArgument(plugin));
        addArgument(new EventUptimeArgument(plugin));
    }
}