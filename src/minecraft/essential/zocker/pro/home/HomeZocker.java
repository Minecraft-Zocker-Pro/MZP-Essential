package minecraft.essential.zocker.pro.home;

import minecraft.core.zocker.pro.Zocker;
import minecraft.core.zocker.pro.util.Util;
import minecraft.essential.zocker.pro.Main;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class HomeZocker extends Zocker {

	public HomeZocker(Player player) {
		super(player);
	}

	public HomeZocker(UUID uuid) {
		super(uuid);
	}

	public HomeZocker(String dummy) {
		super(dummy);
	}

	public Home getHome(String id) {
		for (Home home : this.getHomes()) {
			if (home == null) continue;
			if (home.getId().equalsIgnoreCase(id)) return home;
		}

		return null;
	}

	public List<Home> getHomes() {
		try {
			List<Home> homes = new ArrayList<>();
			List<String> homeUUIDs = this.getList(
				Main.ESSENTIAL_HOME_DATABASE_TABLE,
				new String[]{"player_uuid", "home_uuid"},
				"player_uuid",
				this.getUUIDString()
			).get();


			for (String homeUUID : homeUUIDs) {
				Map<String, String> data = this.get(
					Main.ESSENTIAL_HOME_DATABASE_TABLE,
					new String[]{"location_world", "location_x", "location_y", "location_z", "location_yaw", "location_pitch", "name", "public", "material"},
					"home_uuid", homeUUID).get();
				if (data == null) continue;

				World world = Bukkit.getWorld(data.get("location_world"));

				homes.add(new Home(
					homeUUID,
					data.get("name"),
					Util.convertStringToBoolean(data.get("public")),
					Material.valueOf(data.get("material")),
					new Location(
						world,
						Double.valueOf(data.get("location_x")),
						Double.valueOf(data.get("location_y")),
						Double.valueOf(data.get("location_z")),
						Float.valueOf(data.get("location_yaw")),
						Float.valueOf(data.get("location_pitch"))
					)));
			}

			return homes;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public CompletableFuture<Boolean> addHome(Home home) {
		for (Home home1 : this.getHomes()) {
			if (home1.getName().equalsIgnoreCase(home.getName())) return null;
		}

		return this.insert(
			Main.ESSENTIAL_HOME_DATABASE_TABLE,
			new String[]{"home_uuid", "player_uuid", "name", "public", "material", "location_world", "location_x", "location_y", "location_z", "location_yaw", "location_pitch"},
			new Object[]{home.getId(), this.getUUIDString(), home.getName(), home.isPublic() ? 1 : 0, home.getMaterial().name(), home.getLocation().getWorld().getName(), home.getLocation().getX(), home.getLocation().getY(), home.getLocation().getZ(), home.getLocation().getYaw(),
				home.getLocation().getPitch()},
			new String[]{"player_uuid", "home_uuid"},
			new Object[]{this.getUUIDString(), home.getId()}
		);
	}

	public CompletableFuture<Boolean> updateHome(Home home) {
		if (home == null) return null;

		return this.set(Main.ESSENTIAL_HOME_DATABASE_TABLE,
			new String[]{"home_uuid", "player_uuid", "name", "public", "material", "location_world", "location_x", "location_y", "location_z", "location_yaw", "location_pitch"},
			new Object[]{home.getId(), this.getUUIDString(), home.getName(), home.isPublic() ? 1 : 0, home.getMaterial().name(), home.getLocation().getWorld().getName(), home.getLocation().getX(), home.getLocation().getY(), home.getLocation().getZ(), home.getLocation().getYaw(),
				home.getLocation().getPitch()},
			"home_uuid",
			home.getId()
		);

	}

	public CompletableFuture<Boolean> removeHome(Home home) {
		return this.delete(Main.ESSENTIAL_HOME_DATABASE_TABLE, new String[]{"player_uuid", "home_uuid"}, new Object[]{this.getUUIDString(), home.getId()});
	}

	public String getHomeUUID(String name) {
		for (Home home : this.getHomes()) {
			if (home == null) continue;
			String homeName = ChatColor.stripColor(home.getName());
			if (homeName.equalsIgnoreCase(name)) return home.getId();
		}

		return null;
	}
}
