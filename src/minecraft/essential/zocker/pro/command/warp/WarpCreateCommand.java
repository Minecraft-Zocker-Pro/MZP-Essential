package minecraft.essential.zocker.pro.command.warp;

import minecraft.core.zocker.pro.command.SubCommand;
import minecraft.core.zocker.pro.compatibility.CompatibleMaterial;
import minecraft.core.zocker.pro.compatibility.CompatibleMessage;
import minecraft.core.zocker.pro.compatibility.CompatibleSound;
import minecraft.essential.zocker.pro.Main;
import minecraft.essential.zocker.pro.warp.Warp;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class WarpCreateCommand extends SubCommand {

	public WarpCreateCommand() {
		super("create");
	}

	@Override
	public String getUsage() {
		return Main.ESSENTIAL_MESSAGE.getString("essential.prefix") + "§3Type §6/warp create <name>";
	}

	@Override
	public String getPermission() {
		return "mzp.essential.warp.create";
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
			if (warp != null) {
				CompatibleMessage.sendMessage(player, Main.ESSENTIAL_MESSAGE.getString("essential.prefix") + Main.ESSENTIAL_MESSAGE.getString("essential.warp.exist").replace("%warp%", args[0]));
				CompatibleSound.playErrorSound(player);
				return;
			}

			int slot = 10;
			for (Warp warp1 : Warp.getWarps()) {
				if (warp1.getSlot() == slot) {
					if (slot == 16) {
						slot += 3;
						continue;
					}

					if (slot == 25) {
						slot += 3;
						continue;
					}

					if (slot == 34) {
						slot += 3;
						continue;
					}

					slot++;
				}
			}

			Warp newWarp = new Warp(args[0], "§6§l" + args[0], null, true, 0, "mzp.essential.warp", slot, 0, CompatibleMaterial.OAK_SIGN.getMaterial(), player.getLocation(), null, null);
			newWarp.create();

			CompatibleMessage.sendMessage(player, Main.ESSENTIAL_MESSAGE.getString("essential.prefix") + Main.ESSENTIAL_MESSAGE.getString("essential.warp.created"));
			CompatibleSound.playChangedSound(player);
		}
	}

	@Override
	public List<String> getCompletions(CommandSender sender, String[] args) {
		return null;
	}
}
