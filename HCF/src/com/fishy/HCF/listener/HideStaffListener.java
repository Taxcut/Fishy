package com.fishy.hcf.listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.fishy.hcf.HCF;

public class HideStaffListener {
	public static List<Player> staff;
	public static HashMap<Player, Boolean> showStaff;

	static {
		HideStaffListener.staff = new ArrayList<>();
		HideStaffListener.showStaff = new HashMap<>();
	}

	public static void getStaff() {
		for (Player players : Bukkit.getOnlinePlayers()) {
			if (HCF.getPlugin().getStaffModeListener().isStaffModeActive(players)) {
				HideStaffListener.staff.add(players);
			}
		}
	}

	public static boolean showStaffEnabled(Player player) {
		return !HideStaffListener.showStaff.containsKey(player) || (HideStaffListener.showStaff.get(player) && (!HideStaffListener.showStaff.get(player) || true));
	}
}
