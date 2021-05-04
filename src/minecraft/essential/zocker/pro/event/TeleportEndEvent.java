package minecraft.essential.zocker.pro.event;

import minecraft.essential.zocker.pro.util.Teleporter;
import minecraft.essential.zocker.pro.util.TeleporterCause;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TeleportEndEvent extends Event {
	private static final HandlerList HANDLER_LIST = new HandlerList();

	private final Teleporter teleporter;
	private final TeleporterCause cause;

	public TeleportEndEvent(Teleporter teleporter, TeleporterCause cause, boolean async) {
		super(async);
		this.teleporter = teleporter;
		this.cause = cause;
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

	public TeleporterCause getCause() {
		return cause;
	}
}
