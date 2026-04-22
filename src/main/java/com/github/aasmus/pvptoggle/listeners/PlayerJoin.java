package com.github.aasmus.pvptoggle.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.github.aasmus.pvptoggle.PvPToggle;
import com.github.aasmus.pvptoggle.utils.Util;

import java.util.ArrayList;
import java.util.Objects;

public class PlayerJoin implements Listener {
	
	public PlayerJoin() {
		for (Player player : new ArrayList<>(Bukkit.getOnlinePlayers())) {
			if (!PvPToggle.instance.getConfig().getBoolean("SETTINGS.PERSISTENT_PVP_STATE")) {
				PvPToggle.instance.players.put(player.getUniqueId(), PvPToggle.instance.getConfig().getBoolean("SETTINGS.DEFAULT_PVP_OFF")); //add player to players hash map and set their pvp state
            } else {
				PvPToggle.instance.dataUtils.addPlayer(Objects.requireNonNull(player.getPlayer()));
				PvPToggle.instance.players.put(player.getUniqueId(), PvPToggle.instance.dataUtils.GetPlayerPvPState(player.getPlayer())); //add player to players hash map and set their pvp state
            }

            if (PvPToggle.instance.players.get(player.getUniqueId()) == false) {
                if (PvPToggle.instance.getConfig().getBoolean("SETTINGS.PARTICLES")) {
                    Util.particleEffect(player.getPlayer());	
                }
            }
        }
	}
	
	@EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
    	Player p = event.getPlayer();
    	if (!PvPToggle.instance.getConfig().getBoolean("SETTINGS.PERSISTENT_PVP_STATE")) {
        	PvPToggle.instance.players.put(p.getUniqueId(), PvPToggle.instance.getConfig().getBoolean("SETTINGS.DEFAULT_PVP_OFF")); //add player to players hash map and set their pvp state
        } else {
			PvPToggle.instance.dataUtils.addPlayer(Objects.requireNonNull(p.getPlayer()));
			PvPToggle.instance.players.put(p.getUniqueId(), PvPToggle.instance.dataUtils.GetPlayerPvPState(p.getPlayer())); //add player to players hash map and set their pvp state
        }

        if (PvPToggle.instance.players.get(p.getUniqueId()) == false) {
            if (PvPToggle.instance.getConfig().getBoolean("SETTINGS.PARTICLES")) {
                Util.particleEffect(p.getPlayer());	
            }
        }
    }
}
