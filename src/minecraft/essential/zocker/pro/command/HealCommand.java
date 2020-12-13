package minecraft.essential.zocker.pro.command;

import minecraft.core.zocker.pro.command.Command;
import minecraft.core.zocker.pro.compatibility.CompatibleMessage;
import minecraft.core.zocker.pro.compatibility.CompatibleSound;
import minecraft.essential.zocker.pro.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class HealCommand extends Command {

	public HealCommand() {
		super("heal", "mzp.essential.heal", new String[]{"heal"});
	}

	@Override
	public List<String> onTabComplete(CommandSender commandSender, String[] strings) {
		return null;
	}

	@Override
	public void onExecute(CommandSender sender, String[] args) {
		// self heal
		if (args.length <= 0) {
			if (sender instanceof Player) {
				Player player = (Player) sender;
				if (player.getHealth() <= 0) return;
				heal(sender, player, true);
				CompatibleMessage.sendMessage(sender, Main.ESSENTIAL_MESSAGE.getString("essential.prefix") + Main.ESSENTIAL_MESSAGE.getString("essential.heal.self.success"));
			}

			return;
		}

		// other heal
		if (!sender.hasPermission("mzp.essential.heal.other")) {
			CompatibleMessage.sendMessage(sender, Main.ESSENTIAL_MESSAGE.getString("essential.prefix") + minecraft.core.zocker.pro.Main.CORE_MESSAGE.getString("message.permission.deny"));
			return;
		}

		Player target = Bukkit.getPlayer(args[0]);
		if (target == null || !target.isOnline()) {
			if (sender instanceof Player) {
				CompatibleSound.playErrorSound((Player) sender);
			}

			CompatibleMessage.sendMessage(sender, Main.ESSENTIAL_MESSAGE.getString("essential.prefix") + Main.ESSENTIAL_MESSAGE.getString("essential.general.offline").replace("%target%", args[0]));
			return;
		}

		heal(sender, target, false);

		CompatibleMessage.sendMessage(sender, Main.ESSENTIAL_MESSAGE.getString("essential.prefix") + Main.ESSENTIAL_MESSAGE.getString("essential.heal.other.success").replace("%target%", args[0]));
	}

	private void heal(CommandSender sender, Player target, boolean self) {
		final double amount = target.getMaxHealth() - target.getHealth();
		final EntityRegainHealthEvent entityRegainHealthEvent = new EntityRegainHealthEvent(target, amount, EntityRegainHealthEvent.RegainReason.CUSTOM);
		Bukkit.getServer().getPluginManager().callEvent(entityRegainHealthEvent);
		if (entityRegainHealthEvent.isCancelled()) return;

		double newAmount = target.getHealth() + entityRegainHealthEvent.getAmount();
		if (newAmount > target.getMaxHealth()) {
			newAmount = target.getMaxHealth();
		}

		target.setHealth(newAmount);

		if (Main.ESSENTIAL_CONFIG.getBool("essential.heal.fire")) {
			target.setFireTicks(0);
		}

		if (Main.ESSENTIAL_CONFIG.getBool("essential.heal.food")) {
			target.setFoodLevel(20);
		}

		if (Main.ESSENTIAL_CONFIG.getBool("essential.heal.potion")) {
			new BukkitRunnable() {
				@Override
				public void run() {
					for (final PotionEffect effect : target.getActivePotionEffects()) {
						target.removePotionEffect(effect.getType());
					}
				}
			}.runTask(Main.getPlugin());
		}

		if (Main.ESSENTIAL_CONFIG.getBool("essential.heal.notify") && !self) {
			CompatibleMessage.sendMessage(target, Main.ESSENTIAL_MESSAGE.getString("essential.prefix") + Main.ESSENTIAL_MESSAGE.getString("essential.heal.other.notify").replace("%target%", sender.getName()));
		}
	}
}
