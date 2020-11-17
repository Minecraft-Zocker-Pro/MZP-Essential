package minecraft.essentials.zocker.pro.command;

import minecraft.core.zocker.pro.command.Command;
import minecraft.core.zocker.pro.command.SubCommand;
import minecraft.core.zocker.pro.compatibility.CompatibleMessage;
import minecraft.core.zocker.pro.compatibility.CompatibleSound;
import minecraft.essentials.zocker.pro.Main;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GamemodeCommand extends Command {

	private static final List<SubCommand> SUB_COMMAND_LIST = new ArrayList<>();
	private static final List<String> COMPLETIONS = Arrays.asList("creative", "survival", "adventure", "spectator");

	public GamemodeCommand() {
		super("gamemode", "mzp.essential.gamemode", new String[]{"gm"});
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String[] args) {

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
			SUB_COMMAND_LIST.forEach(subCommand -> COMPLETIONS.add(subCommand.getName()));
		} else if (args.length == 1) {
			SUB_COMMAND_LIST.stream().filter(subCommand -> subCommand.getName().toLowerCase().startsWith(args[0].toLowerCase()))
				.forEach(subCommand -> COMPLETIONS.add(subCommand.getName()));
		} else {
			SubCommand command = findSubCommand(args[0]);

			if (command != null) {
				return command.getCompletions(sender, args);
			}
		}

		return COMPLETIONS;
	}

	private SubCommand findSubCommand(String name) {
		return SUB_COMMAND_LIST.stream().filter(subCommand -> subCommand.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
	}

	@Override
	public void onExecute(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) return;

		Player player = (Player) sender;

		if (args.length == 0) {
			if (player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.ADVENTURE) {
				changeGamemode(player, GameMode.CREATIVE);
				return;
			}

			changeGamemode(player, GameMode.SURVIVAL);
			return;
		}

		if (args.length == 1) {
			String argument = args[0];
			switch (argument) {
				case "0": {
					changeGamemode(player, GameMode.SURVIVAL);
					return;
				}

				case "s": {
					changeGamemode(player, GameMode.SURVIVAL);
					return;
				}

				case "survival": {
					changeGamemode(player, GameMode.SURVIVAL);
					return;
				}

				case "1": {
					changeGamemode(player, GameMode.CREATIVE);
					return;
				}

				case "c": {
					changeGamemode(player, GameMode.CREATIVE);
					return;
				}

				case "creative": {
					changeGamemode(player, GameMode.CREATIVE);
					return;
				}

				case "2": {
					changeGamemode(player, GameMode.ADVENTURE);
					return;
				}

				case "a": {
					changeGamemode(player, GameMode.ADVENTURE);
					return;
				}

				case "adventure": {
					changeGamemode(player, GameMode.ADVENTURE);
					return;
				}

				case "3": {
					changeGamemode(player, GameMode.SPECTATOR);
					return;
				}

				case "sp": {
					changeGamemode(player, GameMode.SPECTATOR);
					return;
				}

				case "spectator": {
					changeGamemode(player, GameMode.SPECTATOR);
					return;
				}

				default: {
					changeGamemode(player, GameMode.SURVIVAL);
				}
			}
		}

		if (args.length == 2) {
			Player target = Bukkit.getPlayer(args[1]);
			if (target == null || !target.isOnline()) {
				CompatibleMessage.sendMessage(player, Main.ESSENTIAL_MESSAGE.getString("essential.prefix") + Main.ESSENTIAL_MESSAGE.getString("essential.general.offline").replace("%target%", args[1]));
				CompatibleSound.playErrorSound(player);
				return;
			}

			if (player == target) {
				CompatibleSound.playErrorSound(player);
				return;
			}

			if (!player.hasPermission("mzp.essential.gamemode.other")) {
				CompatibleSound.playErrorSound(player);
				return;
			}

			String argument = args[0];
			switch (argument) {
				case "0": {
					changeGamemode(target, GameMode.SURVIVAL);
					CompatibleMessage.sendMessage(player, Main.ESSENTIAL_MESSAGE.getString("essential.prefix") + Main.ESSENTIAL_MESSAGE.getString("essential.gamemode.changed.other").replace("%target%", target.getName()).replace("%gamemode%", "Survival"));
					CompatibleSound.playSuccessSound(player);
					return;
				}

				case "s": {
					changeGamemode(target, GameMode.SURVIVAL);
					CompatibleMessage.sendMessage(player, Main.ESSENTIAL_MESSAGE.getString("essential.prefix") + Main.ESSENTIAL_MESSAGE.getString("essential.gamemode.changed.other").replace("%target%", target.getName()).replace("%gamemode%", "Survival"));

					CompatibleSound.playSuccessSound(player);
					return;
				}

				case "survival": {
					changeGamemode(target, GameMode.SURVIVAL);
					CompatibleMessage.sendMessage(player, Main.ESSENTIAL_MESSAGE.getString("essential.prefix") + Main.ESSENTIAL_MESSAGE.getString("essential.gamemode.changed.other").replace("%target%", target.getName()).replace("%gamemode%", "Survival"));
					CompatibleSound.playSuccessSound(player);
					return;
				}

				case "1": {
					changeGamemode(target, GameMode.CREATIVE);
					CompatibleMessage.sendMessage(player, Main.ESSENTIAL_MESSAGE.getString("essential.prefix") + Main.ESSENTIAL_MESSAGE.getString("essential.gamemode.changed.other").replace("%target%", target.getName()).replace("%gamemode%", "Creative"));
					CompatibleSound.playSuccessSound(player);
					return;
				}

				case "c": {
					changeGamemode(target, GameMode.CREATIVE);
					CompatibleMessage.sendMessage(player, Main.ESSENTIAL_MESSAGE.getString("essential.prefix") + Main.ESSENTIAL_MESSAGE.getString("essential.gamemode.changed.other").replace("%target%", target.getName()).replace("%gamemode%", "Creative"));
					CompatibleSound.playSuccessSound(player);
					return;
				}

				case "creative": {
					changeGamemode(target, GameMode.CREATIVE);
					CompatibleMessage.sendMessage(player, Main.ESSENTIAL_MESSAGE.getString("essential.prefix") + Main.ESSENTIAL_MESSAGE.getString("essential.gamemode.changed.other").replace("%target%", target.getName()).replace("%gamemode%", "Creative"));
					CompatibleSound.playSuccessSound(player);
					return;
				}

				case "2": {
					changeGamemode(target, GameMode.ADVENTURE);
					CompatibleMessage.sendMessage(player, Main.ESSENTIAL_MESSAGE.getString("essential.prefix") + Main.ESSENTIAL_MESSAGE.getString("essential.gamemode.changed.other").replace("%target%", target.getName()).replace("%gamemode%", "Adventure"));
					CompatibleSound.playSuccessSound(player);
					return;
				}

				case "a": {
					changeGamemode(target, GameMode.ADVENTURE);
					CompatibleMessage.sendMessage(player, Main.ESSENTIAL_MESSAGE.getString("essential.prefix") + Main.ESSENTIAL_MESSAGE.getString("essential.gamemode.changed.other").replace("%target%", target.getName()).replace("%gamemode%", "Adventure"));
					CompatibleSound.playSuccessSound(player);
					return;
				}

				case "adventure": {
					changeGamemode(target, GameMode.ADVENTURE);
					CompatibleMessage.sendMessage(player, Main.ESSENTIAL_MESSAGE.getString("essential.prefix") + Main.ESSENTIAL_MESSAGE.getString("essential.gamemode.changed.other").replace("%target%", target.getName()).replace("%gamemode%", "Adventure"));
					CompatibleSound.playSuccessSound(player);
					return;
				}

				case "3": {
					changeGamemode(target, GameMode.SPECTATOR);
					CompatibleMessage.sendMessage(player, Main.ESSENTIAL_MESSAGE.getString("essential.prefix") + Main.ESSENTIAL_MESSAGE.getString("essential.gamemode.changed.other").replace("%target%", target.getName()).replace("%gamemode%", "Spectator"));
					CompatibleSound.playSuccessSound(player);
					return;
				}

				case "sp": {
					changeGamemode(target, GameMode.SPECTATOR);
					CompatibleMessage.sendMessage(player, Main.ESSENTIAL_MESSAGE.getString("essential.prefix") + Main.ESSENTIAL_MESSAGE.getString("essential.gamemode.changed.other").replace("%target%", target.getName()).replace("%gamemode%", "Spectator"));
					CompatibleSound.playSuccessSound(player);
					return;
				}

				case "spectator": {
					changeGamemode(target, GameMode.SPECTATOR);
					CompatibleMessage.sendMessage(player, Main.ESSENTIAL_MESSAGE.getString("essential.prefix") + Main.ESSENTIAL_MESSAGE.getString("essential.gamemode.changed.other").replace("%target%", target.getName()).replace("%gamemode%", "Spectator"));
					CompatibleSound.playSuccessSound(player);
					return;
				}

				default: {
					changeGamemode(target, GameMode.SURVIVAL);
					CompatibleMessage.sendMessage(player, Main.ESSENTIAL_MESSAGE.getString("essential.prefix") + Main.ESSENTIAL_MESSAGE.getString("essential.gamemode.changed.other").replace("%target%", target.getName()).replace("%gamemode%", "Survvial"));
					CompatibleSound.playSuccessSound(player);
				}
			}
		}
	}

	private void changeGamemode(Player player, GameMode gamemode) {
		if (player == null) return;
		if (player.getGameMode() == gamemode) return;

		new BukkitRunnable() {
			@Override
			public void run() {
				player.setGameMode(gamemode);
				CompatibleMessage.sendMessage(player, Main.ESSENTIAL_MESSAGE.getString("essential.prefix") + Main.ESSENTIAL_MESSAGE.getString("essential.gamemode.changed.self").replace("%gamemode%", gamemode.name()));
				CompatibleSound.playSuccessSound(player);
			}
		}.runTask(Main.getPlugin());
	}
}
