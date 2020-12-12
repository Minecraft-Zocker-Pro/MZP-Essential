package minecraft.essential.zocker.pro.command.spawn;

import minecraft.core.zocker.pro.command.Command;
import minecraft.core.zocker.pro.command.SubCommand;
import minecraft.core.zocker.pro.config.Config;
import minecraft.essential.zocker.pro.Main;
import minecraft.essential.zocker.pro.util.Teleporter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SpawnCommand extends Command {

	private static final List<SubCommand> SUB_COMMAND_LIST = new ArrayList<>();
	private static Location spawnLocation;

	public SpawnCommand() {
		super("spawn", "mzp.essential.spawn", new ArrayList<>());

		SUB_COMMAND_LIST.add(new SpawnSetCommand());
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String[] args) {
		List<String> completions = new ArrayList<>();

		if (args.length == 0) {
			SUB_COMMAND_LIST.forEach(subCommand -> completions.add(subCommand.getName()));
		} else if (args.length == 1) {
			SUB_COMMAND_LIST.stream().filter(subCommand -> subCommand.getName().toLowerCase().startsWith(args[0].toLowerCase()))
				.forEach(subCommand -> completions.add(subCommand.getName()));
		} else {
			SubCommand command = findSubCommand(args[0]);

			if (command != null) {
				return command.getCompletions(sender, args);
			}
		}

		return completions;
	}

	@Override
	public void onExecute(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) return;

		if (args.length == 0) {
			if (spawnLocation == null) return;
			new Teleporter((Player) sender, spawnLocation, Main.ESSENTIAL_CONFIG.getInt("essential.spawn.cooldown")).teleport();
			return;
		}

		for (SubCommand subCommand : SUB_COMMAND_LIST) {
			if (subCommand.getName().equalsIgnoreCase(args[0])) {
				subCommand.execute(sender, args);
			}
		}
	}

	private SubCommand findSubCommand(String name) {
		return SUB_COMMAND_LIST.stream().filter(subCommand -> subCommand.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
	}

	public static void loadSpawnLocation() {
		Config config = Main.ESSENTIAL_CONFIG;

		String worldName = config.getString("essential.spawn.location.world");
		if (worldName == null) return;

		World w = Bukkit.getWorld(worldName);
		if (w == null) {
			System.out.println("Spawn not found! Please set the spawn with /spawn set");
			return;
		}
		
		double x = config.getDouble("essential.spawn.location.x");
		double y = config.getDouble("essential.spawn.location.y") + 1.0D;
		double z = config.getDouble("essential.spawn.location.z");
		float yaw = (float) config.getDouble("essential.spawn.location.yaw");
		float pitch = (float) config.getDouble("essential.spawn.location.pitch");

		spawnLocation = new Location(w, x, y, z, yaw, pitch);
	}

	public static Location getSpawnLocation() {
		return spawnLocation;
	}
}
