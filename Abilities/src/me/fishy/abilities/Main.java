package me.fishy.abilities;

import lombok.Setter;
import me.fishy.abilities.commands.fAbilitiesCommand;
import me.fishy.abilities.commands.fServicesCommand;
import me.fishy.abilities.listeners.*;
import me.fishy.abilities.utils.CC;
import me.fishy.abilities.utils.Cooldown;
import me.conner.abilities.listeners.*;
import me.runic.abilities.listeners.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;

public class Main extends JavaPlugin {

	@Getter
	private Cooldown snowport = new Cooldown();

	@Getter
	private Cooldown tazer = new Cooldown();
	
	@Getter
	private Cooldown pocketbard = new Cooldown();

	@Getter
	private Cooldown cocaine = new Cooldown();

	@Getter
	private Cooldown switchstick = new Cooldown();

	@Getter
	private Cooldown instantinvis = new Cooldown();

	@Getter
	private Cooldown instantcrapple = new Cooldown();

	@Getter
	private Cooldown ninjastar = new Cooldown();

	@Getter
	private Cooldown rocket = new Cooldown();

	@Getter
	private Cooldown swapperaxe = new Cooldown();

	@Getter
	private Cooldown antibuildegg = new Cooldown();

	@Getter
	private Cooldown antibuildegghit = new Cooldown();

	@Getter
	private Cooldown pearlreset = new Cooldown();

	@Getter
	private Cooldown grapplinghook = new Cooldown();


	@Getter
	private static Main instance;

	@Override
	public void onEnable() {
		instance = this;
		registerCommands();
		registerListeners();
		this.saveDefaultConfig();
		//if (!new AdvancedLicense(Main.getInstance().getConfig().getString("License"),
				//"https://fishy-dev.000webhostapp.com/verify.php", this).register()) return;
			Bukkit.getConsoleSender().sendMessage(CC.translate("&2[fAbilities]&a has been enabled!"));

	}

	// Commands
	public void registerCommands() {
		getCommand("fabilities").setExecutor(new fAbilitiesCommand());
		getCommand("fservices").setExecutor(new fServicesCommand());

	}

	// Listeners
	public void registerListeners() {
		getServer().getPluginManager().registerEvents(new SnowportListener(), this);
		getServer().getPluginManager().registerEvents(new JoinListener(), this);
		getServer().getPluginManager().registerEvents(new CocaineListener(), this);
		getServer().getPluginManager().registerEvents(new PocketBardListener(), this);
		getServer().getPluginManager().registerEvents(new TazerListener(), this);
		getServer().getPluginManager().registerEvents(new SwitchStickListener(), this);
		getServer().getPluginManager().registerEvents(new InstantInvisListener(), this);
		getServer().getPluginManager().registerEvents(new InstantInvisListener(), this);
		getServer().getPluginManager().registerEvents(new InstantCrappleListener(), this);
		getServer().getPluginManager().registerEvents(new RocketListener(), this);
		getServer().getPluginManager().registerEvents(new SwapperAxeListener(), this);
		getServer().getPluginManager().registerEvents(new PearlResetListener(), this);
		getServer().getPluginManager().registerEvents(new FastPearlListener(), this);
		//getServer().getPluginManager().registerEvents(new GrapplingHookListener(), this);
		getServer().getPluginManager().registerEvents(new AntiBuildEggListener(), this);



	}
}
