package com.fishy.hcf.economy;

import gnu.trove.map.TObjectIntMap;

import java.util.UUID;

public interface EconomyManager {

    char ECONOMY_SYMBOL = '$';
    TObjectIntMap<UUID> getBalanceMap();
    int getBalance(UUID uuid);
    int setBalance(UUID uuid, int amount);
    int addBalance(UUID uuid, int amount);
    int subtractBalance(UUID uuid, int amount);
    void reloadEconomyData();
    void saveEconomyData();
}
