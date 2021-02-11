package minecraft.essential.zocker.pro.util;

import minecraft.core.zocker.pro.compatibility.CompatibleMessage;
import minecraft.core.zocker.pro.compatibility.CompatibleSound;
import minecraft.essential.zocker.pro.Main;
import minecraft.essential.zocker.pro.command.spawn.SpawnCommand;
import minecraft.essential.zocker.pro.event.TeleportEndEvent;
import minecraft.essential.zocker.pro.event.TeleportStartEvent;
import org.bukkit.Bukkit;
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
	private TeleportStartEvent teleportStartEvent;

	private Teleporter teleporter;

	public Teleporter(Player player, Location location, int cooldown) {
		this(player, location, cooldown, false);
	}

	public Teleporter(Player player, Location location, int cooldown, boolean sync) {
		if (location.getWorld() == null) {
			if (SpawnCommand.getSpawnLocation() == null) {
				System.out.println("Cant teleport the player. No spawn found!");
				return;
			}

			this.location = SpawnCommand.getSpawnLocation();
		}

		this.player = player;
		this.location = location;
		this.cooldown = cooldown;
		this.sync = sync;

		this.teleporter = this;
		this.teleportStartEvent = new TeleportStartEvent(this);
	}

	public void teleport() {
		new BukkitRunnable() {
			@Override
			public void run() {
				Bukkit.getPluginManager().callEvent(teleportStartEvent);

				if (teleportStartEvent.isCancelled()) return;

				if (PLAYER_TELEPORTING.contains(player.getUniqueId())) return;
				PLAYER_TELEPORTING.add(player.getUniqueId());

				if (cooldown == 0) {
					if (!sync) {
						player.teleport(location);
						CompatibleSound.playTeleportSound(player);
					} else {
						teleportSync();
					}
				} else {
					new BukkitRunnable() {
						int cd = cooldown;
						final Location playerLocation = player.getLocation();

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
								callTeleportEndEvent();
								this.cancel();
							}
						}
					}.runTaskTimerAsynchronously(Main.getPlugin(), 20L, 20L);
				}
			}
		}.runTask(Main.getPlugin());
	}

	private void teleportSync() {
		Bukkit.getPluginManager().callEvent(new TeleportEndEvent(teleporter, true));

		new BukkitRunnable() {
			@Override
			public void run() {
				player.teleport(location);

				if (teleporterListener != null) {
					teleporterListener.onTeleported();
				}

				PLAYER_TELEPORTING.remove(player.getUniqueId());
			}
		}.runTask(Main.getPlugin());

		CompatibleSound.playTeleportSound(player);
	}

	private void callTeleportEndEvent() {
		Bukkit.getPluginManager().callEvent(new TeleportEndEvent(teleporter, true));
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

	public void setCooldown(int cooldown) {
		this.cooldown = cooldown;
	}

	public void setTeleporterListener(TeleporterListener teleporterListener) {
		this.teleporterListener = teleporterListener;
	}

	public interface TeleporterListener {

		void onCanceled();

		void onTeleported();

	}
}
