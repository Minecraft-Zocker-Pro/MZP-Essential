package minecraft.essential.zocker.pro.command;

import minecraft.core.zocker.pro.command.Command;
import minecraft.core.zocker.pro.compatibility.CompatibleSound;
import minecraft.essential.zocker.pro.Main;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class NightCommand extends Command {

	public NightCommand() {
		super("night", "mzp.essential.night", new String[]{"night"});
	}

	@Override
	public List<String> onTabComplete(CommandSender commandSender, String[] strings) {
		return null;
	}

	@Override
	public void onExecute(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) return;
		Player player = (Player) sender;

		new BukkitRunnable() {
			@Override
			public void run() {
				player.getWorld().setTime(13000);
				CompatibleSound.playChangedSound(player);
			}
		}.runTask(Main.getPlugin());
	}
}
