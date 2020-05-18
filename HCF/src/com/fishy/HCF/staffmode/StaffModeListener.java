package com.fishy.hcf.staffmode;

import com.fishy.hcf.HCF;
import com.fishy.hcf.logger.CombatLogListener;
import com.fishy.hcf.util.CC;
import com.fishy.hcf.util.Color;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.eytril.hide.HideArea;
import org.eytril.hide.HidePlugin;
import org.github.paperspigot.event.server.ServerShutdownEvent;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class StaffModeListener implements Listener {
	private final HCF modmode = HCF.getPlugin();

	public Set<UUID> staffMode = new HashSet<>();
	private final Set<UUID> staffChat = new HashSet<>();
	private final Set<UUID> vanished = new HashSet<>();

	public HashMap<UUID, UUID> inspectedPlayer = new HashMap<>();
	private final HashMap<UUID, ItemStack[]> contents = new HashMap<>();
	private final HashMap<UUID, ItemStack[]> armorContents = new HashMap<>();
	public Inventory inv;
	public Inventory staffInv;

	public StaffModeListener() {
		Bukkit.getPluginManager().registerEvents(this, modmode);
	}

	public Player getInspectedPlayer(Player player) {
		return Bukkit.getServer().getPlayer(inspectedPlayer.get(player.getUniqueId()));
	}

	public boolean isVanished(Player player) {
		return vanished.contains(player.getUniqueId());
	}

	public boolean isStaffChatActive(Player player) {
		return staffChat.contains(player.getUniqueId());
	}

	public boolean isStaffModeActive(Player player) {
		return staffMode.contains(player.getUniqueId());
	}

	public boolean hasPreviousInventory(Player player) {
		return contents.containsKey(player.getUniqueId()) && armorContents.containsKey(player.getUniqueId());
	}

	public void saveInventory(Player player) {
		contents.put(player.getUniqueId(), player.getInventory().getContents());
		armorContents.put(player.getUniqueId(), player.getInventory().getArmorContents());
	}

	public void loadInventory(Player player) {
		PlayerInventory playerInventory = player.getInventory();
		playerInventory.setContents(contents.get(player.getUniqueId()));
		playerInventory.setArmorContents(armorContents.get(player.getUniqueId()));
		contents.remove(player.getUniqueId());
		armorContents.remove(player.getUniqueId());
	}

	public void setStaffMode(Player player, boolean status) {
		if (status) {
			if (player.hasPermission("hcf.command.modmode")) {
				staffMode.add(player.getUniqueId());
				saveInventory(player);
				PlayerInventory playerInventory = player.getInventory();
				playerInventory.setArmorContents(new ItemStack[] { new ItemStack(Material.AIR),
						new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR) });
				playerInventory.clear();
				player.setFireTicks(0);
				player.setFoodLevel(20);
				player.setHealth(player.getMaxHealth());
				player.spigot().setAffectsSpawning(false);
				player.spigot().setCollidesWithEntities(false);
				for (PotionEffect potionEffect : player.getActivePotionEffects()) {
					player.removePotionEffect(potionEffect.getType());
				}
				setItems(player);
				setVanished(player, true);
				player.updateInventory();
				if (player.getGameMode() == GameMode.SURVIVAL) {
					player.setGameMode(GameMode.CREATIVE);
				}
			} else {
				player.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
			}
		} else {
			staffMode.remove(player.getUniqueId());
			PlayerInventory playerInventory = player.getInventory();
			playerInventory.setArmorContents(new ItemStack[] { new ItemStack(Material.AIR), new ItemStack(Material.AIR),
					new ItemStack(Material.AIR), new ItemStack(Material.AIR) });
			playerInventory.clear();
			player.spigot().setViewDistance(Bukkit.getViewDistance());
			player.spigot().setAffectsSpawning(true);
			player.spigot().setCollidesWithEntities(true);
			setVanished(player, false);
			if (hasPreviousInventory(player)) {
				loadInventory(player);
			}
			player.updateInventory();
			player.setGameMode(GameMode.SURVIVAL);
		}
	}

	public void setVanished(Player player, boolean status) {
		if (status) {
			vanished.add(player.getUniqueId());
			for (Player online : Bukkit.getServer().getOnlinePlayers()) {
				if (!HCF.getPlugin().getStaffModeListener().isVanished(online)) {
					online.hidePlayer(player);
				}
			}
			for (Player online : Bukkit.getOnlinePlayers()) {
				for (HideArea area : HidePlugin.getPlugin().getAreaManager().getAreas()) {
					if (!area.isInArea(online.getLocation())
							&& HCF.getPlugin().getStaffModeListener().isVanished(online)) {
						player.showPlayer(online);
					}

					if (HidePlugin.getPlugin().getAreaManager().getAreas().size() == 0) {
						online.showPlayer(player);
					}
				}
			}

			if (isStaffModeActive(player)) {
				PlayerInventory playerInventory = player.getInventory();
				playerInventory.setItem(8, getVanishItemFor(player));
			}
		} else {
			vanished.remove(player.getUniqueId());
			for (Player online : Bukkit.getServer().getOnlinePlayers()) {
				if (HCF.getPlugin().getStaffModeListener().isVanished(online)) {
					player.hidePlayer(online);
				}
				for (HideArea area : HidePlugin.getPlugin().getAreaManager().getAreas()) {
					if (!area.isInArea(player.getLocation())) {
						online.showPlayer(player);
					}
				}
				if (HidePlugin.getPlugin().getAreaManager().getAreas().size() == 0) {
					online.showPlayer(player);
				}
			}

			if (isStaffModeActive(player)) {
				PlayerInventory playerInventory = player.getInventory();
				playerInventory.setItem(8, getVanishItemFor(player));
			}
		}
	}

	public void setItems(Player player) {
		PlayerInventory playerInventory = player.getInventory();

		ItemStack compass = new ItemStack(Material.COMPASS, 1);
		ItemMeta compassMeta = compass.getItemMeta();
		compassMeta.setDisplayName(Color.translate("&5Phase Compass"));
		compassMeta.setLore(Color.translateFromArray(Arrays.asList(
				new String[] { "&7Right click: Move through", "&7Left click: Move to block in line of sight" })));
		compass.setItemMeta(compassMeta);

		ItemStack book = new ItemStack(Material.BOOK, 1);
		ItemMeta bookMeta = book.getItemMeta();
		bookMeta.setDisplayName(Color.translate("&5Examine Player"));
		bookMeta.setLore(
				Color.translateFromArray(Arrays.asList(new String[] { "&7Right click player to inspect inventory" })));
		book.setItemMeta(bookMeta);

		ItemStack freeze = new ItemStack(Material.PACKED_ICE, 1);
		ItemMeta freezeMeta = freeze.getItemMeta();
		freezeMeta.setDisplayName(Color.translate("&5Freeze Player"));
		freezeMeta.setLore(Color
				.translateFromArray(Arrays.asList(new String[] { "&7Right click player to update freeze status" })));
		freeze.setItemMeta(freezeMeta);

		ItemStack carrotStick = new ItemStack(Material.LEASH, 1);
		ItemMeta carrotStickMeta = carrotStick.getItemMeta();
		carrotStickMeta.setDisplayName(Color.translate("&5Follow Player"));
		carrotStickMeta.setLore(
				Color.translateFromArray(Arrays.asList(new String[] { "&7Right click player to follow them" })));
		carrotStick.setItemMeta(carrotStickMeta);

		/*
		 * ItemStack carpet = new ItemStack(Material.WOOD_AXE); ItemMeta carpetMeta =
		 * carpet.getItemMeta();
		 * carpetMeta.setDisplayName(Color.translate("&5World Edit"));
		 * carpetMeta.setLore(Color.translateFromArray(Arrays.asList(new String[] {
		 * "&7The normal worldedit wand" }))); carpet.setItemMeta(carpetMeta);
		 */

		ItemStack staff = new ItemStack(Material.NETHER_STAR);
		ItemMeta staffMeta = staff.getItemMeta();
		staffMeta.setDisplayName(Color.translate("&5Online Staff"));
		staffMeta.setLore(Color.translateFromArray(Arrays.asList(new String[] { "&7See online staff" })));
		staff.setItemMeta(staffMeta);

		ItemStack miner = new ItemStack(Material.BEACON);
		ItemMeta minerMeta = miner.getItemMeta();
		minerMeta.setDisplayName(Color.translate("&5Miner TP"));
		minerMeta.setLore(
				Color.translateFromArray(Arrays.asList(new String[] { "&7Used to get a list of possible xrayers" })));
		miner.setItemMeta(minerMeta);

		ItemStack random = new ItemStack(Material.WATCH, 1);
		ItemMeta randomMeta = random.getItemMeta();
		randomMeta.setDisplayName(Color.translate("&5Random TP"));
		randomMeta.setLore(Color.translateFromArray(Arrays.asList(new String[] {
				"&7Right click to teleport to a random player", "&7Left click to teleport to a random miner" })));
		random.setItemMeta(randomMeta);

		ItemStack carpet = new ItemStack(Material.CARPET, 1);
		ItemMeta carpetMeta = carpet.getItemMeta();
		carpetMeta.setDisplayName(Color.translate("&5Better looking"));
		carpetMeta.setLore(Color.translateFromArray(Arrays.asList(new String[] { "&7Hold to get rid of your hand" })));
		carpet.setItemMeta(carpetMeta);

		playerInventory.setItem(0, compass);
		playerInventory.setItem(7, book);
		// playerInventory.setItem(2, carpet);
		playerInventory.setItem(2, freeze);
		// playerInventory.setItem(6, carrotStick);
		playerInventory.setItem(6, staff);
		playerInventory.setItem(8, getVanishItemFor(player));
		playerInventory.setItem(1, random);
		// playerInventory.setItem(1, miner);
	}

	private ItemStack getVanishItemFor(Player player) {
		ItemStack inkSack = null;
		if (isVanished(player)) {
			inkSack = new ItemStack(Material.INK_SACK, 1, (short) 10);
			ItemMeta inkSackMeta = inkSack.getItemMeta();
			inkSackMeta.setDisplayName(Color.translate("&5Become Visible"));
			inkSackMeta.setLore(Color
					.translateFromArray(Arrays.asList(new String[] { "&7Right click to update your vanish status." })));
			inkSack.setItemMeta(inkSackMeta);
		} else {
			inkSack = new ItemStack(Material.INK_SACK, 1, (short) 8);
			ItemMeta inkSackMeta = inkSack.getItemMeta();
			inkSackMeta.setDisplayName(Color.translate("&5Become Vanished"));
			inkSackMeta.setLore(Color
					.translateFromArray(Arrays.asList(new String[] { "&7Right click to update your vanish status." })));
			inkSack.setItemMeta(inkSackMeta);
		}

		return inkSack;
	}

	public void openInspectionMenu(Player player, Player target) {
		Inventory inventory = Bukkit.getServer().createInventory(null, 9 * 6,
				Color.translate("&5Examining: &f" + target.getName()));

		new BukkitRunnable() {
			@Override
			public void run() {
				inspectedPlayer.put(player.getUniqueId(), target.getUniqueId());

				PlayerInventory playerInventory = target.getInventory();

				/*
				 * ItemStack redstoneTorch = new ItemStack(Material.REDSTONE_TORCH_ON, () ;
				 * ItemMeta redstoneTorchMeta = redstoneTorch.getItemMeta();
				 * redstoneTorchMeta.setDisplayName(Color.translate("&5Clear Inventory"));
				 * redstoneTorch.setItemMeta(redstoneTorchMeta); target.getInventory().clear();
				 */

				ItemStack speckledMelon = new ItemStack(Material.SPECKLED_MELON, (int) target.getHealth());
				ItemMeta speckledMelonMeta = speckledMelon.getItemMeta();
				speckledMelonMeta.setDisplayName(Color.translate("&5Health"));
				speckledMelon.setItemMeta(speckledMelonMeta);

				ItemStack cookedBeef = new ItemStack(Material.COOKED_BEEF, target.getFoodLevel());
				ItemMeta cookedBeefMeta = cookedBeef.getItemMeta();
				cookedBeefMeta.setDisplayName(Color.translate("&5Hunger"));
				cookedBeef.setItemMeta(cookedBeefMeta);

				ItemStack brewingStand = new ItemStack(Material.BREWING_STAND_ITEM,
						target.getPlayer().getActivePotionEffects().size());
				ItemMeta brewingStandMeta = brewingStand.getItemMeta();
				brewingStandMeta.setDisplayName(Color.translate("&5Active Potion Effects"));
				ArrayList<String> brewingStandLore = new ArrayList<>();
				for (PotionEffect potionEffect : target.getPlayer().getActivePotionEffects()) {
					String effectName = potionEffect.getType().getName();
					int effectLevel = potionEffect.getAmplifier();
					effectLevel++;
					brewingStandLore.add(Color.translate(
							"&f" + WordUtils.capitalizeFully(effectName).replace("_", " ") + " " + effectLevel));
				}
				brewingStandMeta.setLore(brewingStandLore);
				brewingStand.setItemMeta(brewingStandMeta);

				ItemStack orangeGlassPane = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7);

				inventory.setContents(playerInventory.getContents());
				inventory.setItem(36, playerInventory.getHelmet());
				inventory.setItem(37, playerInventory.getChestplate());
				inventory.setItem(38, playerInventory.getLeggings());
				inventory.setItem(39, playerInventory.getBoots());
				inventory.setItem(40, playerInventory.getItemInHand());
				for (int i = 41; i <= 46; i++) {
					inventory.setItem(i, orangeGlassPane);
				}
				// inventory.setItem(45, redstoneTorch);
				inventory.setItem(47, speckledMelon);
				inventory.setItem(48, cookedBeef);
				inventory.setItem(49, brewingStand);
				for (int i = 50; i <= 51; i++) {
					inventory.setItem(i, orangeGlassPane);
				}

				if (!player.getOpenInventory().getTitle()
						.equals(Color.translate("&5Examining &f" + target.getName()))) {
					cancel();
					inspectedPlayer.remove(player.getUniqueId());
				}
			}
		}.runTaskTimer(modmode, 0L, 5L);

		player.openInventory(inventory);
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		ItemStack clicked = event.getCurrentItem();
		Inventory inventory = event.getInventory();
		if (event.getClickedInventory() == null) return;
		if (event.getInventory() == null) return;
		if (!clicked.hasItemMeta()) return;
		if (inventory.getName() != null) {
			if (inventory.getName().equals("Miners")) {
				Bukkit.dispatchCommand(player, "tp " + clicked.getItemMeta().getDisplayName());
			}
		}
		if (inventory.getName() != null) {
			if (inventory.getName().equals("Online Staff")) {
				if (!clicked.hasItemMeta()) return;
 				Bukkit.dispatchCommand(player, "tp " + clicked.getItemMeta().getDisplayName());
			}
		}
		if ((inventory.getName() != null)
				&& (inventory.getName().contains(Color.translate("&5Examining &f" + player.getName())))) {
			if (event.getClickedInventory() != null) {
				if (!clicked.hasItemMeta()) return;
				if (isStaffModeActive(player)) {
					event.setCancelled(true);
				}
				if (isStaffModeActive(player)) {
					if (!clicked.hasItemMeta()) return;
					if (event.getSlotType().equals(InventoryType.SlotType.OUTSIDE)) {
						event.setCancelled(true);
						return;
					}
				}

				if (event.getInventory().getTitle().contains(Color.translate("&5Examining"))) {
					if (!clicked.hasItemMeta()) return;
					Player inspected = getInspectedPlayer(player);
					if (event.getRawSlot() == 51) {
						if (inspected != null) {
							if (modmode.getFreezeListener().isFrozen(inspected)) {
								modmode.getFreezeListener().setFreeze(player, inspected, false);
							} else {
								if (inspected.hasPermission("hcf.command.modmode") || inspected.isOp()) {
									player.sendMessage(Color.translate("&cYou cannot freeze another staff member."));
								} else {
									modmode.getFreezeListener().setFreeze(player, inspected, true);
								}
							}
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Action action = event.getAction();
		Inventory rm = Bukkit.createInventory(null, 54, "Silent Chest");
		if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
			Player player = event.getPlayer();
			if (isStaffModeActive(player) && player.hasPermission("modmode.player.staff")) {
				ItemStack itemStack = player.getItemInHand();
				if (itemStack != null) {
					if (itemStack.getType() == Material.BEACON) {
						event.setCancelled(true);
						List<Player> worldPlayers = player.getWorld().getPlayers().stream()
								.filter(other -> !HCF.getPlugin().getStaffModeListener().isStaffModeActive(other)
										&& other.getLocation().getY() < 45.0)
								.collect(Collectors.toList());
						Player tp;
						if (worldPlayers.isEmpty()) {
							player.sendMessage(ChatColor.RED + "There are no miners online.");
							return;
						} else if (worldPlayers.size() == 1) {
							tp = worldPlayers.iterator().next();
						} else {
							int randInt = ThreadLocalRandom.current().nextInt(worldPlayers.size());
							tp = worldPlayers.get(randInt);
						}
						Bukkit.dispatchCommand(player, "teleport " + tp.getName());
					}
					if (itemStack.getType() == Material.NETHER_STAR) {
						int StaffCounter = 0;
						this.staffInv = Bukkit.createInventory(null, 54, "Online Staff");

						for (Player players : Bukkit.getOnlinePlayers()) {
							if (players.hasPermission("hcf.command.staffmode")) {
								StaffCounter++;
								
							}
							if (StaffCounter < 54) {
								if (players.hasPermission("hcf.command.staffmode")) {
									ItemStack StaffxSkull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
									ItemMeta StaffxSkullMeta = StaffxSkull.getItemMeta();
									StaffxSkullMeta.setDisplayName(players.getName());
									StaffxSkullMeta.setLore(Color.translateFromArray(Arrays.asList(CC.MENU_BAR, CC.translate("&cClick to teleport to this staff member"), CC.MENU_BAR)));
									StaffxSkull.setItemMeta(StaffxSkullMeta);
									staffInv.addItem(StaffxSkull);
								}
							} else {
								event.getPlayer()
										.sendMessage(Color.translate("&cThere are no staff currently online."));
							}
						}
						player.openInventory(staffInv);
					}
					if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
						if (event.getClickedBlock().getType().equals(Material.CHEST)
								|| event.getClickedBlock().getType().equals(Material.TRAPPED_CHEST)) {
							Chest chest = (Chest) event.getClickedBlock().getState();
							player.sendMessage(
									ChatColor.RED.toString() + ChatColor.ITALIC + "Opened chest silently...");
							rm.setContents(chest.getInventory().getContents());
							player.openInventory(rm);
							event.setCancelled(true);
						}
						event.setCancelled(true);
					}
					if (itemStack.getType() == Material.WATCH && event.getAction() == Action.LEFT_CLICK_AIR) {
						event.setCancelled(true);
						List<Player> worldPlayers = player.getWorld().getPlayers().stream()
								.filter(other -> !HCF.getPlugin().getStaffModeListener().isStaffModeActive(other)
										&& other.getLocation().getY() < 45.0)
								.collect(Collectors.toList());
						Player tp;
						if (worldPlayers.isEmpty()) {
							player.sendMessage(ChatColor.RED + "There are no miners online.");
							return;
						} else if (worldPlayers.size() == 1) {
							tp = worldPlayers.iterator().next();
						} else {
							int randInt = ThreadLocalRandom.current().nextInt(worldPlayers.size());
							tp = worldPlayers.get(randInt);
						}
						Bukkit.dispatchCommand(player, "teleport " + tp.getName());
					}
					if (itemStack.getType() == Material.WATCH && event.getAction() == Action.RIGHT_CLICK_AIR) {
						event.setCancelled(true);
						List<Player> onlinePlayers = Bukkit.getOnlinePlayers().stream()
								.filter(other -> !HCF.getPlugin().getStaffModeListener().isStaffModeActive(other)
										&& event.getPlayer().canSee(other))
								.collect(Collectors.toList());
						Player tp;
						if (onlinePlayers.isEmpty()) {
							player.sendMessage(ChatColor.RED + "Could not tp you to another player.");
							return;
						} else if (onlinePlayers.size() == 1) {
							tp = onlinePlayers.iterator().next();
						} else {
							int randInt = ThreadLocalRandom.current().nextInt(onlinePlayers.size());
							tp = onlinePlayers.get(randInt);
						}
						Bukkit.dispatchCommand(player, "teleport " + tp.getName());
					} else if (itemStack.getType() == Material.INK_SACK) {
						if (isVanished(player)) {
							setVanished(player, false);
						} else {
							setVanished(player, true);
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
		if (event.getRightClicked() != null && event.getRightClicked() instanceof Player) {
			Player player = event.getPlayer();
			if (isStaffModeActive(player) && player.hasPermission("modmode.player.staff")) {
				ItemStack itemStack = player.getItemInHand();
				if (itemStack != null) {
					Player target = (Player) event.getRightClicked();
					if (itemStack.getType() == Material.BOOK) {
						if (target != null && !player.getName().equals(target.getName())) {
							openInspectionMenu(player, target);
							player.sendMessage(Color.translate(
									"&fYou are now inspecting the inventory of &5" + target.getName() + "&f."));
						}
					}

					else if (itemStack.getType() == Material.LEASH) {
						if (target != null && !player.getName().equals(target.getName())) {
							if (player.getVehicle() != null) {
								player.getVehicle().eject();
							}
							target.setPassenger(player);
						}
					}

					else if (itemStack.getType() == Material.PACKED_ICE) {
						if (target != null && !player.getName().equals(target.getName())) {
							if (modmode.getFreezeListener().isFrozen(target)) {
								modmode.getFreezeListener().setFreeze(player, target, false);
							} else {
								if (target.hasPermission("modmode.player.staff") || target.isOp()) {
									player.sendMessage(Color.translate("&cYou cannot freeze another staff member."));
								} else {
									modmode.getFreezeListener().setFreeze(player, target, true);
								}
							}
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		event.setJoinMessage(null);
		if (!player.hasPermission("hcf.command.creative") && player.getGameMode() == GameMode.CREATIVE) {
			player.setGameMode(GameMode.SURVIVAL);
		}

		if (player.hasPermission("modmode.player.staff.onjoin")) {
			setStaffMode(player, true);
		}

		if (player.hasPermission("modmode.player.staff")) {
			for (Player staff : Bukkit.getServer().getOnlinePlayers()) {
				if (staff.hasPermission("modmode.player.staff")) {

				}
			}
		} else {
			if (vanished.size() > 0) {
				for (UUID uuid : vanished) {
					Player vanishedPlayer = Bukkit.getServer().getPlayer(uuid);
					if (vanishedPlayer != null) {
						player.hidePlayer(vanishedPlayer);
					}
				}
			}
			
		}
	}
	@EventHandler
    public void onServerStop(ServerShutdownEvent event) {
        for (Player staff : Bukkit.getServer().getOnlinePlayers()) {
            if (isStaffModeActive(staff) && staff.hasPermission("modmode.player.staff")) {
                staffMode.remove(staff.getUniqueId());
                inspectedPlayer.remove(staff.getUniqueId());
                PlayerInventory playerInventory = staff.getInventory();
                playerInventory.setArmorContents(new ItemStack[] { new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR) });
                playerInventory.clear();
                setVanished(staff, false);
                if (hasPreviousInventory(staff)) {
                    loadInventory(staff);
                }
                if (!staff.hasPermission("hcf.command.creative") && staff.getGameMode() == GameMode.CREATIVE) {
                    staff.setGameMode(GameMode.SURVIVAL);
                }
            }
        }
    }
	@EventHandler
	public void onPlayerGameModeChange(PlayerGameModeChangeEvent event) {
		if (isStaffModeActive(event.getPlayer()) && event.getNewGameMode() != GameMode.CREATIVE) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		event.setQuitMessage(null);
		if (player.hasPermission("modmode.player.staff")) {
			for (Player staff : Bukkit.getServer().getOnlinePlayers()) {
				if (staff.hasPermission("modmode.player.staff")) {

				}
			}
		}
		if (isStaffModeActive(player)) {
			staffMode.remove(player.getUniqueId());
			inspectedPlayer.remove(player.getUniqueId());
			PlayerInventory playerInventory = player.getInventory();
			playerInventory.setArmorContents(new ItemStack[] { new ItemStack(Material.AIR), new ItemStack(Material.AIR),
					new ItemStack(Material.AIR), new ItemStack(Material.AIR) });
			playerInventory.clear();
			setVanished(player, false);
			CombatLogListener.addSafeDisconnect(player.getUniqueId());
			if (hasPreviousInventory(player)) {
				loadInventory(player);
			}
			if (!player.hasPermission("hcf.command.creative") && player.getGameMode() == GameMode.CREATIVE) {
				player.setGameMode(GameMode.SURVIVAL);
			}
		}
	}

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		if (isStaffModeActive(player)) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onInv(InventoryClickEvent event) {
		final Player player = (Player) event.getWhoClicked();
		ItemStack itemStack = event.getCurrentItem();
		InventoryAction invAction = event.getAction();
		if (event.getClickedInventory() == null) return;
		if (event.getInventory() == null) return;
		if (isVanished(player) || isStaffModeActive(player)) {
			if (event.getSlotType().equals(InventoryType.SlotType.OUTSIDE)) {
				event.setCancelled(true);
				return;
			}
			if (((itemStack == null) || (itemStack.getType().equals(Material.AIR)))
					&& ((invAction.equals(InventoryAction.HOTBAR_SWAP))
							|| (invAction.equals(InventoryAction.SWAP_WITH_CURSOR)))) {
				event.setCancelled(true);
				return;
			}
			event.setCancelled(true);
			if ((itemStack.hasItemMeta()) && (itemStack.getItemMeta().hasDisplayName())) {
				if ((invAction.equals(InventoryAction.HOTBAR_SWAP))
						|| (invAction.equals(InventoryAction.SWAP_WITH_CURSOR))) {
					event.setCancelled(true);
				}
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		Player player = event.getPlayer();
		if (isVanished(player) || isStaffModeActive(player)) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlock();
		if (isVanished(player) || isStaffModeActive(player)) {
			if (block != null) {
				// player.sendMessage(ChatColor.RED + "You cannot place blocks whilst
				// vanished.");
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlock();
		if (isVanished(player) || isStaffModeActive(player)) {
			if (block != null) {
				// player.sendMessage(ChatColor.RED + "You cannot break blocks whilst
				// vanished.");
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
			Player player = (Player) event.getDamager();
			if (isVanished(player) || isStaffModeActive(player) && player.hasPermission("modmode.player.staff")) {
				event.setCancelled(true);
				// player.sendMessage(ChatColor.RED + "You cannot attack players whilst
				// vanished.");
			}
		} else if (event.getEntity() instanceof LivingEntity && event.getDamager() instanceof Player) {
			Player player = (Player) event.getDamager();
			if (isVanished(player) || isStaffModeActive(player) && player.hasPermission("modmode.player.staff")) {
				event.setCancelled(true);
				// player.sendMessage(ChatColor.RED + "You cannot attack mobs whilst
				// vanished.");
			}
		}
	}
}