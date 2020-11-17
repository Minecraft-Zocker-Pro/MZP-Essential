package minecraft.essential.zocker.pro.command.warp;

import minecraft.core.zocker.pro.Zocker;
import minecraft.core.zocker.pro.command.SubCommand;
import minecraft.core.zocker.pro.compatibility.CompatibleMessage;
import minecraft.core.zocker.pro.compatibility.CompatibleSound;
import minecraft.essential.zocker.pro.Main;
import minecraft.essential.zocker.pro.warp.Warp;
import minecraft.essential.zocker.pro.warp.WarpEditInventory;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class WarpEditCommand extends SubCommand {

	public WarpEditCommand() {
		super("edit", 1, 1);
	}

	@Override
	public String getUsage() {
		return Main.ESSENTIAL_MESSAGE.getString("essential.prefix") + "§3Type §6/warp edit <name>";
	}

	@Override
	public String getPermission() {
		return "mzp.essential.warp.edit";
	}

	@Override
	public void onExecute(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) return;
		Player player = (Player) sender;


		if (args.length == 0) {
			player.sendMessage(getUsage());
			CompatibleSound.playErrorSound(player);
			return;
		}

		if (args.length == 1) {
			Warp warp = Warp.getWarp(args[0]);
			if (warp == null) {
				CompatibleMessage.sendMessage(player, Main.ESSENTIAL_MESSAGE.getString("essential.prefix") + Main.ESSENTIAL_MESSAGE.getString("essential.warp.notfound").replace("%warp%", args[0]));
				CompatibleSound.playErrorSound(player);
				return;
			}

			Zocker zocker = Zocker.getZocker(player.getUniqueId());
			if (zocker == null) {
				CompatibleSound.playErrorSound(player);
				return;
			}

			new WarpEditInventory(zocker, warp).open(zocker);
		}
	}

	@Override
	public List<String> getCompletions(CommandSender commandSender, String[] strings) {
		return null;
	}
}
