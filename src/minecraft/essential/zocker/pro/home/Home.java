package minecraft.essential.zocker.pro.home;

import org.bukkit.Location;
import org.bukkit.Material;

public class Home {

	private String id;
	private String name;
	private boolean publics;
	private Material material;
	private Location location;

	public Home(String id, String name, Location location) {
		new Home(id, name, false, Material.ENDER_PEARL, location);
	}

	public Home(String id, String name, boolean publics, Material material, Location location) {
		this.id = id;
		this.name = name;
		this.publics = publics;
		this.material = material;
		this.location = location;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isPublic() {
		return publics;
	}

	public void setPublic(boolean publics) {
		this.publics = publics;
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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
