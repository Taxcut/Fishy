package com.fishy.hcf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.comphenix.protocol.ProtocolManager;
import com.fishy.hcf.classes.PvpClassManager;
import com.fishy.hcf.classes.bard.EffectRestorer;
import com.fishy.hcf.command.BroadcastCommand;
import com.fishy.hcf.command.ChatColorCommand;
import com.fishy.hcf.command.ChestCommand;
import com.fishy.hcf.command.ClearCommand;
import com.fishy.hcf.command.CobbleCommand;
import com.fishy.hcf.command.CondenseCommand;
import com.fishy.hcf.command.CopyInvCommand;
import com.fishy.hcf.command.CraftCommand;
import com.fishy.hcf.command.DeathCommand;
import com.fishy.hcf.command.DoubleKeyCommand;
import com.fishy.hcf.command.EnchantCommand;
import com.fishy.hcf.command.EndPortalCommand;
import com.fishy.hcf.command.FFACommand;
import com.fishy.hcf.command.FFAEnableCommand;
import com.fishy.hcf.command.FeedCommand;
import com.fishy.hcf.command.FlashSaleCommand;
import com.fishy.hcf.command.FlyCommand;
import com.fishy.hcf.command.FocusCommand;
import com.fishy.hcf.command.FreezeCommand;
import com.fishy.hcf.command.FreezeServerCommand;
import com.fishy.hcf.command.GamemodeCommand;
import com.fishy.hcf.command.GiveCommand;
import com.fishy.hcf.command.GlowstoneCommand;
import com.fishy.hcf.command.GoppleCommand;
import com.fishy.hcf.command.HealCommand;
import com.fishy.hcf.command.HelpCommand;
import com.fishy.hcf.command.HidePlayerCommand;
import com.fishy.hcf.command.HideStaffCommand;
import com.fishy.hcf.command.InvseeCommand;
import com.fishy.hcf.command.InvseeOfflineCommand;
import com.fishy.hcf.command.KeySaleCommand;
import com.fishy.hcf.command.KillCommand;
import com.fishy.hcf.command.LFFCommand;
import com.fishy.hcf.command.ListCommand;
import com.fishy.hcf.command.LogoutCommand;
import com.fishy.hcf.command.MapKitCommand;
import com.fishy.hcf.command.MassayCommand;
import com.fishy.hcf.command.MessageCommand;
import com.fishy.hcf.command.NickCommand;
import com.fishy.hcf.command.PanicCommand;
import com.fishy.hcf.command.PlayTimeCommand;
import com.fishy.hcf.command.PvpTimerCommand;
import com.fishy.hcf.command.RebootCommand;
import com.fishy.hcf.command.RegenCommand;
import com.fishy.hcf.command.RenameCommand;
import com.fishy.hcf.command.RepairCommand;
import com.fishy.hcf.command.ReplyCommand;
import com.fishy.hcf.command.SaveDataCommand;
import com.fishy.hcf.command.ServerTimeCommand;
import com.fishy.hcf.command.ShowPlayerCommand;

import com.fishy.hcf.command.SkullCommand;
import com.fishy.hcf.command.SpawnCommand;
import com.fishy.hcf.command.SpawnerCommand;
import com.fishy.hcf.command.SpeedCommand;
import com.fishy.hcf.command.StackCommand;
import com.fishy.hcf.command.StaffModeCommand;
import com.fishy.hcf.command.StatsCommand;
import com.fishy.hcf.command.StatsResetCommand;
import com.fishy.hcf.command.SudoCommand;
import com.fishy.hcf.command.TeamCoordinatesCommand;
import com.fishy.hcf.command.TeleportCommand;
import com.fishy.hcf.command.ToggleCapzoneEntryCommand;
import com.fishy.hcf.command.ToggleLightningCommand;
import com.fishy.hcf.command.ToggleMessagesCommand;
import com.fishy.hcf.command.ToggleSoundsCommand;
import com.fishy.hcf.command.TopCommand;
import com.fishy.hcf.command.TpallCommand;
import com.fishy.hcf.command.TphereCommand;
import com.fishy.hcf.command.TpposCommand;
import com.fishy.hcf.command.VanishCommand;
import com.fishy.hcf.command.WorldCommand;
import com.fishy.hcf.command.links.DiscordCommand;
import com.fishy.hcf.command.links.StoreCommand;
import com.fishy.hcf.command.links.TeamspeakCommand;
import com.fishy.hcf.command.links.WebsiteCommand;
import com.fishy.hcf.configs.ConfigFile;
import com.fishy.hcf.deathban.Deathban;
import com.fishy.hcf.deathban.DeathbanListener;
import com.fishy.hcf.deathban.DeathbanManager;
import com.fishy.hcf.deathban.FlatFileDeathbanManager;
import com.fishy.hcf.deathban.StaffReviveCommand;
import com.fishy.hcf.deathban.lives.LivesExecutor;
import com.fishy.hcf.economy.EconomyCommand;
import com.fishy.hcf.economy.EconomyManager;
import com.fishy.hcf.economy.FlatFileEconomyManager;
import com.fishy.hcf.economy.PayCommand;
import com.fishy.hcf.events.CaptureZone;
import com.fishy.hcf.events.EventExecutor;
import com.fishy.hcf.events.conquest.ConquestExecutor;
import com.fishy.hcf.events.crate.KeyManager;
import com.fishy.hcf.events.eotw.EotwCommand;
import com.fishy.hcf.events.eotw.EotwHandler;
import com.fishy.hcf.events.eotw.EotwListener;
import com.fishy.hcf.events.faction.CapturableFaction;
import com.fishy.hcf.events.faction.ConquestFaction;
import com.fishy.hcf.events.faction.KothFaction;
import com.fishy.hcf.events.koth.KothExecutor;
import com.fishy.hcf.events.ktk.KillTheKing;
import com.fishy.hcf.events.ktk.KillTheKingCommand;
import com.fishy.hcf.faction.FactionExecutor;
import com.fishy.hcf.faction.FactionManager;
import com.fishy.hcf.faction.FactionMember;
import com.fishy.hcf.faction.FlatFileFactionManager;
import com.fishy.hcf.faction.claim.Claim;
import com.fishy.hcf.faction.claim.ClaimHandler;
import com.fishy.hcf.faction.claim.ClaimWandListener;
import com.fishy.hcf.faction.claim.Subclaim;
import com.fishy.hcf.faction.claim.SubclaimWandListener;
import com.fishy.hcf.faction.type.ClaimableFaction;
import com.fishy.hcf.faction.type.EndPortalFaction;
import com.fishy.hcf.faction.type.Faction;
import com.fishy.hcf.faction.type.GlowstoneMountainFaction;
import com.fishy.hcf.faction.type.PlayerFaction;
import com.fishy.hcf.faction.type.RoadFaction;
import com.fishy.hcf.faction.type.SpawnFaction;
import com.fishy.hcf.hooks.VaultHook;
import com.fishy.hcf.inventory.settings.ClaimSettingsInventory;
import com.fishy.hcf.kit.FlatFileKitManager;
import com.fishy.hcf.kit.Kit;
import com.fishy.hcf.kit.KitExecutor;
import com.fishy.hcf.kit.KitListener;
import com.fishy.hcf.kit.KitManager;
import com.fishy.hcf.listener.BookDisenchantListener;
import com.fishy.hcf.listener.BorderListener;
import com.fishy.hcf.listener.BottledExpListener;
import com.fishy.hcf.listener.ChatListener;
import com.fishy.hcf.listener.ColorSignListener;
import com.fishy.hcf.listener.CoreListener;
import com.fishy.hcf.listener.CrowbarListener;
import com.fishy.hcf.listener.CutCleanListener;
import com.fishy.hcf.listener.DeathListener;
import com.fishy.hcf.listener.DeathMessageListener;
import com.fishy.hcf.listener.ElevatorListener;
import com.fishy.hcf.listener.EnchantLimitListener;
//import com.fishy.hcf.listener.EnderpearlListener;
import com.fishy.hcf.listener.EntityLimitListener;
import com.fishy.hcf.listener.EventSignListener;
import com.fishy.hcf.listener.ExpMultiplierListener;
import com.fishy.hcf.listener.FactionListener;
import com.fishy.hcf.listener.FoundDiamondsListener;
import com.fishy.hcf.listener.FreezeListener;
import com.fishy.hcf.listener.FreezeServerListener;
import com.fishy.hcf.listener.GlowstoneListener;

import com.fishy.hcf.listener.JoinMessageListener;
import com.fishy.hcf.listener.KillstreakListener;
import com.fishy.hcf.listener.KitMapListener;
import com.fishy.hcf.listener.KitSignListener;
import com.fishy.hcf.listener.KnockBackListener;
import com.fishy.hcf.listener.MobFarmListener;
import com.fishy.hcf.listener.PlayTimeListener;
import com.fishy.hcf.listener.PortalListener;
import com.fishy.hcf.listener.PotionLimitListener;
import com.fishy.hcf.listener.ProtectionListener;
import com.fishy.hcf.listener.ShopSignListener;
import com.fishy.hcf.listener.SignSubclaimListener;
import com.fishy.hcf.listener.SkullListener;
import com.fishy.hcf.listener.StrengthListener;
import com.fishy.hcf.listener.SubclaimListener;
import com.fishy.hcf.listener.WorldListener;
import com.fishy.hcf.listener.fixes.ArmorDurabilityFix;
import com.fishy.hcf.listener.fixes.BeaconStrengthFixListener;
import com.fishy.hcf.listener.fixes.BlockGlitchFixListener;
import com.fishy.hcf.listener.fixes.BlockJumpGlitchFixListener;
import com.fishy.hcf.listener.fixes.BoatGlitchFixListener;
import com.fishy.hcf.listener.fixes.BookQuillFixListener;
import com.fishy.hcf.listener.fixes.EnderChestRemovalListener;
import com.fishy.hcf.listener.fixes.FenceGateGlitchListener;
import com.fishy.hcf.listener.fixes.HungerFix;
import com.fishy.hcf.listener.fixes.InfinityArrowFixListener;
import com.fishy.hcf.listener.fixes.MobFixes;
import com.fishy.hcf.listener.fixes.NameVerifyListener;
import com.fishy.hcf.listener.fixes.PVPTimerListener;
import com.fishy.hcf.listener.fixes.PearlDeathGlitchListener;
//import com.fishy.hcf.listener.fixes.PhaseGlitchListener;
import com.fishy.hcf.listener.fixes.PickFixListener;
import com.fishy.hcf.listener.fixes.PluginListFix;
import com.fishy.hcf.listener.fixes.PotFixListener;
import com.fishy.hcf.listener.fixes.RedstoneReducer;
import com.fishy.hcf.listener.fixes.VoidGlitchFixListener;
import com.fishy.hcf.listener.fixes.WaterGlitchFix;
import com.fishy.hcf.logger.CombatLogListener;
import com.fishy.hcf.logger.CustomEntityRegistration;
import com.fishy.hcf.potioncommands.EffectSpeedCommand;
import com.fishy.hcf.potioncommands.FresCommand;
import com.fishy.hcf.potioncommands.InvisCommand;
import com.fishy.hcf.potioncommands.NightVisionCommand;
import com.fishy.hcf.scoreboard.ScoreboardHandler;
import com.fishy.hcf.staffmode.StaffModeListener;
import com.fishy.hcf.tab.TablistListener;
import com.fishy.hcf.timer.TimerExecutor;
import com.fishy.hcf.timer.TimerManager;
import com.fishy.hcf.timer.custom.CustomTimerCommand;
import com.fishy.hcf.timer.sotw.SotwCommand;
import com.fishy.hcf.timer.sotw.SotwListener;
import com.fishy.hcf.timer.sotw.SotwTimer;
import com.fishy.hcf.timer.type.DoubleKeyTimer;
import com.fishy.hcf.timer.type.FlashSaleTimer;
import com.fishy.hcf.timer.type.KeySaleTimer;
import com.fishy.hcf.user.FactionUser;
import com.fishy.hcf.user.UserManager;
import com.fishy.hcf.util.CC;
import com.fishy.hcf.util.SerializableLocation;
import com.fishy.hcf.util.base.Config;
import com.fishy.hcf.util.base.PersistableLocation;
import com.fishy.hcf.util.base.SignHandler;
import com.fishy.hcf.util.base.chat.HttpMojangLang;
import com.fishy.hcf.util.base.chat.Lang;
import com.fishy.hcf.util.base.chat.MojangLang;
import com.fishy.hcf.util.base.cuboid.Cuboid;
import com.fishy.hcf.util.base.cuboid.NamedCuboid;
import com.fishy.hcf.util.base.itemdb.ItemDb;
import com.fishy.hcf.util.base.itemdb.SimpleItemDb;
import com.fishy.hcf.visualise.ProtocolLibHook;
import com.fishy.hcf.visualise.VisualiseHandler;
import com.fishy.hcf.visualise.WallBorderListener;
import com.google.common.base.Joiner;
import com.hcrival.chronium.ChroniumAPI;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;

import lombok.Getter;
import lombok.Setter;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;

@Getter
@Setter
public class HCF extends JavaPlugin {

	
	public static final Joiner SPACE_JOINER = Joiner.on(' ');
	public static final Joiner COMMA_JOINER = Joiner.on(", ");

	private VaultHook vaultHook;

	public HashMap<String, ItemStack[]> hm;
	public HashMap<String, ItemStack[]> armorContents;

	private ConfigFile configFile;

	public static boolean hiddenPlayers;

	@Getter
	private PlayTimeListener playTimeManager;

	@Setter
	private boolean serverFrozen;

	@Getter
	private static HCF plugin;

	@Getter
	private ItemDb itemDb;

	@Getter
	private FileConfiguration mountain;

	@Getter
	private Random random = new Random();

	@Getter
	private ScoreboardHandler scoreboardHandler;

	@Getter
	private ClaimHandler claimHandler;

	@Getter
	private CombatLogListener combatLogListener;

	@Getter
	private DeathbanManager deathbanManager;

	@Getter
	private EconomyManager economyManager;

	@Getter
	private EffectRestorer effectRestorer;

	@Getter
	private ClaimSettingsInventory claimSettings;

	@Getter
	private EotwHandler eotwHandler;

	@Getter
	private FactionManager factionManager;

	@Getter
	private ImageFolder imageFolder;

	@Getter
	private KeyManager keyManager;

	@Getter
	private PvpClassManager pvpClassManager;

	@Getter
	private SotwTimer sotwTimer;

	@Getter
	private KeySaleTimer keySaleTimer;

	@Getter
	private TimerManager timerManager;

	@Getter
	private UserManager userManager;

	@Getter
	private KitManager kitManager;

	@Getter
	private VisualiseHandler visualiseHandler;

	@Getter
	private WorldEditPlugin worldEdit;

	@Getter
	private FreezeListener freezeListener;

	@Getter
	private StaffModeListener staffModeListener;

	@Getter
	private FlashSaleTimer flashSaleTimer;

	@Getter
	private DoubleKeyTimer doubleKeyTimer;

	@Getter
	private boolean paperPatch;

	@Getter
	private Chat chat = null;

	@Getter
	private Permission permission = null;

	@Getter
	private SignHandler signHandler;

	private ProtocolManager protocolManager;

	private boolean configurationLoaded = true;

	private KillTheKing activeKTK;

	private Config locations;

	private PersistableLocation endSpawn;
	private PersistableLocation endExit;

	private String craftBukkitVersion;

	public static String c(String msg) {
		return ChatColor.translateAlternateColorCodes('&', msg);
	}

	@Getter
	private MojangLang language;

	@Override
	public void onEnable() {
	
		Bukkit.getPluginManager().registerEvents(new HungerFix(), this);
		getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		setupChat();
		setupPermission();
		if (!configurationLoaded) {
			getLogger().severe("Disabling plugin..");
			setEnabled(false);
			return;
		}

		locations = new Config(this, "locations");

		this.craftBukkitVersion = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];

		this.itemDb = new SimpleItemDb(this);
		this.signHandler = new SignHandler(this);

		this.language = new HttpMojangLang();
		try {
			Lang.initialize("en_US");
			// this.language.index("1.7.10", Locale.ENGLISH);
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		HCF.plugin = this;
		DateTimeFormats.reload(TimeZone.getTimeZone("EST")); // Initialise the static fields.
		///////////////////////////
		Plugin wep = getServer().getPluginManager().getPlugin("WorldEdit"); // Initialise WorldEdit hook.
		worldEdit = wep instanceof WorldEditPlugin && wep.isEnabled() ? (WorldEditPlugin) wep : null;

		registerSerialization();
		registerCommands();
		registerManagers();
		registerListeners();

		CustomEntityRegistration.registerCustomEntities();

		paperPatch = false;
		long dataSaveInterval = TimeUnit.MINUTES.toMillis(20L);
		new BukkitRunnable() {
			@Override
			public void run() {
				saveData();
			}
		}.runTaskTimerAsynchronously(this, dataSaveInterval, dataSaveInterval);
		(this.configFile = new ConfigFile("config.yml")).saveDefault();
		ProtocolLibHook.hook(this); // Initialise ProtocolLib hook

		for (World world : Bukkit.getWorlds()) {
			world.setAutoSave(true);
			world.setGameRuleValue("doDaylightCycle", "false");
			world.setTime(0);
			world.setDifficulty(Difficulty.EASY);
		}

		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "knockback 0.9 0.9 0.9");
		setupBroadcast();
		addRecipe();
	}

	public void addRecipe() {
		ShapelessRecipe shapelessRecipe = new ShapelessRecipe(new ItemStack(Material.SPECKLED_MELON, 1));
		shapelessRecipe.addIngredient(1, Material.GOLD_NUGGET);
		shapelessRecipe.addIngredient(1, Material.MELON);
		Bukkit.addRecipe(shapelessRecipe);
	}

	public void saveData() {
		deathbanManager.saveDeathbanData();
		economyManager.saveEconomyData();
		factionManager.saveFactionData();
		playTimeManager.savePlaytimeData();
		keyManager.saveKeyData();
		timerManager.saveTimerData();
		userManager.saveUserData();
		kitManager.saveKitData();
	}

	@Override
	public void onDisable() {

		CustomEntityRegistration.unregisterCustomEntities();

		this.signHandler.cancelTasks(null);

		if (!configurationLoaded) {
			// Ignore everything.
			return;
		}

		combatLogListener.removeCombatLoggers();
		pvpClassManager.onDisable();
		scoreboardHandler.clearBoards();

		saveData();
		this.signHandler.cancelTasks(null);
		HCF.plugin = null; // Always uninitialise last.
	}

	private boolean setupChat() {
		RegisteredServiceProvider<Chat> chatProvider = this.getServer().getServicesManager()
				.getRegistration(Chat.class);
		if (chatProvider != null) {
			chat = chatProvider.getProvider();
		}

		return (chat != null);
	}

	private boolean setupPermission() {
		RegisteredServiceProvider<Permission> permissionProvider = this.getServer().getServicesManager()
				.getRegistration(Permission.class);
		if (permissionProvider != null) {
			permission = permissionProvider.getProvider();
		}

		return (permission != null);
	}

	private void registerSerialization() {
		ConfigurationSerialization.registerClass(CaptureZone.class);
		ConfigurationSerialization.registerClass(Deathban.class);
		ConfigurationSerialization.registerClass(Claim.class);
		ConfigurationSerialization.registerClass(Subclaim.class);
		ConfigurationSerialization.registerClass(Deathban.class);
		ConfigurationSerialization.registerClass(Kit.class);
		ConfigurationSerialization.registerClass(FactionUser.class);
		ConfigurationSerialization.registerClass(ClaimableFaction.class);
		ConfigurationSerialization.registerClass(ConquestFaction.class);
		ConfigurationSerialization.registerClass(CapturableFaction.class);
		ConfigurationSerialization.registerClass(KothFaction.class);
		ConfigurationSerialization.registerClass(EndPortalFaction.class);
		ConfigurationSerialization.registerClass(Faction.class);
		ConfigurationSerialization.registerClass(FactionMember.class);
		ConfigurationSerialization.registerClass(PlayerFaction.class);
		ConfigurationSerialization.registerClass(RoadFaction.class);
		ConfigurationSerialization.registerClass(SpawnFaction.class);
		ConfigurationSerialization.registerClass(RoadFaction.NorthRoadFaction.class);
		ConfigurationSerialization.registerClass(RoadFaction.EastRoadFaction.class);
		ConfigurationSerialization.registerClass(RoadFaction.SouthRoadFaction.class);
		ConfigurationSerialization.registerClass(RoadFaction.WestRoadFaction.class);
		ConfigurationSerialization.registerClass(GlowstoneMountainFaction.class);
		ConfigurationSerialization.registerClass(PersistableLocation.class);
		ConfigurationSerialization.registerClass(SerializableLocation.class);
		ConfigurationSerialization.registerClass(Cuboid.class);
		ConfigurationSerialization.registerClass(NamedCuboid.class);
	}

	private void registerListeners() {
		PluginManager manager = getServer().getPluginManager();

		freezeListener = new FreezeListener();
		staffModeListener = new StaffModeListener();
		manager.registerEvents(new CobbleCommand(), this);
		manager.registerEvents(new BlockJumpGlitchFixListener(), this);
		manager.registerEvents(new FenceGateGlitchListener(), this);
		manager.registerEvents(new BoatGlitchFixListener(), this);
		manager.registerEvents(new BookDisenchantListener(this), this);
		manager.registerEvents(new MobFarmListener(), this);
		manager.registerEvents(new BottledExpListener(this), this);
		manager.registerEvents(new ChatListener(this), this);
		manager.registerEvents(new ClaimWandListener(this), this);
		// manager.registerEvents(combatLogListener = new CombatLogListener(this),
		// this);
		manager.registerEvents(new CoreListener(this), this);
		manager.registerEvents(new CrowbarListener(this), this);
		// manager.registerEvents(new HidingPlayersListener(), this);
		manager.registerEvents(new DeathListener(this), this);
		manager.registerEvents(new CutCleanListener(), this);
		if (!HCF.getPlugin().getConfig().getBoolean("KITMAP")) {
			manager.registerEvents(new DeathbanListener(this), this);
			manager.registerEvents(new FoundDiamondsListener(this), this);
			manager.registerEvents(new VoidGlitchFixListener(), this);
		}
		if (HCF.getPlugin().getConfig().getBoolean("KITMAP")) {
			manager.registerEvents(new KillstreakListener(this), this);
			manager.registerEvents(new KitMapListener(this), this);
			manager.registerEvents(new KitSignListener(this), this);
		}
		manager.registerEvents(new KitListener(this), this);
		manager.registerEvents(new EnderChestRemovalListener(this), this);
		manager.registerEvents(new EntityLimitListener(this), this);
		manager.registerEvents(new BorderListener(), this);
		manager.registerEvents(new EotwListener(this), this);
		manager.registerEvents(new EventSignListener(), this);
		manager.registerEvents(new ElevatorListener(this), this);
		manager.registerEvents(new ExpMultiplierListener(this), this);
		manager.registerEvents(new FactionListener(this), this);
		manager.registerEvents(new InfinityArrowFixListener(), this);
		//manager.registerEvents(new PearlGlitchListener(this), this);
		manager.registerEvents(new PortalListener(this), this);
		manager.registerEvents(new ProtectionListener(this), this);
		manager.registerEvents(new JoinMessageListener(), this);
		manager.registerEvents(new SubclaimWandListener(this), this);
		manager.registerEvents(new PVPTimerListener(this), this);
		manager.registerEvents(new CombatLogListener(this), this);
		manager.registerEvents(new SignSubclaimListener(this), this);
		manager.registerEvents(new SkullListener(), this);
		manager.registerEvents(new SotwListener(this), this);
		manager.registerEvents(new BeaconStrengthFixListener(), this);
		manager.registerEvents(new ArmorDurabilityFix(), this);
		manager.registerEvents(new WallBorderListener(this), this);
		manager.registerEvents(new WorldListener(), this);
		//manager.registerEvents(new PhaseListener(), this);
		//manager.registerEvents(new PhaseGlitchListener(), this);
		manager.registerEvents(new DeathMessageListener(this), this);
		manager.registerEvents(new ColorSignListener(), this);
		manager.registerEvents(new StrengthListener(), this);
		manager.registerEvents(new SubclaimListener(this), this);
		manager.registerEvents(new PluginListFix(), this);
		manager.registerEvents(new FreezeServerListener(this), this);
		manager.registerEvents(new PickFixListener(), this);
		manager.registerEvents(new GlowstoneListener(this), this);
		manager.registerEvents(new PearlDeathGlitchListener(), this);
		manager.registerEvents(new RedstoneReducer(), this);
		manager.registerEvents(new MobFixes(), this);
		manager.registerEvents(new PotFixListener(this), this);
		manager.registerEvents(new EnchantLimitListener(), this);
		manager.registerEvents(new PotionLimitListener(this), this);
		manager.registerEvents(this.playTimeManager = new PlayTimeListener(this), this);
		manager.registerEvents(new BlockGlitchFixListener(this), this);
		manager.registerEvents(new ShopSignListener(this), this);
		// manager.registerEvents(new PotionListener(), this);
		manager.registerEvents(new KnockBackListener(this), this);

		manager.registerEvents(new BookQuillFixListener(this), this);
		manager.registerEvents(new TablistListener(), this);
		manager.registerEvents(new NameVerifyListener(this), this);
		manager.registerEvents(new WaterGlitchFix(), this);
	}

	private void registerCommands() {
		if (!HCF.getPlugin().getConfig().getBoolean("KITMAP")) {
			getCommand("pvptimer").setExecutor(new PvpTimerCommand(this));
			getCommand("lives").setExecutor(new LivesExecutor(this));
			getCommand("regen").setExecutor(new RegenCommand(this));
			getCommand("kit").setExecutor(new KitExecutor(this));
			getCommand("staffrevive").setExecutor(new StaffReviveCommand(this));
		}
		if (HCF.getPlugin().getConfig().getBoolean("KITMAP")) {
			getCommand("chest").setExecutor(new ChestCommand());
		}
		getCommand("ffaenabled").setExecutor(new FFAEnableCommand());
		getCommand("freezeserver").setExecutor(new FreezeServerCommand(this));
		getCommand("conquest").setExecutor(new ConquestExecutor(this));
		getCommand("top").setExecutor(new TopCommand());
		getCommand("endportal").setExecutor(new EndPortalCommand(this));
		getCommand("economy").setExecutor(new EconomyCommand(this));
		getCommand("eotw").setExecutor(new EotwCommand(this));
		getCommand("event").setExecutor(new EventExecutor(this));
		getCommand("faction").setExecutor(new FactionExecutor(this));
		getCommand("skull").setExecutor(new SkullCommand());
		getCommand("feed").setExecutor(new FeedCommand());
		getCommand("gopple").setExecutor(new GoppleCommand(this));
		getCommand("koth").setExecutor(new KothExecutor(this));
		getCommand("logout").setExecutor(new LogoutCommand(this));
		getCommand("clear").setExecutor(new ClearCommand(this));
		getCommand("massay").setExecutor(new MassayCommand());
		getCommand("glowstone").setExecutor(new GlowstoneCommand(this));
		getCommand("heal").setExecutor(new HealCommand());
		getCommand("oinvsee").setExecutor(new InvseeOfflineCommand());
		getCommand("ffa").setExecutor(new FFACommand());
		getCommand("pay").setExecutor(new PayCommand(this));
		getCommand("copyinv").setExecutor(new CopyInvCommand());
		getCommand("servertime").setExecutor(new ServerTimeCommand(this));
		getCommand("sotw").setExecutor(new SotwCommand(this));
		getCommand("give").setExecutor(new GiveCommand());
		getCommand("focus").setExecutor(new FocusCommand());
		getCommand("panic").setExecutor(new PanicCommand());
		getCommand("togglecapzoneentry").setExecutor(new ToggleCapzoneEntryCommand(this));
		getCommand("togglelightning").setExecutor(new ToggleLightningCommand(this));
		getCommand("savedata").setExecutor(new SaveDataCommand(this));
		getCommand("craft").setExecutor(new CraftCommand());
		getCommand("stats").setExecutor(new StatsCommand());
		getCommand("rename").setExecutor(new RenameCommand());
		getCommand("teleport").setExecutor(new TeleportCommand());
		getCommand("gamemode").setExecutor(new GamemodeCommand());

		getCommand("freeze").setExecutor(new FreezeCommand());
		getCommand("vanish").setExecutor(new VanishCommand());
		getCommand("staff").setExecutor(new StaffModeCommand());
		getCommand("broadcast").setExecutor(new BroadcastCommand());
		getCommand("sounds").setExecutor(new ToggleSoundsCommand(this));
		getCommand("togglemessages").setExecutor(new ToggleMessagesCommand(this));
		getCommand("reply").setExecutor(new ReplyCommand(this));
		getCommand("hideplayer").setExecutor(new HidePlayerCommand());
		getCommand("showplayer").setExecutor(new ShowPlayerCommand());
		getCommand("message").setExecutor(new MessageCommand(this));
		getCommand("statsreset").setExecutor(new StatsResetCommand());
		getCommand("refund").setExecutor(new DeathCommand());
		getCommand("tl").setExecutor(new TeamCoordinatesCommand());
		getCommand("spawn").setExecutor(new SpawnCommand());
		getCommand("cobble").setExecutor(new CobbleCommand());
		getCommand("invsee").setExecutor(new InvseeCommand());
		getCommand("hidestaff").setExecutor(new HideStaffCommand());
		getCommand("lff").setExecutor(new LFFCommand(this));
		getCommand("discord").setExecutor(new DiscordCommand());
		getCommand("website").setExecutor(new WebsiteCommand());
		getCommand("mapkit").setExecutor(new MapKitCommand(this));
		getCommand("fly").setExecutor(new FlyCommand(this));
		getCommand("kill").setExecutor(new KillCommand());
		getCommand("reboot").setExecutor(new RebootCommand());
		getCommand("chatcolor").setExecutor(new ChatColorCommand(this));
		getCommand("repair").setExecutor(new RepairCommand());
		getCommand("teamspeak").setExecutor(new TeamspeakCommand());
		getCommand("condense").setExecutor(new CondenseCommand(this));
		getCommand("panic").setExecutor(new PanicCommand());
		getCommand("tpall").setExecutor(new TpallCommand());
		getCommand("tphere").setExecutor(new TphereCommand());
		getCommand("tppos").setExecutor(new TpposCommand());
		getCommand("timer").setExecutor(new TimerExecutor(this));
		getCommand("enchant").setExecutor(new EnchantCommand(this));
		getCommand("keysale").setExecutor(new KeySaleCommand());
		getCommand("invis").setExecutor(new InvisCommand());
		getCommand("speed").setExecutor(new EffectSpeedCommand());
		getCommand("fres").setExecutor(new FresCommand());
		getCommand("nightvision").setExecutor(new NightVisionCommand());
		getCommand("flashsale").setExecutor(new FlashSaleCommand());
		getCommand("doublekey").setExecutor(new DoubleKeyCommand());
		getCommand("stack").setExecutor(new StackCommand(this));
		getCommand("playtime").setExecutor(new PlayTimeCommand());
		getCommand("world").setExecutor(new WorldCommand(this));
		getCommand("help").setExecutor(new HelpCommand());
		getCommand("spawner").setExecutor(new SpawnerCommand(this));
		getCommand("list").setExecutor(new ListCommand());
		getCommand("store").setExecutor(new StoreCommand());
		getCommand("nick").setExecutor(new NickCommand());
		getCommand("flyspeed").setExecutor(new SpeedCommand(this));
		getCommand("killtheking").setExecutor(new KillTheKingCommand());
		getCommand("sudo").setExecutor(new SudoCommand(this));
		getCommand("customtimer").setExecutor(new CustomTimerCommand());

		Map<String, Map<String, Object>> map = getDescription().getCommands();
		for (Map.Entry<String, Map<String, Object>> entry : map.entrySet()) {
			PluginCommand command = getCommand(entry.getKey());
			command.setPermission("hcf.command." + entry.getKey());
			command.setPermissionMessage(ChatColor.RED + "You do not have permission to use this command.");
		}
	}

	public String translate(String s) {
		return ChatColor.translateAlternateColorCodes('&', s);
	}

	public void setupBroadcast() {
		Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
			List<String> players = new ArrayList<>();
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "save-all");
			saveData();
			for (Player player : Bukkit.getOnlinePlayers()) {
				String primary = ChroniumAPI.getRankOfPlayer(player).getDisplayName();
				if (primary != null && primary.equalsIgnoreCase("Desire")) {
					players.add(player.getName());
					// players.add(ChatColor.translateAlternateColorCodes('&',
					// ChroniumAPI.getRankOfPlayer(player).getPrefix() + player.getName()));
				}
			}
			String message = ChatColor.LIGHT_PURPLE.toString() + "Online " + CC.DARK_PURPLE.toString() + ChatColor.BOLD + "Desire"
					+ ChatColor.LIGHT_PURPLE + " Users" + ChatColor.GRAY + ": " + ChatColor.WHITE
					+ Joiner.on(ChatColor.WHITE + ", ").join(players);
			if (!players.isEmpty()) {
				Bukkit.getScheduler().runTask(HCF.this, () -> {
					Bukkit.broadcastMessage("");
					Bukkit.broadcastMessage(message);
					Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + "Want to be shown? Purchase Desire rank at "
							+ ChatColor.DARK_PURPLE + "shop.desirepvp.net " + ChatColor.LIGHT_PURPLE + "to be displayed.");
					Bukkit.broadcastMessage("");

				});
			}
		}, (20 * 60 * 5) + 45, 20 * 60 * 5);
	}

	private void registerManagers() {

		this.itemDb = new SimpleItemDb(this);

		this.hm = new HashMap<String, ItemStack[]>();
		this.armorContents = new HashMap<String, ItemStack[]>();

		this.signHandler = new SignHandler(this);
		claimHandler = new ClaimHandler(this);
		deathbanManager = new FlatFileDeathbanManager(this);
		economyManager = new FlatFileEconomyManager(this);
		effectRestorer = new EffectRestorer(this);
		eotwHandler = new EotwHandler(this);
		timerManager = new TimerManager(this);// Needs to be registered before ScoreboardHandler.
		scoreboardHandler = new ScoreboardHandler(this);
		factionManager = new FlatFileFactionManager(this);
		imageFolder = new ImageFolder(this);
		keyManager = new KeyManager(this);
		kitManager = new FlatFileKitManager(this);
		pvpClassManager = new PvpClassManager(this);
		sotwTimer = new SotwTimer();
		keySaleTimer = new KeySaleTimer();
		flashSaleTimer = new FlashSaleTimer();
		doubleKeyTimer = new DoubleKeyTimer();

		userManager = new UserManager(this);
		visualiseHandler = new VisualiseHandler();
		vaultHook = new VaultHook(this);
	}

	public String getCraftBukkitVersion() {
		return craftBukkitVersion;
	}

	public static void setHidden(boolean value) {
		hiddenPlayers = value;
	}

}
