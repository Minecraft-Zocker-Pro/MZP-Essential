package minecraft.essentials.zocker.pro.home;

import minecraft.core.zocker.pro.Zocker;
import minecraft.core.zocker.pro.compatibility.CompatibleHand;
import minecraft.core.zocker.pro.compatibility.CompatibleMaterial;
import minecraft.core.zocker.pro.compatibility.CompatibleMessage;
import minecraft.core.zocker.pro.compatibility.CompatibleSound;
import minecraft.core.zocker.pro.inventory.InventoryZocker;
import minecraft.core.zocker.pro.inventory.builder.InventoryEntryBuilder;
import minecraft.core.zocker.pro.inventory.util.ItemBuilder;
import minecraft.core.zocker.pro.util.Util;
import minecraft.essentials.zocker.pro.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class HomeEditInventory extends InventoryZocker {

	private Zocker zocker;
	private Home home;

	public HomeEditInventory(Zocker zocker, Home home) {
		this.zocker = zocker;
		this.home = home;
	}

	@Override
	public String getTitle() {
		return "Home Edit";
	}

	@Override
	public InventoryType getInventoryType() {
		return InventoryType.CHEST;
	}

	@Override
	public Integer getSize() {
		return 45;
	}

	@Override
	public void onOpen(InventoryZocker inventoryZocker, InventoryOpenEvent inventoryOpenEvent) {
	}

	@Override
	public void onClose(InventoryZocker inventoryZocker, InventoryCloseEvent inventoryCloseEvent) {
		new HomeZocker(zocker.getUUID()).updateHome(home);
	}

	@Override
	public void setupInventory() {
		this.fillBorders();

		this.addItem(new InventoryEntryBuilder()
			.setSlot(39)
			.onAllClicks(e -> new HomeListInventory(zocker).open(zocker))
			.setItem(this.getPreviousArrow().getItem()).build());

		this.setItem(new InventoryEntryBuilder()
			.setItem(new ItemBuilder(CompatibleMaterial.REDSTONE_BLOCK.getMaterial())
				.setName("§c§lDelete"))
			.onAllClicks(inventoryClickEvent -> {
				HomeZocker homeZocker = new HomeZocker(zocker.getPlayer().getUniqueId());
				try {
					boolean success = homeZocker.removeHome(home).get();
					if (success) {
						CompatibleSound.playSuccessSound(zocker.getPlayer());
						new HomeListInventory(zocker).open(zocker);
						return;
					}

					CompatibleSound.playErrorSound(zocker.getPlayer());
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
			})
			.setSlot(24)
			.build());

		this.setItem(new InventoryEntryBuilder()
			.setItem(new ItemBuilder(CompatibleMaterial.PAPER.getMaterial())
				.setName("§6§lDisplay Name")
				.addLore("§3" + home.getName()))
			.onAllClicks(inventoryClickEvent -> new HomeEditAnvilInventory(zocker, home, HomeEditType.DISPLAY_NAME).open(zocker))
			.setSlot(20)
			.build());


		this.setItem(new InventoryEntryBuilder()
			.setItem(new ItemBuilder(CompatibleMaterial.GRASS_BLOCK.getMaterial())
				.setName("§6§lPosition")
				.addLore("§3" + home.getLocation().getWorld().getName())
				.addLore("§3X: " + Util.formatDouble(home.getLocation().getX()) + " Y: " + Util.formatDouble(home.getLocation().getY()) + " Z: " + Util.formatDouble(home.getLocation().getZ()))
				.addLore("")
				.addLore("§6Click to update"))
			.onAllClicks(inventoryClickEvent -> {
				Player player = zocker.getPlayer();

				// Check world
				List<String> worldBlacklist = Main.ESSENTIAL_CONFIG.getStringList("essential.home.world.blacklist");
				if (worldBlacklist != null && !worldBlacklist.isEmpty()) {
					for (String worldName : worldBlacklist) {
						if (player.getLocation().getWorld().getName().equalsIgnoreCase(worldName)) {
							CompatibleMessage.sendMessage(player, Main.ESSENTIAL_MESSAGE.getString("essential.prefix") + Main.ESSENTIAL_MESSAGE.getString("essential.home.blacklist"));
							CompatibleSound.playErrorSound(player);
							return;
						}
					}
				}

				home.setLocation(zocker.getPlayer().getLocation());
				player.closeInventory();
				CompatibleSound.playSuccessSound(zocker.getPlayer());

				new HomeEditInventory(zocker, home).open(zocker);
			})
			.setSlot(21)
			.build());

		this.setItem(new InventoryEntryBuilder()
			.setItem(new ItemBuilder(CompatibleMaterial.ARMOR_STAND.getMaterial())
				.setName("§6§lMaterial")
				.addLore("§3" + home.getMaterial().name()))
			.onAllClicks(inventoryClickEvent -> {
				Player player = zocker.getPlayer();
				ItemStack itemStack = CompatibleHand.MAIN_HAND.getItem(player);

				if (itemStack.getType() == CompatibleMaterial.AIR.getMaterial()) {
					CompatibleSound.playErrorSound(player);
					return;
				}

				if (itemStack.getType() == home.getMaterial()) {
					CompatibleSound.playErrorSound(player);
					return;
				}

				home.setMaterial(CompatibleMaterial.getMaterial(itemStack).getMaterial());
				player.closeInventory();
				CompatibleSound.playSuccessSound(zocker.getPlayer());
				new HomeEditInventory(zocker, home).open(zocker);
			})
			.setSlot(22)
			.build());
	}
}
