package minecraft.essential.zocker.pro.command;

import minecraft.core.zocker.pro.OfflineZocker;
import minecraft.core.zocker.pro.command.Command;
import minecraft.core.zocker.pro.compatibility.CompatibleSound;
import minecraft.core.zocker.pro.inventory.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class SkullCommand extends Command {

	public SkullCommand() {
		super("skull", "mzp.essential.skull", new String[]{"skull"});
	}

	@Override
	public void onExecute(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) return;
		Player player = (Player) sender;

		if (args.length == 0) {
			ItemBuilder itemBuilder = new ItemBuilder(Material.PLAYER_HEAD)
				.setName("§6§l" + player.getName() + " §3Head")
				.setOwningPlayer(player);

			player.getInventory().addItem(itemBuilder.toItemStack());
			CompatibleSound.playSuccessSound(player);
			return;
		}

		String target = args[0];
		if (target == null) {
			CompatibleSound.playErrorSound(player);
			return;
		}

		// TODO Support all offline names, not only the own database
		UUID offlineZockerUUID = OfflineZocker.fetchUUID(target);
		if (offlineZockerUUID == null) {
			CompatibleSound.playErrorSound(player);
			return;
		}

		OfflineZocker offlineZocker = new OfflineZocker(offlineZockerUUID);

		ItemBuilder itemBuilder = new ItemBuilder(Material.PLAYER_HEAD)
			.setName("§6§l" + target + " §3Head")
			.setOwningPlayer(offlineZocker.getPlayer()); // TODO npe?

		player.getInventory().addItem(itemBuilder.toItemStack());
		CompatibleSound.playSuccessSound(player);
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String[] args) {
		List<String> completions = new ArrayList<>();

		if (args.length == 1) {
			String partialPlayerName = args[0];

			int lastSpaceIndex = partialPlayerName.lastIndexOf(' ');
			if (lastSpaceIndex >= 0) {
				partialPlayerName = partialPlayerName.substring(lastSpaceIndex + 1);
			}

			for (Player p : Bukkit.getOnlinePlayers()) {
				if (p.getName().toLowerCase().startsWith(partialPlayerName)) {
					return Collections.singletonList(p.getName());
				}
			}
		}

		return completions;
	}
}
