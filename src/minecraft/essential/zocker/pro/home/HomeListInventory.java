package minecraft.essential.zocker.pro.home;

import minecraft.core.zocker.pro.Zocker;
import minecraft.core.zocker.pro.compatibility.CompatibleMaterial;
import minecraft.core.zocker.pro.inventory.InventoryEntry;
import minecraft.core.zocker.pro.inventory.InventoryZocker;
import minecraft.core.zocker.pro.inventory.builder.InventoryEntryBuilder;
import minecraft.core.zocker.pro.inventory.util.ItemBuilder;
import minecraft.essential.zocker.pro.Main;
import minecraft.essential.zocker.pro.util.Teleporter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;

import java.util.ArrayList;
import java.util.List;

public class HomeListInventory extends InventoryZocker {

	private Zocker zocker;

	public HomeListInventory(Zocker zocker) {
		this.zocker = zocker;
	}

	@Override
	public String getTitle() {
		return "Homes";
	}

	@Override
	public InventoryType getInventoryType() {
		return InventoryType.CHEST;
	}

	@Override
	public Integer getSize() {
		return Main.ESSENTIAL_CONFIG.getInt("essential.home.size");
	}

	@Override
	public void onOpen(InventoryZocker zockerInventory, InventoryOpenEvent inventoryOpenEvent) {
	}

	@Override
	public void onClose(InventoryZocker zockerInventory, InventoryCloseEvent inventoryCloseEvent) {
	}

	@Override
	public InventoryEntry getInfoSign() {
		List<String> lores = new ArrayList<>();
		for (String string : Main.ESSENTIAL_MESSAGE.getStringList("essential.inventory.warp.info.admin.description")) {
			string = string.replace("&", "§");
			lores.add(string);
		}

		return new InventoryEntryBuilder()
			.setItem(new ItemBuilder(CompatibleMaterial.OAK_SIGN.getMaterial())
				.setName(Main.ESSENTIAL_MESSAGE.getString("essential.inventory.home.info.title"))
				.setLore(lores))
			.setAsync(false)
			.build();
	}

	@Override
	public void setupInventory() {
		this.fillBorders();

		HomeZocker homeZocker = new HomeZocker(zocker.getUUID());

		int count = 10;
		for (Home home : homeZocker.getHomes()) {
			if (home == null) continue;

			this.setItem(new InventoryEntryBuilder()
				.setItem(new ItemBuilder(home.getMaterial())
					.setName("§6§l" + home.getName()))
				.onLeftClick(e -> {
					Player player = zocker.getPlayer();
					player.closeInventory();
					Teleporter teleporter = new Teleporter(player, home.getLocation(), Main.ESSENTIAL_CONFIG.getInt("essential.home.cooldown"), false);
					teleporter.teleport();
				})
				.onRightClick(e -> {
					Player player = zocker.getPlayer();
					player.closeInventory();
					Teleporter teleporter = new Teleporter(player, home.getLocation(), Main.ESSENTIAL_CONFIG.getInt("essential.home.cooldown"), false);
					teleporter.teleport();
				})
				.addAction(ClickType.MIDDLE, e -> {
					new HomeEditInventory(zocker, home).open(zocker);
				})
				.setAsync(false)
				.setSlot(count)
				.build());

			if (count == 16) {
				count += 3;
				continue;
			}

			if (count == 25) {
				count += 3;
				continue;
			}

			if (count == 34) {
				count += 3;
				continue;
			}

			count++;
		}
	}
}
