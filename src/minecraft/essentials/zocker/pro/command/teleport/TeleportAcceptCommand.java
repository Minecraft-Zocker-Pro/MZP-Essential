package minecraft.essentials.zocker.pro.command.teleport;

import minecraft.core.zocker.pro.command.SubCommand;
import minecraft.core.zocker.pro.compatibility.CompatibleMessage;
import minecraft.core.zocker.pro.compatibility.CompatibleSound;
import minecraft.essentials.zocker.pro.Main;
import minecraft.essentials.zocker.pro.util.Teleporter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class TeleportAcceptCommand extends SubCommand {

	public TeleportAcceptCommand() {
		super("accept");
	}

	@Override
	public String getUsage() {
		return null;
	}

	@Override
	public String getPermission() {
		return "mzp.essential.teleport.request.accept";
	}

	@Override
	public void onExecute(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) return;
		if (args.length == 0) {
			Player player = (Player) sender;

			for (UUID uuidTarget : TeleportRequestCommand.TELEPORT_REQUESTS.keySet()) {
				if (player.getUniqueId() == uuidTarget) {
					UUID uuidPlayer = TeleportRequestCommand.TELEPORT_REQUESTS.get(uuidTarget);
					Player requester = Bukkit.getPlayer(uuidPlayer);

					if (requester == null || !requester.isOnline()) {
						CompatibleSound.playErrorSound(player);
						CompatibleMessage.sendMessage(player, Main.ESSENTIAL_MESSAGE.getString("essential.prefix") + Main.ESSENTIAL_MESSAGE.getString("essential.general.offline").replace("%target%", "Player"));
						return;
					}

					CompatibleSound.playSuccessSound(player);
					CompatibleMessage.sendMessage(player, Main.ESSENTIAL_MESSAGE.getString("essential.prefix") + Main.ESSENTIAL_MESSAGE.getString("essential.teleport.accepted.self"));
					CompatibleMessage.sendMessage(requester, Main.ESSENTIAL_MESSAGE.getString("essential.prefix") + Main.ESSENTIAL_MESSAGE.getString("essential.teleport.accepted.other").replace("%target%", player.getName()));
					TeleportRequestCommand.TELEPORT_REQUESTS.remove(uuidTarget);

					Teleporter teleporter = new Teleporter(requester, player.getLocation(), Main.ESSENTIAL_CONFIG.getInt("essential.teleport.cooldown"), true);
					teleporter.teleport();
					return;
				}
			}

			CompatibleSound.playErrorSound(player);
			CompatibleMessage.sendMessage(player, Main.ESSENTIAL_MESSAGE.getString("essential.prefix") + Main.ESSENTIAL_MESSAGE.getString("essential.teleport.empty"));
		}
	}

	@Override
	public List<String> getCompletions(CommandSender sender, String[] args) {
		return null;
	}
}
