package minecraft.essentials.zocker.pro.command.teleport;

import minecraft.core.zocker.pro.command.Command;
import minecraft.core.zocker.pro.command.SubCommand;
import minecraft.core.zocker.pro.compatibility.CompatibleMessage;
import minecraft.core.zocker.pro.compatibility.CompatibleSound;
import minecraft.essentials.zocker.pro.Main;
import net.md_5.bungee.api.chat.*;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class TeleportRequestCommand extends Command {

	public static final HashMap<UUID, UUID> TELEPORT_REQUESTS = new HashMap<UUID, UUID>();
	private static final List<SubCommand> SUB_COMMAND_LIST = new ArrayList<>();


	public TeleportRequestCommand() {
		super("tpa", "mzp.essential.teleport.request", new String[]{null});

		SUB_COMMAND_LIST.add(new TeleportDenyCommand());
		SUB_COMMAND_LIST.add(new TeleportAcceptCommand());
	}

	@Override
	public void onExecute(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) return;
		Player player = (Player) sender;

		if (args.length == 1) {
			for (SubCommand subCommand : SUB_COMMAND_LIST) {
				if (subCommand.getName().equalsIgnoreCase(args[0])) {
					subCommand.execute(sender, args);
					return;
				}
			}

			Player target = Bukkit.getPlayer(args[0]);
			if (target == null || !target.isOnline()) {
				CompatibleSound.playErrorSound(player);
				CompatibleMessage.sendMessage(player, Main.ESSENTIAL_MESSAGE.getString("essential.prefix") + Main.ESSENTIAL_MESSAGE.getString("essential.general.offline").replace("%target%", args[0]));
				return;
			}

			if (player == target) {
				CompatibleSound.playErrorSound(player);
				return;
			}

			if (TELEPORT_REQUESTS.containsKey(target.getUniqueId())) {
				CompatibleSound.playErrorSound(player);
				CompatibleMessage.sendMessage(player, Main.ESSENTIAL_MESSAGE.getString("essential.prefix") + Main.ESSENTIAL_MESSAGE.getString("essential.teleport.already").replace("%target%", target.getName()));
				return;
			}

			TELEPORT_REQUESTS.put(target.getUniqueId(), player.getUniqueId());

			CompatibleSound.playSuccessSound(player);
			CompatibleMessage.sendMessage(player, Main.ESSENTIAL_MESSAGE.getString("essential.prefix") + Main.ESSENTIAL_MESSAGE.getString("essential.teleport.request.sent").replace("%target%", target.getName()));

			CompatibleSound.playSuccessSound(target);
			target.spigot().sendMessage(this.getAcceptText(Main.ESSENTIAL_MESSAGE.getString("essential.prefix") + Main.ESSENTIAL_MESSAGE.getString("essential.teleport.request.received").replace("%target%", player.getName())));
			return;
		}

		CompatibleMessage.sendMessage(player, Main.ESSENTIAL_MESSAGE.getString("essential.prefix") + Main.ESSENTIAL_MESSAGE.getString("essential.teleport.usage"));
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


	private BaseComponent[] getAcceptText(String text) {
		ComponentBuilder componentBuilder = new ComponentBuilder(text);

		componentBuilder.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpa accept"));
		componentBuilder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§3Click to accept the teleport request.").create()));

		return componentBuilder.create();
	}
}
