package minecraft.essential.zocker.pro.event;

import minecraft.essential.zocker.pro.util.Teleporter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TeleportStartEvent extends Event implements Cancellable {

	private static final HandlerList HANDLER_LIST = new HandlerList();

	private final Teleporter teleporter;
	private boolean cancelled;

	public TeleportStartEvent(Teleporter teleporter) {
		super(false);
		this.teleporter = teleporter;
	}

	public HandlerList getHandlers() {
		return HANDLER_LIST;
	}

	public static HandlerList getHandlerList() {
		return HANDLER_LIST;
	}

	public Teleporter getTeleporter() {
		return teleporter;
	}


	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean b) {
		this.cancelled = b;
	}
}
