package minecraft.essential.zocker.pro.listener;

import minecraft.essential.zocker.pro.command.spawn.SpawnCommand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class PlayerRespawnListener implements Listener {

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerRespawn(PlayerRespawnEvent e) {
		if (SpawnCommand.getSpawnLocation() != null) {
			e.setRespawnLocation(SpawnCommand.getSpawnLocation());
		}
	}
}
