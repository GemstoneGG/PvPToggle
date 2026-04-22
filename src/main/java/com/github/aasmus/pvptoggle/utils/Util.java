package com.github.aasmus.pvptoggle.utils;
import java.util.Date;
import java.util.UUID;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.github.aasmus.pvptoggle.PvPToggle;
public class Util {

	private static final float radius = .75f;

	public static boolean getPlayerState(UUID uuid) {
		Boolean result = PvPToggle.instance.players.get(uuid);
		if (result == null) {
			return false;
		} else {
			return result;
		}
	}

	public static void setPlayerState(UUID uuid, boolean state) {
		PvPToggle.instance.players.put(uuid,state);
	}

	// Set player state while performing checks to make sure it's a valid switch.
	public static boolean setPlayerState(Player player, boolean state, CommandSender caller) {
		if (player == null) {
			return false;
		}

		World world = player.getWorld();
		// You can't set the state to false (PVP enabled) if the world doesn't allow it
		if (Boolean.FALSE.equals(world.getGameRuleValue(GameRules.PVP)) && !state) {
			if (caller == player) {
				Chat.send(caller, "PVP_WORLD_CANNOT_CHANGE_SELF");
			} else {
				Chat.send(caller, "PVP_WORLD_CANNOT_CHANGE_OTHERS");
			}
			return false;
		}

		// You can't set the state to true (PVP disabled) if the world requires it
		if (Boolean.TRUE.equals(world.getGameRuleValue(GameRules.PVP)) && PvPToggle.blockedWorlds.contains(world.getName()) && state) {
			if (caller == player) {
				Chat.send(caller, "PVP_WORLD_CANNOT_CHANGE_SELF");
			} else {
				Chat.send(caller, "PVP_WORLD_CANNOT_CHANGE_OTHERS");
			}
			return false;
		}

		setPlayerState(player.getUniqueId(), state);
		return true;
	}

	public static void setCooldownTime(Player player) {
		PvPToggle.instance.cooldowns.put(player.getUniqueId(), new Date());
	}

	public static void removeCooldownTime(Player player) {
		PvPToggle.instance.cooldowns.remove(player.getUniqueId());
	}

	public static boolean getCooldown(Player player) {
		if (PvPToggle.instance.cooldowns.containsKey(player.getUniqueId())) {
			Date lastChange = PvPToggle.instance.cooldowns.get(player.getUniqueId());
			Date currentTime = new Date();
			int seconds = (int) (currentTime.getTime() - lastChange.getTime())/1000;
			if (seconds > PvPToggle.instance.getConfig().getInt("SETTINGS.COOLDOWN") || player.hasPermission("pvptoggle.bypass")) {
				Util.removeCooldownTime(player);
				return false;
			} else {
				Chat.send(player, "PVP_COOLDOWN", String.valueOf(PvPToggle.instance.getConfig().getInt("SETTINGS.COOLDOWN") - seconds));
				return true;
			}
		} else {
			return false;
		}
	}

	public static void particleEffect(Player player) {
		player.getScheduler().runAtFixedRate(PvPToggle.instance, task -> {
			if (!player.isOnline() || PvPToggle.instance.players.get(player.getUniqueId()) != false) {
				task.cancel();
			} else if (!player.isDead()) {
				double angle = 0;
				Particle.DustOptions dustOptions = new Particle.DustOptions(Color.RED, 1);
				Location location = player.getLocation();
				for (int i = 0; i < 25; i++) {
					double x = (radius * Math.sin(angle));
					double z = (radius * Math.cos(angle));
					angle += 0.251;
					player.getWorld().spawnParticle(Particle.DUST, location.getX()+x, location.getY(), location.getZ()+z, 0, 0, 1, 0, dustOptions);
				}
			}
		}, null, 1L, 2L);
	}
}
