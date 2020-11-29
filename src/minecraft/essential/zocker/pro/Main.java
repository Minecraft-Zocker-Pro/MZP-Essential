package minecraft.essential.zocker.pro;

import minecraft.core.zocker.pro.CorePlugin;
import minecraft.core.zocker.pro.compatibility.CompatibleMaterial;
import minecraft.core.zocker.pro.config.Config;
import minecraft.core.zocker.pro.storage.StorageManager;
import minecraft.essential.zocker.pro.command.*;
import minecraft.essential.zocker.pro.command.home.HomeCommand;
import minecraft.essential.zocker.pro.command.spawn.SpawnCommand;
import minecraft.essential.zocker.pro.command.teleport.TeleportCommand;
import minecraft.essential.zocker.pro.command.warp.WarpCommand;
import minecraft.essential.zocker.pro.listener.PlayerDisconnectPositionListener;
import minecraft.essential.zocker.pro.listener.PlayerRespawnListener;
import minecraft.essential.zocker.pro.listener.ZockerDataInitializeListener;
import minecraft.essential.zocker.pro.warp.Warp;
import minecraft.essential.zocker.pro.command.teleport.TeleportRequestCommand;
import minecraft.essential.zocker.pro.listener.PlayerDisconnectListener;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Optional;

public class Main extends CorePlugin {

	private static Economy ECONOMY;
	public static Config ESSENTIAL_CONFIG;
	public static Config ESSENTIAL_MESSAGE;
	public static String ESSENTIAL_WARP_DATABASE_TABLE;
	public static String ESSENTIAL_HOME_DATABASE_TABLE;

	public static String ESSENTIAL_POSITION_DATABASE_TABLE;

	private static CorePlugin PLUGIN;

	@Override
	public void onEnable() {
		super.onEnable();
		super.setDisplayItem(CompatibleMaterial.DARK_OAK_SAPLING.getMaterial());
		super.setPluginName("MZP-Essential");

		PLUGIN = this;

		if (!Bukkit.getPluginManager().isPluginEnabled("MZP-Core")) {
			System.out.println("Disabled due to no MZP-Core dependency found!");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}

		if (!setupEconomy()) {
			System.out.println("Disabled due to no Vault dependency found!");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}

		this.buildConfig();
		this.verifyDatabase();
		this.registerCommand();
		this.registerListener();

		new BukkitRunnable() {
			@Override
			public void run() {
				SpawnCommand.loadSpawnLocation();
				Warp.loadWarps();
			}
		}.runTaskLater(this, 20);
	}

	@Override
	public void reload() {
		HandlerList.unregisterAll(this);

		ESSENTIAL_CONFIG.reload();
		ESSENTIAL_MESSAGE.reload();

		Warp.loadWarps();

		this.registerListener();
		this.registerCommand();
	}

	@Override
	public void buildConfig() {
		// Config
		ESSENTIAL_CONFIG = new Config("essential.yml", this.getPluginName());

		// Global or Per Server
		ESSENTIAL_CONFIG.set("essential.global", true, "0.0.1");

		// Spectate
		ESSENTIAL_CONFIG.set("essential.spectate.enabled", true, "0.0.1");

		// Teleport
		ESSENTIAL_CONFIG.set("essential.teleport.cooldown", 10, "0.0.1");
		ESSENTIAL_CONFIG.set("essential.teleport.request.enabled", true, "0.0.1");

		// Warp
		ESSENTIAL_CONFIG.set("essential.warp.enabled", true, "0.0.1");
		ESSENTIAL_CONFIG.set("essential.warp.size", 45, "0.0.1");

		// Speed
		ESSENTIAL_CONFIG.set("essential.speed.enabled", true, "0.0.1");
		ESSENTIAL_CONFIG.set("essential.speed.max", 0.5, "0.0.1");

		// Home
		ESSENTIAL_CONFIG.set("essential.home.enabled", true, "0.0.1");
		ESSENTIAL_CONFIG.set("essential.home.cooldown", 10, "0.0.1");
		ESSENTIAL_CONFIG.set("essential.home.limit", 21, "0.0.1");
		ESSENTIAL_CONFIG.set("essential.home.size", 45, "0.0.1");
		ESSENTIAL_CONFIG.set("essential.home.world.blacklist", new String[]{"my_world", "my_world_nether"}, "0.0.1");

		// Spawn
		ESSENTIAL_CONFIG.set("essential.spawn.enabled", true, "0.0.7");
		ESSENTIAL_CONFIG.set("essential.spawn.force", false, "0.0.7");
		ESSENTIAL_CONFIG.set("essential.spawn.cooldown", 0, "0.0.7");
		ESSENTIAL_CONFIG.set("essential.spawn.sync.enabled", false, "0.0.7");
		ESSENTIAL_CONFIG.set("essential.spawn.sync.wait", 1000, "0.0.7");

		ESSENTIAL_CONFIG.setVersion("0.0.7", true);

		// Message
		ESSENTIAL_MESSAGE = new Config("message.yml", this.getPluginName());

		ESSENTIAL_MESSAGE.set("essential.prefix", "&6&l[MZP]&3 ", "0.0.1");
		ESSENTIAL_MESSAGE.set("essential.general.offline", "&6%target%&3 is not online.", "0.0.1");

		// Teleport
		ESSENTIAL_MESSAGE.set("essential.teleport.success", "&3Successfully teleported to &6%target%&3.", "0.0.1");
		ESSENTIAL_MESSAGE.set("essential.teleport.denied.other", "&6%target%&3 denied your teleport request.", "0.0.1");
		ESSENTIAL_MESSAGE.set("essential.teleport.denied.self", "&6You&3 denied the teleport request.", "0.0.1");
		ESSENTIAL_MESSAGE.set("essential.teleport.canceled", "&3You''ve moved. Teleport &6canceled!", "0.0.1");
		ESSENTIAL_MESSAGE.set("essential.teleport.accepted.other", "&6%target% &3accepted your teleport request.", "0.0.1");
		ESSENTIAL_MESSAGE.set("essential.teleport.accepted.self", "&6You &3accepted the teleport request.", "0.0.1");
		ESSENTIAL_MESSAGE.set("essential.teleport.expired", "&3Your request has timed out.", "0.0.1");
		ESSENTIAL_MESSAGE.set("essential.teleport.empty", "&3There is no teleport request available.", "0.0.1");
		ESSENTIAL_MESSAGE.set("essential.teleport.request.sent", "&3Teleport request sent to &6%target%&3.", "0.0.1");
		ESSENTIAL_MESSAGE.set("essential.teleport.request.received", "&6You &3received from &6%target% &3a teleport request. \n&3Accept it with &6/tpa accept&3", "0.0.1");
		ESSENTIAL_MESSAGE.set("essential.teleport.already", "&6%target% &3already have a running teleport request.", "0.0.1");
		ESSENTIAL_MESSAGE.set("essential.teleport.usage", "&3Send teleport request with &6/tpa <player>", "0.0.1");
		ESSENTIAL_MESSAGE.set("essential.teleport.teleporting", "&3Teleporting in &6%cooldown% seconds&3", "0.0.1");
		ESSENTIAL_MESSAGE.set("essential.teleport.offline", "&6%target%&3 is not online.", "0.0.1");

		// Spectate
		ESSENTIAL_MESSAGE.set("essential.spectate.enabled", "&3Spectate mode enabled.", "0.0.1");
		ESSENTIAL_MESSAGE.set("essential.spectate.disabled", "&3Spectate mode disabled.", "0.0.1");

		// Gamemode
		ESSENTIAL_MESSAGE.set("essential.gamemode.changed.self", "&3Successfully changed gamemode to &6%gamemode%&3.", "0.0.1");
		ESSENTIAL_MESSAGE.set("essential.gamemode.changed.other", "&3Successfully changed &6%target% &3gamemode to &6%gamemode%&3.", "0.0.1");

		// Spawn
		ESSENTIAL_MESSAGE.set("essential.warp.set", "&3Spawn set.", "0.0.7");

		// Warp
		ESSENTIAL_MESSAGE.set("essential.warp.created", "&3Warp created.", "0.0.1");
		ESSENTIAL_MESSAGE.set("essential.warp.deleted", "&3Warp deleted.", "0.0.1");
		ESSENTIAL_MESSAGE.set("essential.warp.usage", "&3Type &6/warp <name>", "0.0.1");
		ESSENTIAL_MESSAGE.set("essential.warp.notenough", "&6You &3dont have enough money to teleport.", "0.0.1");
		ESSENTIAL_MESSAGE.set("essential.warp.notfound", "&3Warp &6%warp% &3not found!", "0.0.1");
		ESSENTIAL_MESSAGE.set("essential.warp.exist", "&3Warp &6%warp% &3already exists!", "0.0.1");

		// Speed
		ESSENTIAL_MESSAGE.set("essential.speed.max", "&6You &3cant set the speed higher as &6%max%&3.", "0.0.1");
		ESSENTIAL_MESSAGE.set("essential.speed.changed", "&3Changed fly speed to &6%speed%&3.", "0.0.1");
		ESSENTIAL_MESSAGE.set("essential.speed.usage", "&3Type &6/speed <speed>&3 to change your fly speed.", "0.0.1");

		// Home
		ESSENTIAL_MESSAGE.set("essential.home.created", "&6You &3created an new home.", "0.0.1");
		ESSENTIAL_MESSAGE.set("essential.home.deleted", "&6You &3deleted the home &6%home%&3.", "0.0.1");
		ESSENTIAL_MESSAGE.set("essential.home.teleporting", "&6Teleporting to &6%home%&3.", "0.0.1");
		ESSENTIAL_MESSAGE.set("essential.home.max", "&3Failed to create a new home, because you reached the max limit!", "0.0.1");
		ESSENTIAL_MESSAGE.set("essential.home.exist", "&6%home% &3already created!", "0.0.1");
		ESSENTIAL_MESSAGE.set("essential.home.notfound", "&6%home% &3not found!", "0.0.1");
		ESSENTIAL_MESSAGE.set("essential.home.blacklist", "&3You cant set in this world your home!", "0.0.1");

		// Inventory
		ESSENTIAL_MESSAGE.set("essential.inventory.home.info.title", "&6&lInformation", "0.0.1");
		ESSENTIAL_MESSAGE.set("essential.inventory.home.info.description", new String[]{
				"&3You can do following actions",
				"",
				"&3Create home with &6/home create <name>",
				"",
				"&3Left click: &6Teleport",
				"&3Right click: &6Teleport",
				"&3Middle click: &6Edit"},
			"0.0.1");

		ESSENTIAL_MESSAGE.set("essential.inventory.warp.info.player.title", "&6&lInformation", "0.0.1");
		ESSENTIAL_MESSAGE.set("essential.inventory.warp.info.player.description", new String[]{
				"",
				"&3Left click: &6Teleport",
				"&3Right click: &6Teleport"},
			"0.0.1");

		ESSENTIAL_MESSAGE.set("essential.inventory.warp.info.admin.title", "&6&lInformation", "0.0.1");
		ESSENTIAL_MESSAGE.set("essential.inventory.warp.info.admin.description", new String[]{
				"",
				"&3Left click: &6Teleport",
				"&3Right click: &6Teleport",
				"&3Middle click: &6Edit"},
			"0.0.1");

		ESSENTIAL_MESSAGE.setVersion("0.0.7", true);
	}

	private void verifyDatabase() {
		if (ESSENTIAL_CONFIG.getBool("essential.global")) {
			ESSENTIAL_WARP_DATABASE_TABLE = "player_essential_warp";
			ESSENTIAL_HOME_DATABASE_TABLE = "player_essential_home";
			ESSENTIAL_POSITION_DATABASE_TABLE = "player_essential_position";
		} else {
			ESSENTIAL_WARP_DATABASE_TABLE = "player_essential_warp_" + StorageManager.getServerName();
			ESSENTIAL_HOME_DATABASE_TABLE = "player_essential_home_" + StorageManager.getServerName();
			ESSENTIAL_POSITION_DATABASE_TABLE = "player_essential_position_" + StorageManager.getServerName();
		}

		String createWarpTable, createHomeTable, createLocationTable;

		createWarpTable = "CREATE TABLE IF NOT EXISTS `" + ESSENTIAL_WARP_DATABASE_TABLE + "` (`name` varchar(48) NOT NULL UNIQUE, `display` varchar(48) DEFAULT NULL, `lore` varchar(255) DEFAULT NULL, `enabled` tinyint(4) DEFAULT TRUE, `price` double DEFAULT 0, `permission` varchar(255) DEFAULT " +
			"NULL, `slot` int(11) DEFAULT 0, `cooldown` int(11) DEFAULT 0, `material` varchar(64) DEFAULT 'ENDER_PEARL', `command` varchar(255) DEFAULT NULL, `title` varchar(255) DEFAULT NULL, `location_world` varchar(36) NOT NULL, `location_x` double NOT NULL, `location_y` double NOT NULL, `location_z` double NOT NULL, " +
			"`location_pitch` float NOT NULL, `location_yaw` float NOT NULL);";

		createHomeTable = "CREATE TABLE IF NOT EXISTS `" + ESSENTIAL_HOME_DATABASE_TABLE + "` (`player_uuid` varchar(36) NOT NULL,`home_uuid` varchar(36) NOT NULL UNIQUE,`name` varchar(48) DEFAULT NULL,`public` tinyint(4) DEFAULT FALSE, `material` varchar(64) DEFAULT 'ENDER_PEARL',`location_world` " +
			"varchar(36) NOT NULL,`location_x` double NOT NULL,`location_y` double NOT NULL,`location_z` double NOT NULL,`location_pitch` float NOT NULL,`location_yaw` float NOT NULL,FOREIGN KEY (player_uuid) REFERENCES player (uuid) ON DELETE CASCADE);";

		String createHomeIndex = "CREATE INDEX IF NOT EXISTS `player_uuid` ON `" + ESSENTIAL_HOME_DATABASE_TABLE + "` (`player_uuid`);";

		createLocationTable = "CREATE TABLE IF NOT EXISTS `" + ESSENTIAL_POSITION_DATABASE_TABLE + "` (`player_uuid` varchar(36) NOT NULL,`position_world` varchar(36) NOT NULL,`position_x` double NOT NULL,`position_y` double NOT NULL,`position_z` double NOT NULL,`position_pitch` float NOT NULL," +
			"`position_yaw` float NOT NULL,FOREIGN KEY (player_uuid) REFERENCES player (uuid) ON DELETE CASCADE);";

		if (StorageManager.isMySQL()) {
			assert StorageManager.getMySQLDatabase() != null : "Create table failed.";
			StorageManager.getMySQLDatabase().createTable(createWarpTable);
			StorageManager.getMySQLDatabase().createTable(createLocationTable);

			StorageManager.getMySQLDatabase().createTable(createHomeTable);
			StorageManager.getMySQLDatabase().createTable(createHomeIndex);
			return;
		}

		assert StorageManager.getSQLiteDatabase() != null : "Create table failed.";
		StorageManager.getSQLiteDatabase().createTable(createWarpTable);
		StorageManager.getSQLiteDatabase().createTable(createLocationTable);

		StorageManager.getSQLiteDatabase().createTable(createHomeTable);
		StorageManager.getSQLiteDatabase().createTable(createHomeIndex);
	}


	@Override
	public void registerCommand() {
		if (Optional.of(ESSENTIAL_CONFIG.getBool("essential.teleport.request.enabled")).orElse(true)) {
			getCommand("teleport").setExecutor(new TeleportCommand());
			getCommand("tpa").setExecutor(new TeleportRequestCommand());
		}

		if (Optional.of(ESSENTIAL_CONFIG.getBool("essential.spectate.enabled")).orElse(true)) {
			getCommand("spectate").setExecutor(new SpectateCommand());
		}

		getCommand("gamemode").setExecutor(new GamemodeCommand());
		getCommand("invsee").setExecutor(new InventoryWatchCommand());

		if (Optional.of(ESSENTIAL_CONFIG.getBool("essential.warp.enabled")).orElse(true)) {
			getCommand("warp").setExecutor(new WarpCommand());
		}

		if (Optional.of(ESSENTIAL_CONFIG.getBool("essential.speed.enabled")).orElse(true)) {
			getCommand("speed").setExecutor(new SpeedCommand());
		}

		if (Optional.of(ESSENTIAL_CONFIG.getBool("essential.home.enabled")).orElse(true)) {
			getCommand("home").setExecutor(new HomeCommand());
		}

		if (Optional.of(ESSENTIAL_CONFIG.getBool("essential.spawn.enabled")).orElse(true)) {
			getCommand("spawn").setExecutor(new SpawnCommand());
		}

		getCommand("day").setExecutor(new DayCommand());
		getCommand("night").setExecutor(new NightCommand());
		getCommand("skull").setExecutor(new SkullCommand());
	}

	private boolean setupEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}

		ECONOMY = rsp.getProvider();
		return true;
	}

	@Override
	public void registerListener() {
		PluginManager pluginManager = Bukkit.getPluginManager();

		if (ESSENTIAL_CONFIG.getBool("essential.teleport.request.enabled") || ESSENTIAL_CONFIG.getBool("essential.spectate.enabled")) {
			pluginManager.registerEvents(new PlayerDisconnectListener(), this);
		}

		if (ESSENTIAL_CONFIG.getBool("essential.spawn.enabled")) {
			if (ESSENTIAL_CONFIG.getBool("essential.spawn.sync.enabled")) {
				pluginManager.registerEvents(new PlayerDisconnectPositionListener(), this);
			}

			pluginManager.registerEvents(new ZockerDataInitializeListener(), this);
			pluginManager.registerEvents(new PlayerRespawnListener(), this);
		}
	}

	public static CorePlugin getPlugin() {
		return PLUGIN;
	}

	public static Economy getEconomy() {
		return ECONOMY;
	}
}
