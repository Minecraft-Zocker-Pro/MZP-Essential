package minecraft.essential.zocker.pro.command.teleport;

import minecraft.core.zocker.pro.command.SubCommand;
import minecraft.core.zocker.pro.compatibility.CompatibleMessage;
import minecraft.core.zocker.pro.compatibility.CompatibleSound;
import minecraft.essential.zocker.pro.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class TeleportDenyCommand extends SubCommand {

	public TeleportDenyCommand() {
		super("deny");
	}

	@Override
	public String getUsage() {
		return null;
	}

	@Override
	public String getPermission() {
		return "mzp.essential.teleport.request.deny";
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
					CompatibleMessage.sendMessage(player, Main.ESSENTIAL_MESSAGE.getString("essential.prefix") + Main.ESSENTIAL_MESSAGE.getString("essential.teleport.denied.self"));
					CompatibleMessage.sendMessage(requester, Main.ESSENTIAL_MESSAGE.getString("essential.prefix") + Main.ESSENTIAL_MESSAGE.getString("essential.teleport.denied.other").replace("%target%", player.getName()));
					TeleportRequestCommand.TELEPORT_REQUESTS.remove(uuidTarget);
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
