package minecraft.essentials.zocker.pro.command.warp;

import minecraft.core.zocker.pro.command.SubCommand;
import minecraft.core.zocker.pro.compatibility.CompatibleMessage;
import minecraft.core.zocker.pro.compatibility.CompatibleSound;
import minecraft.essentials.zocker.pro.Main;
import minecraft.essentials.zocker.pro.warp.Warp;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class WarpDeleteCommand extends SubCommand {

	public WarpDeleteCommand() {
		super("delete");
	}

	@Override
	public String getUsage() {
		return Main.ESSENTIAL_MESSAGE.getString("essential.prefix") + "§3Type §6/warp delete <name>";
	}

	@Override
	public String getPermission() {
		return "mzp.essential.warp.delete";
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

			warp.delete();
			CompatibleMessage.sendMessage(player, Main.ESSENTIAL_MESSAGE.getString("essential.prefix") + Main.ESSENTIAL_MESSAGE.getString("essential.warp.deleted"));
			CompatibleSound.playChangedSound(player);
		}
	}

	@Override
	public List<String> getCompletions(CommandSender sender, String[] args) {
		return null;
	}
}
