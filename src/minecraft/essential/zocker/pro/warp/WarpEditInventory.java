package minecraft.essential.zocker.pro.warp;

import minecraft.core.zocker.pro.Zocker;
import minecraft.core.zocker.pro.compatibility.CompatibleHand;
import minecraft.core.zocker.pro.compatibility.CompatibleMaterial;
import minecraft.core.zocker.pro.compatibility.CompatibleSound;
import minecraft.core.zocker.pro.inventory.InventoryEntry;
import minecraft.core.zocker.pro.inventory.InventoryUpdateZocker;
import minecraft.core.zocker.pro.inventory.InventoryZocker;
import minecraft.core.zocker.pro.inventory.builder.InventoryEntryBuilder;
import minecraft.core.zocker.pro.inventory.util.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class WarpEditInventory extends InventoryUpdateZocker {

	private Zocker zocker;
	private Warp warp;
	private int lorePosition = 0;
	private int commandPosition = 0;
	private int titlePosition = 0;

	public WarpEditInventory(Zocker zocker, Warp warp) {
		this.zocker = zocker;
		this.warp = warp;
	}

	@Override
	public String getTitle() {
		return "Warp Edit";
	}

	@Override
	public InventoryEntry getInfoSign() {
		return new InventoryEntryBuilder()
			.setItem(new ItemBuilder(CompatibleMaterial.OAK_SIGN.getMaterial())
				.setName("§6§lInformation")
				.setLore(
					"",
					"§6Placeholders §3for Lore, Title & Command",
					"§6%price%§3, §6%cooldown%§3, §6%player%",
					"",
					"§6Commands",
					"§6[c] §3= run as console",
					"§6[p] §3= run as player",
					"",
					"§6Action§3 for Lore, Title & Command",
					"§3Left click: §6Edit",
					"§3Right click: §6New line",
					"§3Middle click: §6Change position",
					"§3Shift-Right click: §6Remove"))
			.build();
	}

	@Override
	public InventoryType getInventoryType() {
		return InventoryType.CHEST;
	}

	@Override
	public Integer getSize() {
		return 54;
	}

	@Override
	public void onOpen(InventoryZocker inventoryZocker, InventoryOpenEvent inventoryOpenEvent) {
	}

	@Override
	public void onClose(InventoryZocker inventoryZocker, InventoryCloseEvent inventoryCloseEvent) {
		warp.update();
	}

	@Override
	public void setupInventory() {
		this.fillBorders();
		this.setUpdate(true);
		this.setUpdateOffset(1);
		this.setUpdateTimeUnit(TimeUnit.SECONDS);

		this.addItem(new InventoryEntryBuilder()
			.setAsync(false)
			.setSlot(48)
			.onAllClicks(e -> new WarpListInventory(zocker).open(zocker))
			.setItem(this.getPreviousArrow().getItem()).build());

		this.setItem(new InventoryEntryBuilder()
			.setItem(new ItemBuilder(CompatibleMaterial.REDSTONE_BLOCK.getMaterial())
				.setName("§c§lDelete"))
			.onAllClicks(inventoryClickEvent -> {
				warp.delete();
				CompatibleSound.playSuccessSound(zocker.getPlayer());
				new WarpListInventory(zocker).open(zocker);
			})
			.setAsync(false)
			.setSlot(13)
			.build());

		this.setItem(new InventoryEntryBuilder()
			.setItem(new ItemBuilder(CompatibleMaterial.PAPER.getMaterial())
				.setName("§6§lDisplay Name")
				.addLore("§3" + warp.getDisplayName()))
			.onAllClicks(inventoryClickEvent -> new WarpEditAnvilInventory(zocker, warp, WarpEditType.DISPLAY_NAME).open(zocker))
			.setAsync(false)
			.setSlot(20)
			.build());

		this.setItem(new InventoryEntryBuilder()
			.setItem(new ItemBuilder(CompatibleMaterial.SUNFLOWER.getMaterial())
				.setName("§6§lPrice")
				.addLore("§3" + warp.getPrice()))
			.onAllClicks(inventoryClickEvent -> new WarpEditAnvilInventory(zocker, warp, WarpEditType.PRICE).open(zocker))
			.setAsync(false)
			.setSlot(22)
			.build());

		this.setItem(new InventoryEntryBuilder()
			.setItem(new ItemBuilder(CompatibleMaterial.ENDER_EYE.getMaterial())
				.setName("§6§lPermission")
				.addLore("§3" + warp.getPermission()))
			.onAllClicks(inventoryClickEvent -> new WarpEditAnvilInventory(zocker, warp, WarpEditType.PERMISSION).open(zocker))
			.setAsync(false)
			.setSlot(23)
			.build());

		this.setItem(new InventoryEntryBuilder()
			.setItem(new ItemBuilder(CompatibleMaterial.IRON_BARS.getMaterial())
				.setName("§6§lPosition")
				.addLore("§3" + warp.getSlot()))
			.onAllClicks(inventoryClickEvent -> new WarpEditAnvilInventory(zocker, warp, WarpEditType.SLOT).open(zocker))
			.setAsync(false)
			.setSlot(24)
			.build());

		this.setItem(new InventoryEntryBuilder()
			.setItem(new ItemBuilder(CompatibleMaterial.CLOCK.getMaterial())
				.setName("§6§lCooldown")
				.addLore("§3" + warp.getCooldown()))
			.onAllClicks(inventoryClickEvent -> new WarpEditAnvilInventory(zocker, warp, WarpEditType.COOLDOWN).open(zocker))
			.setAsync(false)
			.setSlot(30)
			.build());

	}

	@Override
	public void onUpdate() {

		// region Lore
		this.setItem(new InventoryEntryBuilder()
			.setItem(new ItemBuilder(CompatibleMaterial.BLAZE_ROD.getMaterial())
				.setName("§6§lLore §7[" + (this.lorePosition + 1) + "]")
				.setLore(warp.getLore()))
			.addAction(ClickType.MIDDLE, inventoryClickEvent -> {  // change position
				if ((warp.getLore().size() - 1) > this.lorePosition) {
					this.lorePosition++;
					CompatibleSound.playChangedSound(zocker.getPlayer());
					return;
				}

				if (warp.getLore().size() >= this.lorePosition) {
					this.lorePosition = 0;
				}

				CompatibleSound.playErrorSound(zocker.getPlayer());
			})
			.addAction(ClickType.LEFT, inventoryClickEvent -> {
				if (warp.getLore() == null || warp.getLore().isEmpty()) {
					CompatibleSound.playErrorSound(zocker.getPlayer());
					return;
				}

				new WarpEditAnvilInventory(zocker, warp, WarpEditType.LORE, this.lorePosition).open(zocker); // edit x position
			})
			.addAction(ClickType.RIGHT, inventoryClickEvent -> { // player.
				List<String> lores = new ArrayList<>();
				if (warp.getLore() != null && !warp.getLore().isEmpty()) {
					lores = new ArrayList(Arrays.asList(warp.getLore().toArray(new String[0])));
				}

				lores.add("&3new line");
				warp.setLore(lores);
			})
			.addAction(ClickType.SHIFT_RIGHT, inventoryClickEvent -> {
				List<String> lores;
				if (warp.getLore() != null && !warp.getLore().isEmpty()) {
					lores = new ArrayList(Arrays.asList(warp.getLore().toArray(new String[0])));
				} else {
					CompatibleSound.playErrorSound(zocker.getPlayer());
					return;
				}

				lores.remove(this.lorePosition);
				warp.setLore(lores);

				if (this.lorePosition != 0) {
					this.lorePosition--;
				}
			})
			.setAsync(false)
			.setSlot(21)
			.build());

		// endregion

		this.setItem(new InventoryEntryBuilder()
			.setItem(new ItemBuilder(CompatibleMaterial.ARMOR_STAND.getMaterial())
				.setName("§6§lMaterial")
				.addLore("§3" + warp.getMaterial().name()))
			.onAllClicks(inventoryClickEvent -> {
				Player player = zocker.getPlayer();
				ItemStack itemStack = CompatibleHand.MAIN_HAND.getItem(player);

				if (itemStack.getType() == CompatibleMaterial.AIR.getMaterial()) {
					CompatibleSound.playErrorSound(player);
					return;
				}

				warp.setMaterial(CompatibleMaterial.getMaterial(itemStack).getMaterial());
			})
			.setSlot(29)
			.build());

		if (warp.isEnabled()) {
			this.setItem(new InventoryEntryBuilder()
				.setItem(new ItemBuilder(CompatibleMaterial.LIME_DYE.getItem())
					.setName("§6§lEnabled"))
				.onAllClicks(inventoryClickEvent -> {
					warp.setEnabled(false);
				})
				.setSlot(31)
				.build());
		} else {
			this.setItem(new InventoryEntryBuilder()
				.setItem(new ItemBuilder(CompatibleMaterial.RED_DYE.getItem())
					.setName("§6§lDisabled"))
				.onAllClicks(inventoryClickEvent -> {
					warp.setEnabled(true);
				})
				.setSlot(31)
				.build());
		}

		// region Command
		this.setItem(new InventoryEntryBuilder()
			.setItem(new ItemBuilder(CompatibleMaterial.COMMAND_BLOCK.getMaterial())
				.setName("§6§lCommand §7[" + (this.commandPosition + 1) + "]")
				.setLore(warp.getCommands().stream().map(s -> s = "§3" + s).collect(Collectors.toList())))
			.addAction(ClickType.MIDDLE, inventoryClickEvent -> {  // change position
				if ((warp.getCommands().size() - 1) > this.commandPosition) {
					this.commandPosition++;
					CompatibleSound.playChangedSound(zocker.getPlayer());
					return;
				}

				if (warp.getCommands().size() >= this.commandPosition) {
					this.commandPosition = 0;
				}

				CompatibleSound.playErrorSound(zocker.getPlayer());
			})
			.addAction(ClickType.LEFT, inventoryClickEvent -> {
				if (warp.getCommands() == null || warp.getCommands().isEmpty()) {
					CompatibleSound.playErrorSound(zocker.getPlayer());
					return;
				}

				new WarpEditAnvilInventory(zocker, warp, WarpEditType.COMMAND, this.commandPosition).open(zocker); // edit x position
			})
			.addAction(ClickType.RIGHT, inventoryClickEvent -> { // add new line
				List<String> commands = new ArrayList<>();
				if (warp.getCommands() != null && !warp.getCommands().isEmpty()) {
					commands = new ArrayList(Arrays.asList(warp.getCommands().toArray(new String[0])));
				}

				commands.add("new line");
				warp.setCommands(commands);
			})
			.addAction(ClickType.SHIFT_RIGHT, inventoryClickEvent -> {
				List<String> commands;
				if (warp.getCommands() != null && !warp.getCommands().isEmpty()) {
					commands = new ArrayList(Arrays.asList(warp.getCommands().toArray(new String[0])));
				} else {
					CompatibleSound.playErrorSound(zocker.getPlayer());
					return;
				}

				commands.remove(this.commandPosition);
				warp.setCommands(commands);

				if (this.commandPosition != 0) {
					this.commandPosition--;
				}
			})
			.setAsync(false)
			.setSlot(32)
			.build());

		// endregion

		// region Title
		this.setItem(new InventoryEntryBuilder()
			.setItem(new ItemBuilder(CompatibleMaterial.ITEM_FRAME.getMaterial())
				.setName("§6§lTitle §7[" + (this.titlePosition + 1) + "]")
				.setLore(warp.getTitle().stream().map(s -> s = s.replace("&", "§")).collect(Collectors.toList())))
			.addAction(ClickType.MIDDLE, inventoryClickEvent -> {  // change position
				if (this.titlePosition >= 1) {
					this.titlePosition = 0;
					CompatibleSound.playErrorSound(zocker.getPlayer());
					return;
				}

				if ((warp.getTitle().size() - 1) > this.titlePosition) {
					this.titlePosition++;
					CompatibleSound.playChangedSound(zocker.getPlayer());
					return;
				}

				CompatibleSound.playErrorSound(zocker.getPlayer());
			})
			.addAction(ClickType.LEFT, inventoryClickEvent -> {
				if (warp.getTitle() == null || warp.getTitle().isEmpty()) {
					CompatibleSound.playErrorSound(zocker.getPlayer());
					return;
				}

				new WarpEditAnvilInventory(zocker, warp, WarpEditType.TITLE, this.titlePosition).open(zocker); // edit x position
			})
			.addAction(ClickType.RIGHT, inventoryClickEvent -> { // add new line
				List<String> title = new ArrayList<>();
				if (warp.getTitle() != null && !warp.getTitle().isEmpty()) {
					title = new ArrayList(Arrays.asList(warp.getTitle().toArray(new String[0])));
				}

				if (title.size() >= 2) {
					CompatibleSound.playErrorSound(zocker.getPlayer());
					return;
				}

				title.add("&3new line");
				warp.setTitle(title);
			})
			.addAction(ClickType.SHIFT_RIGHT, inventoryClickEvent -> {
				List<String> title;
				if (warp.getTitle() != null && !warp.getTitle().isEmpty()) {
					title = new ArrayList(Arrays.asList(warp.getTitle().toArray(new String[0])));
				} else {
					CompatibleSound.playErrorSound(zocker.getPlayer());
					return;
				}

				title.remove(this.titlePosition);
				warp.setTitle(title);

				if (this.titlePosition != 0) {
					this.titlePosition--;
				}
			})
			.setAsync(false)
			.setSlot(33)
			.build());

		// endregion
	}
}
