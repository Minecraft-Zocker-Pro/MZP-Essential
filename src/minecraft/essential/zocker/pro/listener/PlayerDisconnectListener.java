package minecraft.essential.zocker.pro.listener;

import minecraft.essential.zocker.pro.command.SpectateCommand;
import minecraft.essential.zocker.pro.command.teleport.TeleportRequestCommand;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerDisconnectListener implements Listener {

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerDisconnect(PlayerQuitEvent e) {
		Player player = e.getPlayer();
		TeleportRequestCommand.TELEPORT_REQUESTS.remove(player.getUniqueId());

		if (SpectateCommand.SPECTATOR_LAST_LOCATION.containsKey(player.getUniqueId())) {
			player.teleport(SpectateCommand.SPECTATOR_LAST_LOCATION.get(player.getUniqueId()));
			player.setGameMode(SpectateCommand.SPECTATOR_LAST_GAMEMODE.getOrDefault(player.getUniqueId(), GameMode.SURVIVAL));
		}
	}
}
