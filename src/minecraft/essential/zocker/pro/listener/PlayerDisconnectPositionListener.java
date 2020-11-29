package minecraft.essential.zocker.pro.listener;

import minecraft.core.zocker.pro.Zocker;
import minecraft.essential.zocker.pro.Main;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerDisconnectPositionListener implements Listener {

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onPlayerDisconnectLocation(PlayerQuitEvent e) {
		try {
			Zocker zocker = Zocker.getZocker(e.getPlayer().getUniqueId());
			if (zocker == null) return;

			Location location = e.getPlayer().getLocation();
			String[] values = {location.getWorld().getName(), String.valueOf(location.getX()), String.valueOf(location.getY()), String.valueOf(location.getZ()), String.valueOf(location.getYaw()), String.valueOf(location.getPitch())};
			
			zocker.set(Main.ESSENTIAL_POSITION_DATABASE_TABLE, new String[]{"position_world", "position_x", "position_y", "position_z", "position_yaw", "position_pitch"}, values, "player_uuid", e.getPlayer().getUniqueId().toString());
		} catch (Exception e2) {
			e2.printStackTrace();
		}
	}
}
