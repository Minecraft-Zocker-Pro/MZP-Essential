package minecraft.essential.zocker.pro.command.home;

import minecraft.core.zocker.pro.command.SubCommand;
import minecraft.core.zocker.pro.compatibility.CompatibleMaterial;
import minecraft.core.zocker.pro.compatibility.CompatibleMessage;
import minecraft.core.zocker.pro.compatibility.CompatibleSound;
import minecraft.essential.zocker.pro.Main;
import minecraft.essential.zocker.pro.home.Home;
import minecraft.essential.zocker.pro.home.HomeZocker;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Material;

import java.util.List;
import java.util.UUID;

public class HomeCreateCommand extends SubCommand {

	public HomeCreateCommand() {
		super("create", 1, 1);
	}

	@Override
	public String getUsage() {
		return null;
	}

	@Override
	public String getPermission() {
		return "mzp.essential.home.create";
	}

	@Override
	public void onExecute(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) return;

		String name = args[0];
		if (name == null) return;

		Player player = (Player) sender;
		HomeZocker homeZocker = new HomeZocker(player.getUniqueId());

		if (homeZocker.getHomes().size() >= Main.ESSENTIAL_CONFIG.getInt("essential.home.limit")) {
			CompatibleMessage.sendMessage(player, Main.ESSENTIAL_MESSAGE.getString("essential.prefix") + Main.ESSENTIAL_MESSAGE.getString("essential.home.max"));
			CompatibleSound.playErrorSound(player);
			return;
		}

		String homeUUID = homeZocker.getHomeUUID(name);

		if (homeUUID != null) {
			CompatibleMessage.sendMessage(player, Main.ESSENTIAL_MESSAGE.getString("essential.prefix") + Main.ESSENTIAL_MESSAGE.getString("essential.home.exist").replace("%home%", name));
			CompatibleSound.playErrorSound(player);
			return;
		}

		// Check world
		List<String> worldBlacklist = Main.ESSENTIAL_CONFIG.getStringList("essential.home.world.blacklist");
		if (worldBlacklist != null && !worldBlacklist.isEmpty()) {
			for (String worldName : worldBlacklist) {
				if (player.getLocation().getWorld().getName().equalsIgnoreCase(worldName)) {
					CompatibleMessage.sendMessage(player, Main.ESSENTIAL_MESSAGE.getString("essential.prefix") + Main.ESSENTIAL_MESSAGE.getString("essential.home.blacklist"));
					CompatibleSound.playErrorSound(player);
					return;
				}
			}
		}

		Material material = player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType();
		if (material == CompatibleMaterial.AIR.getMaterial()) {
			material = CompatibleMaterial.ENDER_PEARL.getMaterial();
		}

		homeZocker.addHome(new Home(UUID.randomUUID().toString(), "§6" + name, false, material, player.getLocation()));
		CompatibleMessage.sendMessage(player, Main.ESSENTIAL_MESSAGE.getString("essential.prefix") + Main.ESSENTIAL_MESSAGE.getString("essential.home.created"));
		CompatibleSound.playChangedSound(player);
	}

	@Override
	public List<String> getCompletions(CommandSender sender, String[] strings) {
		return null;
	}
}
