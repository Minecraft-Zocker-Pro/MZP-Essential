package minecraft.essentials.zocker.pro.command;

import minecraft.core.zocker.pro.command.Command;
import minecraft.core.zocker.pro.compatibility.CompatibleMessage;
import minecraft.core.zocker.pro.compatibility.CompatibleSound;
import minecraft.essentials.zocker.pro.Main;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class SpectateCommand extends Command {

	public static final HashMap<UUID, Location> SPECTATOR_LAST_LOCATION = new HashMap<>();
	public static final HashMap<UUID, GameMode> SPECTATOR_LAST_GAMEMODE = new HashMap<>();


	public SpectateCommand() {
		super("spectate", "mzp.essential.spectate", new ArrayList<>());
	}


	@Override
	public List<String> onTabComplete(CommandSender sender, String[] args) {
		return null;
	}

	@Override
	public void onExecute(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) return;
		Player player = (Player) sender;

		if (player.getGameMode() != GameMode.SPECTATOR) {
			SPECTATOR_LAST_LOCATION.put(player.getUniqueId(), player.getLocation());
			SPECTATOR_LAST_GAMEMODE.put(player.getUniqueId(), player.getGameMode());

			new BukkitRunnable() {
				@Override
				public void run() {
					player.setGameMode(GameMode.SPECTATOR);
				}
			}.runTask(Main.getPlugin());


			CompatibleSound.playSuccessSound(player);
			CompatibleMessage.sendMessage(player, Main.ESSENTIAL_MESSAGE.getString("essential.prefix") + Main.ESSENTIAL_MESSAGE.getString("essential.spectate.enabled"));
			return;
		}

		if (player.getGameMode() == GameMode.SPECTATOR) {
			new BukkitRunnable() {
				@Override
				public void run() {
					player.setGameMode(SPECTATOR_LAST_GAMEMODE.getOrDefault(player.getUniqueId(), GameMode.SURVIVAL));
					CompatibleMessage.sendMessage(player, Main.ESSENTIAL_MESSAGE.getString("essential.prefix") + Main.ESSENTIAL_MESSAGE.getString("essential.spectate.disabled"));

					if (SPECTATOR_LAST_LOCATION.get(player.getUniqueId()) != null) {
						player.teleport(SPECTATOR_LAST_LOCATION.get(player.getUniqueId()));
						CompatibleSound.playTeleportSound(player);
					}

					SPECTATOR_LAST_LOCATION.remove(player.getUniqueId());
					SPECTATOR_LAST_GAMEMODE.remove(player.getUniqueId());
				}
			}.runTask(Main.getPlugin());

			return;
		}

		CompatibleSound.playErrorSound(player);
	}
}
