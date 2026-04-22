package com.github.aasmus.pvptoggle.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

public class PVPToggleEvent extends Event {

    private final Player player;

    public PVPToggleEvent(Player player) {
        this.player = player;
    }

    private static final HandlerList HANDLERS = new HandlerList();

    public @NonNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public Player getPlayer() {
        return this.player;
    }
}
