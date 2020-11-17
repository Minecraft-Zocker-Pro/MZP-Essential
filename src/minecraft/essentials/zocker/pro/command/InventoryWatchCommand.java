package minecraft.essentials.zocker.pro.command;

import minecraft.core.zocker.pro.command.Command;
import minecraft.core.zocker.pro.command.SubCommand;
import minecraft.core.zocker.pro.compatibility.CompatibleMessage;
import minecraft.core.zocker.pro.compatibility.CompatibleSound;
import minecraft.essentials.zocker.pro.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InventoryWatchCommand extends Command {

	private static final List<SubCommand> SUB_COMMAND_LIST = new ArrayList<>();

	public InventoryWatchCommand() {
		super("invsee", "mzp.essential.invsee", new String[]{"invsee"});
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String[] args) {
		List<String> completions = new ArrayList<>();

		if (args.length == 0) {
			completions.add("ec");
		}

		if (args.length == 1) {
			String partialPlayerName = args[0];

			int lastSpaceIndex = partialPlayerName.lastIndexOf(' ');
			if (lastSpaceIndex >= 0) {
				partialPlayerName = partialPlayerName.substring(lastSpaceIndex + 1);
			}

			for (Player p : Bukkit.getOnlinePlayers()) {
				if (p.getName().toLowerCase().startsWith(partialPlayerName)) {
					return Collections.singletonList(p.getName());
				}
			}
		}

		if (args.length == 2) {
			String partialPlayerName = args[1];

			int lastSpaceIndex = partialPlayerName.lastIndexOf(' ');
			if (lastSpaceIndex >= 0) {
				partialPlayerName = partialPlayerName.substring(lastSpaceIndex + 1);
			}

			for (Player p : Bukkit.getOnlinePlayers()) {
				if (p.getName().toLowerCase().startsWith(partialPlayerName)) {
					return Collections.singletonList(p.getName());
				}
			}
		}

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

	@Override
	public void onExecute(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) return;

		Player player = (Player) sender;
		if (args.length == 0) {
			CompatibleSound.playErrorSound(player);
			return;
		}

		if (args.length == 1) {
			Player target = Bukkit.getPlayer(args[0]);
			if (target == null || !target.isOnline()) {
				CompatibleMessage.sendMessage(player, Main.ESSENTIAL_MESSAGE.getString("essential.prefix") + Main.ESSENTIAL_MESSAGE.getString("essential.general.offline").replace("%target%", args[0]));
				CompatibleSound.playErrorSound(player);
				return;
			}

			new BukkitRunnable() {
				@Override
				public void run() {
					player.playSound(player.getLocation(), CompatibleSound.BLOCK_CHEST_OPEN.getSound(), 2, 2);
					player.openInventory(target.getInventory());
				}
			}.runTask(Main.getPlugin());
			return;
		}

		if (args.length == 2) {
			if (args[0].equalsIgnoreCase("ec")) {
				Player target = Bukkit.getPlayer(args[1]);
				if (target == null || !target.isOnline()) {
					CompatibleMessage.sendMessage(player, Main.ESSENTIAL_MESSAGE.getString("essential.prefix") + Main.ESSENTIAL_MESSAGE.getString("essential.general.offline").replace("%target%", args[0]));
					CompatibleSound.playErrorSound(player);
					return;
				}

				new BukkitRunnable() {
					@Override
					public void run() {
						player.playSound(player.getLocation(), CompatibleSound.BLOCK_CHEST_OPEN.getSound(), 2, 2);
						player.openInventory(target.getEnderChest());
					}
				}.runTask(Main.getPlugin());
			}
		}
	}
}
