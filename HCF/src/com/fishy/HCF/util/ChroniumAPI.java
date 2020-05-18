package com.fishy.hcf.util;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.hcrival.chronium.profile.Profile;
import com.hcrival.chronium.rank.Rank;



public class ChroniumAPI {

	public static String getColorOfPlayer(Player player) {
		Profile profile = Profile.getProfiles().get(player.getUniqueId());
		return profile == null ? "&f" : profile.getActiveRank().getColor();
	}

	public static String getColoredName(Player player) {
		Profile profile = Profile.getProfiles().get(player.getUniqueId());
		return CC.translate((profile == null ? ChatColor.WHITE : profile.getActiveRank().getColor()) + player.getName());
	}

	public static Rank getRankOfPlayer(Player player) {
		Profile profile = Profile.getProfiles().get(player.getUniqueId());
		return profile == null ? Rank.getDefaultRank() : profile.getActiveRank();
	}

	public static boolean isInStaffMode(Player player) {
		Profile profile = Profile.getProfiles().get(player.getUniqueId());
		return profile != null && player.hasPermission("core.staff") && profile.getStaffOptions().staffModeEnabled();
	}

}
