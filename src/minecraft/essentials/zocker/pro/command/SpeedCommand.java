package minecraft.essentials.zocker.pro.command;

import minecraft.core.zocker.pro.command.Command;
import minecraft.core.zocker.pro.compatibility.CompatibleMessage;
import minecraft.core.zocker.pro.compatibility.CompatibleSound;
import minecraft.essentials.zocker.pro.Main;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class SpeedCommand extends Command {

	public SpeedCommand() {
		super("speed", "mzp.essential.speed", new ArrayList<>());
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String[] args) {
		return null;
	}

	@Override
	public void onExecute(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) return;
		Player player = (Player) sender;

		if (args.length == 1) {
			if (NumberUtils.isDigits(args[0])) {
				float speed = Float.valueOf(args[0]) / 10.0f;
				if (speed <= Main.ESSENTIAL_CONFIG.getDouble("essential.speed.max")) {
					player.setFlySpeed(speed);
					CompatibleMessage.sendMessage(player, Main.ESSENTIAL_MESSAGE.getString("essential.prefix") + Main.ESSENTIAL_MESSAGE.getString("essential.speed.changed").replace("%speed%", String.valueOf(speed * 10.0F)));
					CompatibleSound.playSuccessSound(player);
					return;
				}

				CompatibleMessage.sendMessage(player, Main.ESSENTIAL_MESSAGE.getString("essential.prefix") + Main.ESSENTIAL_MESSAGE.getString("essential.speed.max").replace("%max%", String.valueOf(Main.ESSENTIAL_CONFIG.getDouble("essential.speed.max") * 10.0f)));
				CompatibleSound.playErrorSound(player);
				return;
			}
		}

		CompatibleSound.playErrorSound(player);
		CompatibleMessage.sendMessage(player, Main.ESSENTIAL_MESSAGE.getString("essential.prefix") + Main.ESSENTIAL_MESSAGE.getString("essential.speed.usage"));
	}
}
