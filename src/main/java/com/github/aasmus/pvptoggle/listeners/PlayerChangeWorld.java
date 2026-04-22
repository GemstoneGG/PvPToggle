package com.github.aasmus.pvptoggle.listeners;
import com.github.aasmus.pvptoggle.PvPToggle;
import com.github.aasmus.pvptoggle.events.PvPToggleEvent;
import com.github.aasmus.pvptoggle.utils.Chat;
import com.github.aasmus.pvptoggle.utils.Util;
import io.canvasmc.canvas.event.EntityPostPortalAsyncEvent;
import io.canvasmc.canvas.event.EntityPostTeleportAsyncEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameRules;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
public class PlayerChangeWorld implements Listener {

    @EventHandler
    public void onPostTeleport(EntityPostTeleportAsyncEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        World to = event.getTo().getWorld();
        // Only track teleporting that actually change worlds
        if (event.getFrom().getWorld() == to) return;
        reconcilePvpState(player, to);
    }

    @EventHandler
    public void onPostPortal(EntityPostPortalAsyncEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        // Portal always changes worlds, but check defensively in case of custom portals
        if (event.getFrom() == event.getTo()) return;
        reconcilePvpState(player, event.getTo());
    }

    private void reconcilePvpState(Player player, World world) {
        boolean playerPvpEnabled = !Util.getPlayerState(player.getUniqueId());
        // If PVP isn't enabled in the world but the player has it enabled, disable it.
        if (Boolean.FALSE.equals(world.getGameRuleValue(GameRules.PVP)) && playerPvpEnabled) {
            Util.setPlayerState(player.getUniqueId(), true);
            Bukkit.getPluginManager().callEvent(new PvPToggleEvent(player));
            Chat.send(player, "PVP_WORLD_CHANGE_DISABLED");
            return;
        }

        // If PVP is required (i.e., the world has PVP enabled, and it is in the blocked worlds) and the player has it disabled, enable it.
        if (Boolean.TRUE.equals(world.getGameRuleValue(GameRules.PVP)) && PvPToggle.blockedWorlds.contains(world.getName()) && !playerPvpEnabled) {
            Util.setPlayerState(player.getUniqueId(), false);
            Bukkit.getPluginManager().callEvent(new PvPToggleEvent(player));
            Chat.send(player, "PVP_WORLD_CHANGE_REQUIRED");
            if (PvPToggle.instance.getConfig().getBoolean("SETTINGS.PARTICLES")) {
                Util.particleEffect(player);
            }
        }
    }
}
