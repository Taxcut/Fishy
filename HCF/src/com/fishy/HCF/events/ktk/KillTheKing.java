package com.fishy.hcf.events.ktk;

import com.fishy.hcf.HCF;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@Data @AllArgsConstructor
public class KillTheKing {

    private UUID king;
    private long started;

    public void initKiller(Player player) {
        for (String s : HCF.getPlugin().getConfig().getStringList("EVENT.KILL_THE_KING_COMMANDS")) {
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), s.replace("%player%", player.getName()));
        }
    }
}
