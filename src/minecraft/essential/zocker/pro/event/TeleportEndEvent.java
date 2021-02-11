package minecraft.essential.zocker.pro.event;

import minecraft.essential.zocker.pro.util.Teleporter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TeleportEndEvent extends Event {
	private static final HandlerList HANDLER_LIST = new HandlerList();

	private final Teleporter teleporter;

	public TeleportEndEvent(Teleporter teleporter, boolean async) {
		super(async);
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
	}}
