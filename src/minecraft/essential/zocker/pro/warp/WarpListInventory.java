package minecraft.essential.zocker.pro.warp;

import minecraft.core.zocker.pro.Zocker;
import minecraft.core.zocker.pro.compatibility.CompatibleMaterial;
import minecraft.core.zocker.pro.compatibility.CompatibleMessage;
import minecraft.core.zocker.pro.compatibility.CompatibleSound;
import minecraft.core.zocker.pro.inventory.InventoryEntry;
import minecraft.core.zocker.pro.inventory.InventoryZocker;
import minecraft.core.zocker.pro.inventory.builder.InventoryEntryBuilder;
import minecraft.core.zocker.pro.inventory.util.ItemBuilder;
import minecraft.essential.zocker.pro.Main;
import minecraft.essential.zocker.pro.util.Teleporter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemFlag;

import java.util.ArrayList;
import java.util.List;

public class WarpListInventory extends InventoryZocker {

	private Zocker zocker;

	public WarpListInventory(Zocker zocker) {
		this.zocker = zocker;
	}

	@Override
	public String getTitle() {
		return "Warps";
	}

	@Override
	public InventoryType getInventoryType() {
		return InventoryType.CHEST;
	}

	@Override
	public Integer getSize() {
		if (Main.ESSENTIAL_CONFIG.getInt("essential.warp.size") == 0) return 45;
		return Main.ESSENTIAL_CONFIG.getInt("essential.warp.size");
	}

	@Override
	public InventoryEntry getInfoSign() {
		if (zocker.getPlayer().hasPermission("mzp.essential.warp.info.admin")) {
			List<String> lores = new ArrayList<>();
			for (String string : Main.ESSENTIAL_MESSAGE.getStringList("essential.inventory.warp.info.admin.description")) {
				string = string.replace("&", "§");
				lores.add(string);
			}

			return new InventoryEntryBuilder()
				.setItem(new ItemBuilder(CompatibleMaterial.OAK_SIGN.getMaterial())
					.setName(Main.ESSENTIAL_MESSAGE.getString("essential.inventory.warp.info.admin.title"))
					.setLore(lores))
				.build();
		}

		if (zocker.getPlayer().hasPermission("mzp.essential.warp.info.player")) {
			List<String> lores = new ArrayList<>();
			for (String string : Main.ESSENTIAL_MESSAGE.getStringList("essential.inventory.warp.info.player.description")) {
				string = string.replace("&", "§");
				lores.add(string);
			}
			return new InventoryEntryBuilder()
				.setItem(new ItemBuilder(CompatibleMaterial.OAK_SIGN.getMaterial())
					.setName(Main.ESSENTIAL_MESSAGE.getString("essential.inventory.warp.info.player.title"))
					.setLore(lores))
				.build();
		}


		return null;
	}

	@Override
	public void onOpen(InventoryZocker inventoryZocker, InventoryOpenEvent inventoryOpenEvent) {
	}

	@Override
	public void onClose(InventoryZocker inventoryZocker, InventoryCloseEvent inventoryCloseEvent) {
	}

	@Override
	public void setupInventory() {
		this.fillBorders();

		Player player = zocker.getPlayer();

		for (Warp warp : Warp.getWarps()) {
			if (warp == null) continue;
			if (!warp.isEnabled()) continue;
			if (warp.getSlot() == 0) continue;
			if (!player.hasPermission(warp.getPermission())) continue;

			List<String> lores = new ArrayList<>();
			if (warp.getLore() != null) {
				for (String lore : warp.getLore()) {
					lore = lore.replace("%price%", String.valueOf(warp.getPrice()))
						.replace("%cooldown%", String.valueOf(warp.getCooldown()))
						.replace("%player%", zocker.getPlayer().getName());

					lores.add(lore);
				}
			}

			this.setItem(new InventoryEntryBuilder()
				.setItem(new ItemBuilder(warp.getMaterial())
					.setLore(lores)
					.addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
					.addItemFlag(ItemFlag.HIDE_PLACED_ON)
					.addItemFlag(ItemFlag.HIDE_DESTROYS)
					.setDisplayName(warp.getDisplayName().replace("&", "§")))
				.setSlot(warp.getSlot())
				.onLeftClick(inventoryClickEvent -> handleWarp(player, warp))
				.onRightClick(inventoryClickEvent -> handleWarp(player, warp))
				.addAction(ClickType.MIDDLE, inventoryClickEvent -> new WarpEditInventory(zocker, warp).open(zocker))
				.build());
		}
	}

	private void handleWarp(Player player, Warp warp) {
		if (warp.getPrice() > 0) {
			if (Main.getEconomy().getBalance(player) >= warp.getPrice()) {
				Main.getEconomy().withdrawPlayer(player, warp.getPrice());
				player.closeInventory();
				handleTeleport(player, warp);
				return;
			}

			player.closeInventory();
			CompatibleMessage.sendMessage(player, Main.ESSENTIAL_MESSAGE.getString("essential.prefix") + Main.ESSENTIAL_MESSAGE.getString("essential.warp.notenough"));
			CompatibleSound.playErrorSound(player);
			return;
		}

		player.closeInventory();
		handleTeleport(player, warp);
	}

	private void handleTeleport(Player player, Warp warp) {
		Teleporter teleporter = new Teleporter(player, warp.getLocation(), warp.getCooldown(), true);
		teleporter.setTeleporterListener(new Teleporter.TeleporterListener() {
			@Override
			public void onCanceled() {
				Main.getEconomy().depositPlayer(player, warp.getPrice());
			}

			@Override
			public void onTeleported() {
				if (warp.getCommands() != null && !warp.getCommands().isEmpty()) {
					for (String command : warp.getCommands()) {
						String cmd;
						if (command.startsWith("[c]")) {
							cmd = command.substring(3);
							cmd = cmd
								.replace("%player%", zocker.getPlayer().getName())
								.replace("%price%", String.valueOf(warp.getPrice()))
								.replace("%cooldown%", String.valueOf(warp.getCooldown()));
							Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
						} else if (command.startsWith("[p]")) {
							cmd = command.substring(3);
							player.performCommand(cmd);
						} else {
							player.performCommand(command);
						}
					}
				}

				List<String> title = warp.getTitle();
				if (title != null && !title.isEmpty()) {
					String titleTranslated = title.get(0)
						.replace("%player%", player.getName())
						.replace("%price%", String.valueOf(warp.getPrice()))
						.replace("%cooldown%", String.valueOf(warp.getCooldown()));

					if (title.size() <= 1) {
						CompatibleMessage.sendTitle(player, titleTranslated, "");
					} else {
						String subTitleTranslated = title.get(1)
							.replace("%player%", player.getName())
							.replace("%price%", String.valueOf(warp.getPrice()))
							.replace("%cooldown%", String.valueOf(warp.getCooldown()));
						CompatibleMessage.sendTitle(player, titleTranslated, subTitleTranslated);
					}
				}
			}
		});

		teleporter.teleport();
	}
}
