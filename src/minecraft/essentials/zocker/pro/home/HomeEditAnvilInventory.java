package minecraft.essentials.zocker.pro.home;

import minecraft.core.zocker.pro.Zocker;
import minecraft.core.zocker.pro.compatibility.CompatibleMaterial;
import minecraft.core.zocker.pro.inventory.InventoryAnvilZocker;
import minecraft.core.zocker.pro.inventory.InventoryEntry;
import minecraft.core.zocker.pro.inventory.InventoryZocker;
import minecraft.core.zocker.pro.inventory.builder.InventoryEntryBuilder;
import minecraft.core.zocker.pro.inventory.util.ItemBuilder;
import minecraft.essentials.zocker.pro.Main;
import minecraft.essentials.zocker.pro.warp.WarpEditInventory;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class HomeEditAnvilInventory extends InventoryAnvilZocker {

	private Zocker zocker;
	private Home home;
	private HomeEditType type;

	public HomeEditAnvilInventory(Zocker zocker, Home home, HomeEditType type) {
		this.zocker = zocker;
		this.home = home;
		this.type = type;
	}

	@Override
	public InventoryEntry getLeftInventoryEntry() {
		return new InventoryEntryBuilder()
			.setItem(CompatibleMaterial.PAPER.getItem())
			.setSlot(1)
			.build();
	}

	@Override
	public InventoryEntry getRightInventoryEntry() {
		return new InventoryEntryBuilder()
			.setItem(CompatibleMaterial.PAPER.getItem())
			.setSlot(2)
			.build();
	}

	@Override
	public InventoryEntry getResultInventoryEntry() {
		String displayName;
		switch (type) {
			case DISPLAY_NAME: {
				displayName = home.getName().replace("§", "&");
				break;
			}

			default: {
				displayName = "";
			}
		}

		return new InventoryEntryBuilder()
			.setItem(new ItemBuilder(CompatibleMaterial.PAPER.getMaterial())
				.setDisplayName(displayName))
			.setSlot(0)
			.build();
	}

	@Override
	public void onResult(String output) {
		if (zocker.getPlayer().hasPermission("mzp.essential.home.edit.color")) {
			output = output.replace("&", "§");
		} else {
			output = "§6" + output;
		}

		switch (type) {
			case DISPLAY_NAME: {
				home.setName(output);
				break;
			}
		}

		this.zocker.getPlayer().closeInventory();
		new HomeEditInventory(zocker, home).open(zocker);
	}

	@Override
	public int levelCost() {
		return 0;
	}

	@Override
	public String getTitle() {
		return "Home Edit";
	}

	@Override
	public void onOpen(InventoryZocker inventoryZocker, InventoryOpenEvent inventoryOpenEvent) {

	}

	@Override
	public void onClose(InventoryZocker inventoryZocker, InventoryCloseEvent inventoryCloseEvent) {
		new HomeEditInventory(zocker, home).open(zocker);
	}

	@Override
	public void setupInventory() {
	}
}