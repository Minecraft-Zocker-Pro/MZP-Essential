package minecraft.essentials.zocker.pro.command.home;

import minecraft.core.zocker.pro.Zocker;
import minecraft.core.zocker.pro.command.Command;
import minecraft.core.zocker.pro.command.SubCommand;
import minecraft.core.zocker.pro.compatibility.CompatibleMessage;
import minecraft.core.zocker.pro.compatibility.CompatibleSound;
import minecraft.essentials.zocker.pro.Main;
import minecraft.essentials.zocker.pro.home.HomeListInventory;
import minecraft.essentials.zocker.pro.home.HomeZocker;
import minecraft.essentials.zocker.pro.util.Teleporter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class HomeCommand extends Command {

	private static final List<SubCommand> SUB_COMMAND_LIST = new ArrayList<>();

	public HomeCommand() {
		super("home", "mzp.essential.home", new String[]{"homes"});

		SUB_COMMAND_LIST.add(new HomeCreateCommand());
		SUB_COMMAND_LIST.add(new HomeDeleteCommand());
	}

	@Override
	public void onExecute(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) return;

		Player player = (Player) sender;
		Zocker zocker = Zocker.getZocker(player.getUniqueId());

		if (args.length == 0) {
			new HomeListInventory(zocker).open(zocker);
			return;
		}

		for (SubCommand subCommand : SUB_COMMAND_LIST) {
			if (subCommand.getName().equalsIgnoreCase(args[0])) {
				subCommand.execute(sender, args);
				return;
			}
		}

		String homeName = args[0];
		if (homeName == null) return;

		HomeZocker homeZocker = new HomeZocker(zocker.getUUID());
		String homeUUID = homeZocker.getHomeUUID(homeName);

		if (homeUUID == null) {
			CompatibleMessage.sendMessage(player, Main.ESSENTIAL_MESSAGE.getString("essential.prefix") + Main.ESSENTIAL_MESSAGE.getString("essential.home.notfound").replace("%home%", homeName));
			CompatibleSound.playErrorSound(player);
			return;
		}

		Teleporter teleporter = new Teleporter(player, homeZocker.getHome(homeUUID).getLocation(), Main.ESSENTIAL_CONFIG.getInt("essential.home.cooldown"), false);
		teleporter.teleport();
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

	private SubCommand findSubCommand(String name) {
		return SUB_COMMAND_LIST.stream().filter(subCommand -> subCommand.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
	}
}
