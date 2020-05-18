package com.fishy.hcf.listener;

public enum KillStreaks
{
    GoldenApple("3x Golden Apple", 3, "give name 322 3"), 
    Poison("1x Poison Potion", 5, "give name 373:16388 1"), 
    Slowness("1x Slowness Potion", 10, "give name 373:16426 1"),
    Invis("1x Invis Potion", 15, "give name 373:16430 1"), 
    GodApple("1x God Apple", 20, "give name 322:1 1"), 
    Strength("Strength II", 25, "effect name INCREASE_DAMAGE 10 1");
    
    public String name;
    public Integer kills;
    public String command;
    
    private KillStreaks(final String name, final Integer kills, final String command) {
        this.kills = kills;
        this.name = name;
        this.command = command;
    }
}