package com.fishy.hcf.faction.struct;

public interface Raidable {
    boolean isRaidable();
    double getDeathsUntilRaidable();
    double getMaximumDeathsUntilRaidable();
    double setDeathsUntilRaidable(double deathsUntilRaidable);
    long getRemainingRegenerationTime();
    void setRemainingRegenerationTime(long millis);
    RegenStatus getRegenStatus();
}
