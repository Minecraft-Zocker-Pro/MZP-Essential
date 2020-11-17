package minecraft.essential.zocker.pro.warp;

import minecraft.core.zocker.pro.Zocker;
import minecraft.core.zocker.pro.compatibility.CompatibleMaterial;
import minecraft.core.zocker.pro.inventory.InventoryAnvilZocker;
import minecraft.core.zocker.pro.inventory.InventoryEntry;
import minecraft.core.zocker.pro.inventory.InventoryZocker;
import minecraft.core.zocker.pro.inventory.builder.InventoryEntryBuilder;
import minecraft.core.zocker.pro.inventory.util.ItemBuilder;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

import java.util.*;

public class WarpEditAnvilInventory extends InventoryAnvilZocker {

	private Zocker zocker;
	private Warp warp;
	private WarpEditType type;
	private int position = 0;

	public WarpEditAnvilInventory(Zocker zocker, Warp warp, WarpEditType type) {
		this.zocker = zocker;
		this.warp = warp;
		this.type = type;
	}

	public WarpEditAnvilInventory(Zocker zocker, Warp warp, WarpEditType type, int position) {
		this.zocker = zocker;
		this.warp = warp;
		this.type = type;
		this.position = position;
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
				displayName = warp.getDisplayName().replace("§", "&");
				break;
			}

			case LORE: {
				if (warp.getLore().isEmpty()) {
					displayName = "New lore";
					break;
				}

				displayName = warp.getLore().get(this.position).replace("§", "&");
				break;
			}

			case SLOT: {
				displayName = String.valueOf(warp.getSlot());
				break;
			}

			case PRICE: {
				displayName = String.valueOf(warp.getPrice());
				break;
			}

			case COOLDOWN: {
				displayName = String.valueOf(warp.getCooldown());
				break;
			}

			case PERMISSION: {
				if (warp.getPermission() == null) {
					displayName = "New permission";
					break;
				}

				displayName = warp.getPermission();
				break;
			}

			case COMMAND: {
				if (warp.getCommands().isEmpty()) {
					displayName = "New command";
					break;
				}

				displayName = warp.getCommands().get(this.position).replace("§", "&");
				break;
			}

			case TITLE: {
				if (warp.getTitle().isEmpty()) {
					displayName = "New title";
					break;
				}

				displayName = warp.getTitle().get(this.position).replace("§", "&");
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
		output = output.replace("&", "§");
		switch (type) {
			case DISPLAY_NAME: {
				warp.setDisplayName(output);
				break;
			}

			case LORE: {
				List<String> lores = new ArrayList(Arrays.asList(warp.getLore().toArray(new String[0])));
				if (!lores.isEmpty()) {
					lores.remove(this.position);
				}

				lores.add(this.position, output);
				warp.setLore(lores);
				break;
			}

			case SLOT: {
				warp.setSlot(Integer.valueOf(output));
				break;
			}

			case PRICE: {
				warp.setPrice(Integer.valueOf(output));
				break;
			}

			case COOLDOWN: {
				warp.setCooldown(Integer.valueOf(output));
				break;
			}

			case PERMISSION: {
				warp.setPermission(output);
				break;
			}

			case COMMAND: {
				List<String> commands = new ArrayList(Arrays.asList(warp.getCommands().toArray(new String[0])));

				commands.remove(this.position);
				commands.add(this.position, output);
				warp.setCommands(commands);
				break;
			}

			case TITLE: {
				List<String> title = new ArrayList(Arrays.asList(warp.getTitle().toArray(new String[0])));

				title.remove(this.position);
				title.add(this.position, output);
				warp.setTitle(title);
				break;
			}
		}


		this.zocker.getPlayer().closeInventory();
		new WarpEditInventory(zocker, warp).open(zocker);
	}

	@Override
	public int levelCost() {
		return 0;
	}

	@Override
	public String getTitle() {
		return "Warp Edit";
	}

	@Override
	public void onOpen(InventoryZocker inventoryZocker, InventoryOpenEvent inventoryOpenEvent) {
	}

	@Override
	public void onClose(InventoryZocker inventoryZocker, InventoryCloseEvent inventoryCloseEvent) {
		new WarpEditInventory(zocker, warp).open(zocker);
	}

	@Override
	public void setupInventory() {
	}
}
