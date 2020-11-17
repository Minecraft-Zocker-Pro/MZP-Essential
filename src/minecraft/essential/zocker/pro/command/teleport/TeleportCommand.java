package minecraft.essential.zocker.pro.command.teleport;

import minecraft.core.zocker.pro.Main;
import minecraft.core.zocker.pro.command.Command;
import minecraft.core.zocker.pro.compatibility.CompatibleMessage;
import minecraft.core.zocker.pro.compatibility.CompatibleSound;
import minecraft.core.zocker.pro.config.Config;
import minecraft.core.zocker.pro.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class TeleportCommand extends Command {

	public TeleportCommand() {
		super("teleport", "mzp.essential.teleport", new String[]{"tp", "tele"});
	}

	@Override
	public void onExecute(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) return;
		Player player = (Player) sender;

		// Teleport to player
		if (args.length == 1) {
			if (player.getGameMode() == GameMode.SPECTATOR) {
				Player target = Bukkit.getPlayer(args[0]);
				if (target == null || !target.isOnline()) {
					CompatibleSound.playErrorSound(player);
					CompatibleMessage.sendMessage(player, Main.CORE_MESSAGE.getString("essential.prefix") + Main.CORE_MESSAGE.getString("essential.general.offline").replace("%target%", args[0]));
					return;
				}

				if (player == target) {
					CompatibleSound.playErrorSound(player);
					return;
				}

				Bukkit.getScheduler().runTask(Main.getPlugin(), () -> {
					player.teleport(target.getLocation());
					CompatibleSound.playTeleportSound(player);
				});

				CompatibleMessage.sendMessage(player, Main.CORE_MESSAGE.getString("essential.prefix") + Main.CORE_MESSAGE.getString("essential.teleport.success").replace("%target%", args[0]));
				return;
			}

			if (!player.hasPermission("essentials.teleport.self")) {
				CompatibleSound.playErrorSound(player);
				return;
			}

			Player target = Bukkit.getPlayer(args[0]);
			if (target == null || !target.isOnline()) {
				CompatibleSound.playErrorSound(player);
				CompatibleMessage.sendMessage(player, Main.CORE_MESSAGE.getString("essential.prefix") + Main.CORE_MESSAGE.getString("essential.general.offline").replace("%target%", args[0]));
				return;
			}

			if (player == target) {
				CompatibleSound.playErrorSound(player);
				return;
			}

			Bukkit.getScheduler().runTask(Main.getPlugin(), () -> {
				player.teleport(target.getLocation());
				CompatibleSound.playTeleportSound(player);
			});

			CompatibleMessage.sendMessage(player, Main.CORE_MESSAGE.getString("essential.prefix") + Main.CORE_MESSAGE.getString("essential.teleport.success").replace("%target%", args[0]));
			return;
		}

		if (!player.hasPermission("mzp.essential.teleport.other")) {
			CompatibleSound.playErrorSound(player);
			return;
		}

		// Teleport target to player
		if (args.length == 2) {
			Player target = Bukkit.getPlayer(args[0]);
			if (target == null || target == player || !target.isOnline()) {
				CompatibleSound.playErrorSound(player);
				return;
			}

			Bukkit.getScheduler().runTask(Main.getPlugin(), () -> {
				target.teleport(player.getLocation());
				CompatibleSound.playTeleportSound(target);
			});

			CompatibleMessage.sendMessage(target, Main.CORE_MESSAGE.getString("essential.prefix") + Main.CORE_MESSAGE.getString("essential.teleport.success").replace("%target%", player.getName()));
			return;
		}

		// Teleport to coordinates
		if (args.length == 3) {
			if (!player.hasPermission("mzp.essential.teleport.coordinate")) {
				CompatibleSound.playErrorSound(player);
				return;
			}

			Bukkit.getScheduler().runTask(Main.getPlugin(), () -> {
				player.teleport(
					new Location(
						player.getWorld(),
						Double.valueOf(args[0]),
						Double.valueOf(args[1]),
						Double.valueOf(args[2])));
				CompatibleSound.playTeleportSound(player);
			});
		}
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String[] args) {
		return null;
	}
}
