package minecraft.essentials.zocker.pro.command.home;

import minecraft.core.zocker.pro.command.SubCommand;
import minecraft.core.zocker.pro.compatibility.CompatibleMessage;
import minecraft.core.zocker.pro.compatibility.CompatibleSound;
import minecraft.essentials.zocker.pro.Main;
import minecraft.essentials.zocker.pro.home.Home;
import minecraft.essentials.zocker.pro.home.HomeZocker;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class HomeDeleteCommand extends SubCommand {

	public HomeDeleteCommand() {
		super("delete");
	}

	@Override
	public String getUsage() {
		return null;
	}

	@Override
	public String getPermission() {
		return "mzp.essential.home.delete";
	}

	@Override
	public void onExecute(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) return;

		String name = args[0];
		if (name == null) return;

		Player player = (Player) sender;
		HomeZocker homeZocker = new HomeZocker(player.getUniqueId());
		String homeUUID = homeZocker.getHomeUUID(name);

		if (homeUUID == null) {
			CompatibleMessage.sendMessage(player, Main.ESSENTIAL_MESSAGE.getString("essential.prefix") + Main.ESSENTIAL_MESSAGE.getString("essential.home.notfound").replace("%home%", name));
			CompatibleSound.playErrorSound(player);
			return;
		}

		Home home = homeZocker.getHome(homeUUID);
		if (home == null) {
			CompatibleMessage.sendMessage(player, Main.ESSENTIAL_MESSAGE.getString("essential.prefix") + Main.ESSENTIAL_MESSAGE.getString("essential.home.notfound").replace("%home%", name));
			CompatibleSound.playErrorSound(player);
			return;
		}

		homeZocker.removeHome(home);
		CompatibleMessage.sendMessage(player, Main.ESSENTIAL_MESSAGE.getString("essential.prefix") + Main.ESSENTIAL_MESSAGE.getString("essential.home.deleted").replace("%home%", name));
		CompatibleSound.playChangedSound(player);
	}

	@Override
	public List<String> getCompletions(CommandSender commandSender, String[] strings) {
		return null;
	}
}
