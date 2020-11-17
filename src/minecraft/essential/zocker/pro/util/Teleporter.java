package minecraft.essential.zocker.pro.util;

import minecraft.core.zocker.pro.compatibility.CompatibleMessage;
import minecraft.core.zocker.pro.compatibility.CompatibleSound;
import minecraft.essential.zocker.pro.Main;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.UUID;


public class Teleporter {

	private static final ArrayList<UUID> PLAYER_TELEPORTING = new ArrayList<>();

	private Player player;
	private Location location;
	private int cooldown;
	private boolean sync = true;
	private TeleporterListener teleporterListener = null;

	public Teleporter(Player player, Location location, int cooldown) {
		this.player = player;
		this.location = location;
		this.cooldown = cooldown;
	}

	public Teleporter(Player player, Location location, int cooldown, boolean sync) {
		this.player = player;
		this.location = location;
		this.cooldown = cooldown;
		this.sync = sync;
	}

	public void teleport() {
		if (PLAYER_TELEPORTING.contains(this.player.getUniqueId())) return;
		PLAYER_TELEPORTING.add(this.player.getUniqueId());

		if (cooldown == 0) {
			if (!sync) {
				this.player.teleport(this.location);
				CompatibleSound.playTeleportSound(this.player);
			} else {
				teleportSync();
			}
		} else {
			new BukkitRunnable() {
				int cd = cooldown;
				Location playerLocation = player.getLocation();

				@Override
				public void run() {
					if (player.getLocation().getX() == playerLocation.getX() && player.getLocation().getY() == playerLocation.getY()) {
						CompatibleMessage.sendActionBar(player, Main.ESSENTIAL_MESSAGE.getString("essential.teleport.teleporting").replace("%cooldown%", String.valueOf(cd)));
						player.playSound(player.getLocation(), CompatibleSound.BLOCK_NOTE_BLOCK_PLING.getSound(), 1.5F, 1.5F);

						if (cd != 0) {
							cd--;
						} else {
							teleportSync();
							this.cancel();
						}
					} else {
						if (teleporterListener != null) {
							teleporterListener.onCanceled();
						}

						CompatibleMessage.sendMessage(player, Main.ESSENTIAL_MESSAGE.getString("essential.prefix") + Main.ESSENTIAL_MESSAGE.getString("essential.teleport.canceled"));
						PLAYER_TELEPORTING.remove(player.getUniqueId());
						this.cancel();
					}
				}
			}.runTaskTimerAsynchronously(Main.getPlugin(), 20L, 20L);
		}
	}

	private void teleportSync() {
		new BukkitRunnable() {
			@Override
			public void run() {
				player.teleport(location);
				if (teleporterListener != null) {
					teleporterListener.onTeleported();
				}

				PLAYER_TELEPORTING.remove(player.getUniqueId());
				CompatibleSound.playTeleportSound(player);
			}
		}.runTask(Main.getPlugin());
	}

	public Player getPlayer() {
		return player;
	}

	public Location getLocation() {
		return location;
	}

	public int getCooldown() {
		return cooldown;
	}

	public boolean isSync() {
		return sync;
	}

	public void setTeleporterListener(TeleporterListener teleporterListener) {
		this.teleporterListener = teleporterListener;
	}

	public interface TeleporterListener {

		void onCanceled();

		void onTeleported();

	}
}
