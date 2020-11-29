package minecraft.essential.zocker.pro.listener;

import minecraft.core.zocker.pro.event.ZockerDataInitializeEvent;
import minecraft.essential.zocker.pro.Main;
import minecraft.essential.zocker.pro.command.spawn.SpawnCommand;
import minecraft.essential.zocker.pro.util.Teleporter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Map;
import java.util.concurrent.ExecutionException;

public class ZockerDataInitializeListener implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onZockerDataInitialize(ZockerDataInitializeEvent e) {
		Player player = e.getZocker().getPlayer();
		if (Main.ESSENTIAL_CONFIG.getBool("essential.spawn.sync.enabled")) {
			if (Main.ESSENTIAL_CONFIG.getBool("essential.spawn.force")) {
				System.out.println("Misconfiguration detected. You cant force the spawn while sync is enabled!");
				return;
			}
			
			try {
				Thread.sleep(Main.ESSENTIAL_CONFIG.getInt("essential.spawn.sync.wait"));

				Location spawnLocation = SpawnCommand.getSpawnLocation();
				if (spawnLocation == null) return;

				Map<String, String> data = e.getZocker().get(Main.ESSENTIAL_POSITION_DATABASE_TABLE, new String[]{"position_world", "position_x", "position_y", "position_z", "position_yaw", "position_pitch"}, "player_uuid", player.getUniqueId().toString()).get();

				if (data == null || data.isEmpty()) {

					e.getZocker().insert(Main.ESSENTIAL_POSITION_DATABASE_TABLE,
						new String[]{"player_uuid", "position_world", "position_x", "position_y", "position_z", "position_yaw", "position_pitch"},
						new Object[]{player.getUniqueId().toString(), spawnLocation.getWorld().getName(), spawnLocation.getX(), spawnLocation.getY(), spawnLocation.getZ(), spawnLocation.getYaw(), spawnLocation.getPitch()});

					new Teleporter(player, spawnLocation, 0).teleport();
				} else {
					Object world = data.get("position_world");
					Object x = data.get("position_x");
					Object y = data.get("position_y");
					Object z = data.get("position_z");
					Object yaw = data.get("position_yaw");
					Object pitch = data.get("position_pitch");

					if (world != null && x != null && y != null && z != null && yaw != null && pitch != null) {
						if (x.equals("0") && y.equals("0") && z.equals("0")) {
							new Teleporter(player, spawnLocation, 0).teleport();
							return;
						}

						World serverWorld = Bukkit.getWorld(world.toString());
						if (serverWorld != null) {
							new Teleporter(player, new Location(serverWorld, Double.valueOf(x.toString()), Double.valueOf(y.toString()) + 1, Double.valueOf(z.toString()), Float.valueOf(yaw.toString()), Float.valueOf(pitch.toString())), 0)
								.teleport();
						} else {
							new Teleporter(player, spawnLocation, 0).teleport();
						}
					}
				}
			} catch (InterruptedException | ExecutionException e1) {
				e1.printStackTrace();
			}
		} else {
			if (Main.ESSENTIAL_CONFIG.getBool("essential.spawn.force")) {
				Location spawnLocation = SpawnCommand.getSpawnLocation();
				if (spawnLocation == null) return;
				new Teleporter(player, spawnLocation, 0).teleport();
				return;
			}

			if (!player.hasPlayedBefore()) {
				Location spawnLocation = SpawnCommand.getSpawnLocation();
				if (spawnLocation == null) return;
				new Teleporter(player, spawnLocation, 0).teleport();
			}
		}
	}
}
