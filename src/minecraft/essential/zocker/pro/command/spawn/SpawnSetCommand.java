package minecraft.essential.zocker.pro.command.spawn;

import minecraft.core.zocker.pro.command.SubCommand;
import minecraft.core.zocker.pro.compatibility.CompatibleMessage;
import minecraft.core.zocker.pro.compatibility.CompatibleSound;
import minecraft.core.zocker.pro.config.Config;
import minecraft.essential.zocker.pro.Main;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class SpawnSetCommand extends SubCommand {

	public SpawnSetCommand() {
		super("set");
	}

	@Override
	public String getUsage() {
		return Main.ESSENTIAL_MESSAGE.getString("essential.prefix") + "§3Type §6/spawn set";
	}

	@Override
	public String getPermission() {
		return "mzp.essential.spawn.set";
	}

	@Override
	public void onExecute(CommandSender sender, String[] args) {
		if (args.length == 0) {
			Player player = (Player) sender;
			Config config = Main.ESSENTIAL_CONFIG;

			config.set("essential.spawn.enabled", true);
			config.set("essential.spawn.location.world", player.getWorld().getName());
			config.set("essential.spawn.location.x", player.getLocation().getX());
			config.set("essential.spawn.location.y", player.getLocation().getY());
			config.set("essential.spawn.location.z", player.getLocation().getZ());
			config.set("essential.spawn.location.yaw", player.getLocation().getYaw());
			config.set("essential.spawn.location.pitch", player.getLocation().getPitch());

			config.save();

			SpawnCommand.loadSpawnLocation();
			CompatibleMessage.sendMessage(player, Main.ESSENTIAL_MESSAGE.getString("essential.prefix") + Main.ESSENTIAL_MESSAGE.getString("essential.warp.set"));
			CompatibleSound.playSuccessSound(player);
		}
	}

	@Override
	public List<String> getCompletions(CommandSender sender, String[] args) {
		return null;
	}
}
