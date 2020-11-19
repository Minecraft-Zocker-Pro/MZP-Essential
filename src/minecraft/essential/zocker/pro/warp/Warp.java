package minecraft.essential.zocker.pro.warp;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import minecraft.core.zocker.pro.compatibility.CompatibleMaterial;
import minecraft.core.zocker.pro.storage.StorageManager;
import minecraft.essential.zocker.pro.Main;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

public class Warp {

	private static final List<Warp> WARPS = new ArrayList<>();

	private String name;
	private String displayName;
	private List<String> lore;
	private boolean enabled;
	private double price;
	private String permission;
	private int slot;
	private int cooldown;
	private Material material;
	private Location location;

	private List<String> commands;
	private List<String> title;

	public Warp(String name, String displayName, List<String> lore, boolean enabled, double price, String permission, int slot, int cooldown, Material material, Location location, List<String> commands, List<String> title) {
		this.name = name;
		this.displayName = displayName;
		this.lore = lore;
		this.enabled = enabled;
		this.price = price;
		this.permission = permission;
		this.slot = slot;
		this.cooldown = cooldown;
		this.material = material;
		this.location = location;
		this.commands = commands;
		this.title = title;

		WARPS.add(this);
	}

	public boolean create() {
		boolean success;

		String lores = "";
		if (this.lore != null) {
			lores = StringUtils.join(this.lore, "\n");
		}

		String commands = "";
		if (this.commands != null) {
			commands = StringUtils.join(this.commands, "\n");
		}

		String title = "";
		if (this.title != null) {
			title = StringUtils.join(this.title, "\n");
		}

		if (StorageManager.isMySQL()) {
			assert StorageManager.getMySQLDatabase() != null : "Create warp failed.";
			success = StorageManager.getMySQLDatabase().insert(
				Main.ESSENTIAL_WARP_DATABASE_TABLE,
				new String[]{"name", "display", "lore", "enabled", "price", "permission", "slot", "cooldown", "material", "command", "title", "location_world", "location_x", "location_y", "location_z", "location_yaw", "location_pitch"},
				new Object[]{this.name, this.displayName, lores, this.enabled ? 1 : 0, this.price, this.permission, this.slot, this.cooldown, this.material, commands, title, this.location.getWorld().getName(), this.location.getX(),
					this.location.getY(),
					this.location.getZ(),
					this.location.getYaw(),
					this.location.getPitch()});
		} else {
			assert StorageManager.getSQLiteDatabase() != null : "Create warp failed.";
			success = StorageManager.getSQLiteDatabase().insert(
				Main.ESSENTIAL_WARP_DATABASE_TABLE,
				new String[]{"name", "display", "lore", "enabled", "price", "permission", "slot", "cooldown", "material", "command", "title", "location_world", "location_x", "location_y", "location_z", "location_yaw", "location_pitch"},
				new Object[]{this.name, this.displayName, lores, this.enabled ? 1 : 0, this.price, this.permission, this.slot, this.cooldown, this.material, commands, title, this.location.getWorld().getName(), this.location.getX(),
					this.location.getY(),
					this.location.getZ(),
					this.location.getYaw(),
					this.location.getPitch()});
		}

		if (!success) return false;

		WARPS.remove(this);
		WARPS.add(this);

		return true;
	}

	public boolean update() {
		boolean success;

		String lores = "";
		if (this.lore != null) {
			lores = StringUtils.join(this.lore, "\n");
		}

		String commands = "";
		if (this.commands != null) {
			commands = StringUtils.join(this.commands, "\n");
		}

		String title = "";
		if (this.title != null) {
			title = StringUtils.join(this.title, "\n");
		}

		if (StorageManager.isMySQL()) {
			assert StorageManager.getMySQLDatabase() != null : "Update warp entry failed.";
			success = StorageManager.getMySQLDatabase().update(
				Main.ESSENTIAL_WARP_DATABASE_TABLE,
				new String[]{"display", "lore", "enabled", "price", "permission", "slot", "cooldown", "material", "command", "title", "location_world", "location_x", "location_y", "location_z", "location_yaw", "location_pitch"},
				new Object[]{this.displayName, lores, this.enabled ? 1 : 0, this.price, this.permission, this.slot, this.cooldown, this.material, commands, title, this.location.getWorld().getName(), this.location.getX(),
					this.location.getY(),
					this.location.getZ(),
					this.location.getYaw(),
					this.location.getPitch()},
				"name",
				this.getName());
		} else {
			assert StorageManager.getSQLiteDatabase() != null : "Update warp entry failed.";
			success = StorageManager.getSQLiteDatabase().update(
				Main.ESSENTIAL_WARP_DATABASE_TABLE,
				new String[]{"display", "lore", "enabled", "price", "permission", "slot", "cooldown", "material", "command", "title", "location_world", "location_x", "location_y", "location_z", "location_yaw", "location_pitch"},
				new Object[]{this.displayName, lores, this.enabled ? 1 : 0, this.price, this.permission, this.slot, this.cooldown, this.material, commands, title, this.location.getWorld().getName(), this.location.getX(),
					this.location.getY(),
					this.location.getZ(),
					this.location.getYaw(),
					this.location.getPitch()},
				"name",
				this.getName());
		}

		return success;
	}

	public boolean delete() {
		boolean success;

		if (StorageManager.isMySQL()) {
			assert StorageManager.getMySQLDatabase() != null : "Delete warp entry failed.";
			success = StorageManager.getMySQLDatabase().delete(Main.ESSENTIAL_WARP_DATABASE_TABLE, "name", name);
		} else {
			assert StorageManager.getSQLiteDatabase() != null : "Delete warp entry failed.";
			success = StorageManager.getSQLiteDatabase().delete(Main.ESSENTIAL_WARP_DATABASE_TABLE, "name", name);
		}

		if (!success) return false;

		WARPS.remove(this);

		return true;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public List<String> getLore() {
		if (lore == null) {
			return new ArrayList<>();
		}

		return lore;
	}

	public void setLore(List<String> lore) {
		this.lore = lore;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	public int getSlot() {
		return slot;
	}

	public void setSlot(int slot) {
		this.slot = slot;
	}

	public int getCooldown() {
		return cooldown;
	}

	public void setCooldown(int cooldown) {
		this.cooldown = cooldown;
	}

	public Material getMaterial() {
		return material;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public List<String> getCommands() {
		if (commands == null) {
			return new ArrayList<>();
		}

		return commands;
	}

	public void setCommands(List<String> commands) {
		this.commands = commands;
	}

	public List<String> getTitle() {
		if (title == null) {
			return new ArrayList<>();
		}

		return title;
	}

	public void setTitle(List<String> title) {
		this.title = title;
	}

	public static boolean loadWarps() {
		WARPS.clear();

		String[] columns = new String[]{"name", "display", "lore", "enabled", "price", "permission", "slot", "cooldown", "material", "command", "title", "location_world", "location_x", "location_y", "location_z", "location_yaw", "location_pitch"};
		ResultSet result;

		if (StorageManager.isMySQL()) {
			assert StorageManager.getMySQLDatabase() != null : "Load warps failed.";
			result = StorageManager.getMySQLDatabase().select(
				Main.ESSENTIAL_WARP_DATABASE_TABLE,
				columns);
		} else {
			assert StorageManager.getSQLiteDatabase() != null : "Load warps failed.";
			result = StorageManager.getSQLiteDatabase().select(
				Main.ESSENTIAL_WARP_DATABASE_TABLE,
				columns);
		}

		try {
			while (result.next()) {
				Location location = new Location(
					Bukkit.getWorld(result.getString("location_world")),
					result.getDouble("location_x"),
					result.getDouble("location_y"),
					result.getDouble("location_z"),
					result.getFloat("location_yaw"),
					result.getFloat("location_pitch")
				);

				String loreString = result.getString("lore");
				List<String> lore = new ArrayList<>();

				if (loreString != null) {
					if (loreString.length() > 0) {
						lore = Arrays.asList(loreString.split("\n"));
					}
				}

				String commandString = result.getString("command");
				List<String> commands = new ArrayList<>();

				if (commandString != null) {
					if (commandString.length() > 0) {
						commands = Arrays.asList(commandString.split("\n"));
					}
				}

				String titleString = result.getString("title");
				List<String> title = new ArrayList<>();

				if (titleString != null) {
					if (titleString.length() > 0) {
						title = Arrays.asList(titleString.split("\n"));
					}
				}

				new Warp(
					result.getString("name"),
					result.getString("display"),
					lore,
					result.getBoolean("enabled"),
					result.getDouble("price"),
					result.getString("permission"),
					result.getInt("slot"),
					result.getInt("cooldown"),
					CompatibleMaterial.getMaterial(result.getString("material")).getMaterial(),
					location,
					commands,
					title
				);
			}

			result.close();

			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	public static Warp getWarp(String name) {
		for (Warp warp : WARPS) {
			if (warp == null) continue;
			if (warp.getName().equalsIgnoreCase(name)) return warp;
		}

		return null;
	}

	public static boolean removeWarp(String name) {
		Warp warp = getWarp(name);
		if (warp == null) return false;

		boolean success;

		if (StorageManager.isMySQL()) {
			assert StorageManager.getMySQLDatabase() != null : "Delete warp entry failed.";
			success = StorageManager.getMySQLDatabase().delete(Main.ESSENTIAL_WARP_DATABASE_TABLE, "name", name);
		} else {
			assert StorageManager.getSQLiteDatabase() != null : "Delete warp entry failed.";
			success = StorageManager.getSQLiteDatabase().delete(Main.ESSENTIAL_WARP_DATABASE_TABLE, "name", name);
		}

		if (!success) return success;

		WARPS.remove(warp);
		return success;
	}

	public static List<Warp> getWarps() {
		return WARPS;

	}
}
