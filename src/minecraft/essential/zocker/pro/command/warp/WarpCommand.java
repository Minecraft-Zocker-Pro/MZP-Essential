package minecraft.essential.zocker.pro.command.warp;

import minecraft.core.zocker.pro.Zocker;
import minecraft.core.zocker.pro.command.Command;
import minecraft.core.zocker.pro.command.SubCommand;
import minecraft.core.zocker.pro.compatibility.CompatibleMessage;
import minecraft.core.zocker.pro.compatibility.CompatibleSound;
import minecraft.essential.zocker.pro.Main;
import minecraft.essential.zocker.pro.util.Teleporter;
import minecraft.essential.zocker.pro.warp.Warp;
import minecraft.essential.zocker.pro.warp.WarpListInventory;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class WarpCommand extends Command {

	private static final List<SubCommand> SUB_COMMAND_LIST = new ArrayList<>();

	public WarpCommand() {
		super("warp", "mzp.essential.warp", new String[]{"warps"});

		SUB_COMMAND_LIST.add(new WarpCreateCommand());
		SUB_COMMAND_LIST.add(new WarpDeleteCommand());
		SUB_COMMAND_LIST.add(new WarpEditCommand());
	}

	@Override
	public void onExecute(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) return;

		if (args.length > 0) {
			for (SubCommand subCommand : SUB_COMMAND_LIST) {
				if (subCommand.getName().equalsIgnoreCase(args[0])) {
					subCommand.execute(sender, args);
					return;
				}
			}
		}

		Player player = (Player) sender;
		if (args.length == 0) {
			Zocker zocker = Zocker.getZocker(player.getUniqueId());
			new WarpListInventory(zocker).open(zocker);
			return;
		}


		if (args.length == 1) {
			Warp warp = Warp.getWarp(args[0]);
			if (warp == null) {
				CompatibleMessage.sendMessage(player, Main.ESSENTIAL_MESSAGE.getString("essential.prefix") + Main.ESSENTIAL_MESSAGE.getString("essential.warp.notfound").replace("%warp%", args[0]));
				CompatibleSound.playErrorSound(player);
				return;
			}

			if (player.hasPermission(warp.getPermission())) {
				if (warp.getPrice() == 0) {
					handleTeleport(player, warp);
				} else {
					if (Main.getEconomy().getBalance(player) >= warp.getPrice()) {
						Main.getEconomy().withdrawPlayer(player, warp.getPrice());

						handleTeleport(player, warp);

						CompatibleSound.playSuccessSound(player);
						return;
					}

					CompatibleMessage.sendMessage(player, Main.ESSENTIAL_MESSAGE.getString("essential.prefix") + Main.ESSENTIAL_MESSAGE.getString("essential.warp.notenough"));
					CompatibleSound.playErrorSound(player);
				}

				return;
			}

			CompatibleSound.playErrorSound(player);
		}
	}

	private void handleTeleport(Player player, Warp warp) {
		Teleporter teleporter = new Teleporter(player, warp.getLocation(), warp.getCooldown(), true);
		teleporter.setTeleporterListener(new Teleporter.TeleporterListener() {
			@Override
			public void onCanceled() {
				if (warp.getPrice() > 0) {
					Main.getEconomy().depositPlayer(player, warp.getPrice());
				}
			}

			@Override
			public void onTeleported() {
				if (warp.getCommands() != null && !warp.getCommands().isEmpty()) {
					for (String command : warp.getCommands()) {
						String cmd;
						if (command.startsWith("[c]")) {
							cmd = command.substring(3);
							cmd = cmd
								.replace("%player%", player.getName())
								.replace("%price%", String.valueOf(warp.getPrice()))
								.replace("%cooldown%", String.valueOf(warp.getCooldown()));
							Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
						} else if (command.startsWith("[p]")) {
							cmd = command.substring(3);
							player.performCommand(cmd);
						} else {
							player.performCommand(command);
						}
					}
				}

				List<String> title = warp.getTitle();
				if (title != null && !title.isEmpty()) {
					String titleTranslated = title.get(0)
						.replace("%player%", player.getName())
						.replace("%price%", String.valueOf(warp.getPrice()))
						.replace("%cooldown%", String.valueOf(warp.getCooldown()));

					if (title.size() <= 1) {
						CompatibleMessage.sendTitle(player, titleTranslated, "");
					} else {
						String subTitleTranslated = title.get(1)
							.replace("%player%", player.getName())
							.replace("%price%", String.valueOf(warp.getPrice()))
							.replace("%cooldown%", String.valueOf(warp.getCooldown()));
						CompatibleMessage.sendTitle(player, titleTranslated, subTitleTranslated);
					}
				}
			}
		});

		teleporter.teleport();
	}


	@Override
	public List<String> onTabComplete(CommandSender sender, String[] args) {
		List<String> completions = new ArrayList<>();

		for (Player player : Bukkit.getOnlinePlayers()) {
			if (player == null) continue;
			if (sender.getName().equals(player.getName())) continue;

			completions.add(player.getName());
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
}
