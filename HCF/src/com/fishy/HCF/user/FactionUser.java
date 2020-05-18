package com.fishy.hcf.user;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import com.fishy.hcf.deathban.Deathban;
import com.fishy.hcf.kit.Kit;
import com.fishy.hcf.util.base.GenericUtils;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.gnu.trove.map.TObjectIntMap;
import net.minecraft.util.gnu.trove.map.TObjectLongMap;
import net.minecraft.util.gnu.trove.map.hash.TObjectIntHashMap;
import net.minecraft.util.gnu.trove.map.hash.TObjectLongHashMap;
import net.minecraft.util.gnu.trove.procedure.TObjectIntProcedure;
import net.minecraft.util.gnu.trove.procedure.TObjectLongProcedure;

@Getter
@Setter
public class FactionUser implements ConfigurationSerializable {

    private Set<UUID> factionChatSpying = new HashSet<>();
    private final TObjectIntMap<UUID> kitUseMap;
    private final TObjectLongMap<UUID> kitCooldownMap;
    private UUID userUUID;
    
    private ChatColor chatcolor;
    private boolean capzoneEntryAlerts;
    private boolean showClaimMap;
    private boolean showLightning = true;
    private boolean messagingSounds = true;
    private boolean messagesVisible = true;
    private Deathban deathban;
    private long lastFactionLeaveMillis;
    private int kills;
    private int balance;
    private int deaths;
    private boolean isSOTW;
    private long playtime = 0;
    private String nick;
    private List<Deathban> deathbanHistory = new ArrayList<>();
    
    private UUID lastRepliedTo;
    private long lastSpeakTimeMillis;
    private long lastReceivedMessageMillis;
    private long lastSentMessageMillis;

    public FactionUser(UUID userUUID) {
        this.userUUID = userUUID;
        this.kitUseMap = new TObjectIntHashMap<>();
        this.kitCooldownMap = new TObjectLongHashMap<>();
        this.isSOTW = true;
        deathbanHistory = new ArrayList<>();
    }

    public FactionUser(Map<String, Object> map) {
        this.kitUseMap = new TObjectIntHashMap<>();
        this.kitCooldownMap = new TObjectLongHashMap<>();
        this.factionChatSpying.addAll(GenericUtils.createList(map.get("faction-chat-spying"), String.class).stream().map(UUID::fromString).collect(Collectors.toList()));
        this.userUUID = UUID.fromString((String) map.get("userUUID"));
        this.capzoneEntryAlerts = (Boolean) map.get("capzoneEntryAlerts");
        this.showLightning = (Boolean) map.get("showLightning");
        this.deathban = (Deathban) map.get("deathban");
        this.lastFactionLeaveMillis = Long.parseLong((String) map.get("lastFactionLeaveMillis"));
        this.kills = (Integer) map.get("kills");
        this.deaths = (Integer) map.get("deaths");
        this.balance = (Integer) map.get("balance");
        this.messagingSounds = (Boolean) map.get("messagingSounds");
        this.messagesVisible = (Boolean) map.get("messages");
        this.isSOTW = (Boolean) map.get("issotw");
        this.nick = (String) map.get("nick");
        this.deathbanHistory = GenericUtils.createList(map.getOrDefault("deathbanHistory", Collections.emptyList()), Deathban.class);
        Object object = map.get("lastRepliedTo");
        if (object instanceof String) {
            this.lastRepliedTo = UUID.fromString((String) object);
        }
        if ((object = map.get("lastSpeakTimeMillis")) instanceof String) {
            this.lastSpeakTimeMillis = Long.parseLong((String) object);
        }
        if ((object = map.get("lastReceivedMessageMillis")) instanceof String) {
            this.lastReceivedMessageMillis = Long.parseLong((String) object);
        }
        if ((object = map.get("lastSentMessageMillis")) instanceof String) {
            this.lastSentMessageMillis = Long.parseLong((String) object);
        }
        for (final Map.Entry<String, Integer> entry : GenericUtils.castMap(map.get("kit-use-map"), String.class, Integer.class).entrySet()) {
            this.kitUseMap.put(UUID.fromString(entry.getKey()), entry.getValue());
        }
        for (final Map.Entry<String, String> entry2 : GenericUtils.castMap(map.get("kit-cooldown-map"), String.class, String.class).entrySet()) {
            this.kitCooldownMap.put(UUID.fromString(entry2.getKey()), Long.parseLong(entry2.getValue()));
        }
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("faction-chat-spying", factionChatSpying.stream().map(UUID::toString).collect(Collectors.toList()));
        map.put("userUUID", userUUID.toString());
        map.put("capzoneEntryAlerts", capzoneEntryAlerts);
        map.put("showClaimMap", showClaimMap);
        map.put("showLightning", showLightning);
        map.put("balance", balance);
        map.put("deathban", deathban);
        map.put("lastFactionLeaveMillis", Long.toString(lastFactionLeaveMillis));
        map.put("kills", kills);
        map.put("deaths", deaths);
        map.put("nick", nick);
        map.put("deathbanHistory", deathbanHistory);
        map.put("issotw", isSOTW);
        map.put("messagingSounds", this.messagingSounds);
        map.put("messages", this.messagesVisible);
        if (this.lastRepliedTo != null) {
            map.put("lastRepliedTo", this.lastRepliedTo.toString());
        }
        map.put("lastSpeakTimeMillis", Long.toString(this.lastSpeakTimeMillis));
        map.put("lastReceivedMessageMillis", Long.toString(this.lastReceivedMessageMillis));
        map.put("lastSentMessageMillis", Long.toString(this.lastSentMessageMillis));

        final Map<String, Integer> kitUseSaveMap = new HashMap<>(this.kitUseMap.size());
        this.kitUseMap.forEachEntry((uuid, value) -> {
            kitUseSaveMap.put(uuid.toString(), value);
            return true;
        });
        new TObjectIntProcedure<UUID>() {
            @Override
            public boolean execute(final UUID uuid, final int value) {
                kitUseSaveMap.put(uuid.toString(), value);
                return true;
            }
        };
        final Map<String, String> kitCooldownSaveMap = new HashMap<>(this.kitCooldownMap.size());
        this.kitCooldownMap.forEachEntry((uuid, value) -> {
            kitCooldownSaveMap.put(uuid.toString(), Long.toString(value));
            return true;
        });
        new TObjectLongProcedure<UUID>() {
            @Override
            public boolean execute(final UUID uuid, final long value) {
                kitCooldownSaveMap.put(uuid.toString(), Long.toString(value));
                return true;
            }
        };
        map.put("kit-use-map", kitUseSaveMap);
        map.put("kit-cooldown-map", kitCooldownSaveMap);
        return map;
    }

    public void removeDeathban() {
        if (deathban != null) {
            if (deathbanHistory == null) {
                deathbanHistory = new ArrayList<>();
            }
            deathbanHistory.add(deathban);
        }
        this.deathban = null;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(userUUID);
    }
    
    public ChatColor getChatColor() {
    	return chatcolor;
    }
    
    public String getNick() {
    	return this.nick;
    }
    
    public void setNick(String nick) {
    	this.nick = nick;
    }
    
    public void setChatColor(ChatColor cc) {
    	this.chatcolor = cc;
    }
    
    public List<Deathban> getDeathbanHistory() {
        return Collections.synchronizedList(deathbanHistory);
    }

    public void setDeathbanHistory(List<Deathban> deathbanHistory) {
        this.deathbanHistory = new ArrayList<>(deathbanHistory);
    }

    public long getRemainingKitCooldown(Kit kit) {
        long remaining = this.kitCooldownMap.get(kit.getUniqueID());
        if (remaining == this.kitCooldownMap.getNoEntryValue()) {
            return 0L;
        }
        return remaining - System.currentTimeMillis();
    }
    public int getBalance() {
        return balance;
    }

    public int setBalance(int balance) {
        int previous = this.balance;
        this.balance = balance;
        return previous;
    }

    public void updateKitCooldown(Kit kit) {
        this.kitCooldownMap.put(kit.getUniqueID(), System.currentTimeMillis() + kit.getDelayMillis());
    }


    public int getKitUses(Kit kit) {
        int result = this.kitUseMap.get(kit.getUniqueID());
        return (result == this.kitUseMap.getNoEntryValue()) ? 0 : result;
    }

    public int incrementKitUses(Kit kit) {
        return this.kitUseMap.adjustOrPutValue(kit.getUniqueID(), 1, 1);
    }
}
