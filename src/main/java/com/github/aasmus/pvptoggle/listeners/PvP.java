package com.github.aasmus.pvptoggle.listeners;

import java.util.Collection;
import java.util.Iterator;

import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.AreaEffectCloudApplyEvent;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.weather.LightningStrikeEvent;
import org.bukkit.metadata.FixedMetadataValue;

import com.github.aasmus.pvptoggle.PvPToggle;
import com.github.aasmus.pvptoggle.utils.Chat;
import com.github.aasmus.pvptoggle.utils.Util;


public class PvP implements Listener {

	@EventHandler(ignoreCancelled = true)
	//fired when an entity is hit
	public void onHit(EntityDamageByEntityEvent event) {
		if (PvPToggle.blockedWorlds.contains(event.getEntity().getWorld().getName())) {
			return;
		}
		
		//check if attack was a player
		if (event.getDamager() instanceof Player damager && event.getEntity() instanceof Player attacked) {
            //player who hit
            Boolean damagerState = PvPToggle.instance.players.get(damager.getUniqueId());
            //player who was hit
            Boolean attackedState = PvPToggle.instance.players.get(attacked.getUniqueId());
			if (damagerState) { 
				event.setCancelled(true);
				Chat.send(damager, "PVP_DISABLED");
			} else if (attackedState != null && attackedState) {
				event.setCancelled(true);
				Chat.send(damager, "PVP_DISABLED_OTHERS", attacked.getDisplayName());
			} else {
				Util.setCooldownTime(damager);
				Util.setCooldownTime(attacked);
			}
		//checks if damage was done by a projectile
		} else if (event.getDamager() instanceof Projectile arrow) {
            if (arrow.getShooter() instanceof Player damager) {
				if (event.getEntity() instanceof Player attacked) {
                    Boolean damagerState = PvPToggle.instance.players.get(damager.getUniqueId());
                    Boolean attackedState = PvPToggle.instance.players.get(attacked.getUniqueId());
					if (damager == attacked) {
						return;
					}

					if (damagerState) {
						event.setCancelled(true);
						Chat.send(damager, "PVP_DISABLED");
					} else if (attackedState != null && attackedState) {
						event.setCancelled(true);
						Chat.send(damager, "PVP_DISABLED_OTHERS", attacked.getDisplayName());
					} else {
						Util.setCooldownTime(damager);
						Util.setCooldownTime(attacked);
					}
				}
			}
		//checks if damage was done by a potion
		} else if (event.getDamager() instanceof ThrownPotion potion) {
            if (potion.getShooter() instanceof Player damager && event.getEntity() instanceof Player attacked) {
                Boolean damagerState = PvPToggle.instance.players.get(damager.getUniqueId());
                Boolean attackedState = PvPToggle.instance.players.get(attacked.getUniqueId());
				if (damager == attacked) {
					return;
				}

				if (damagerState) {
					event.setCancelled(true);
					Chat.send(damager, "PVP_DISABLED");
				} else if (attackedState != null && attackedState) {
					event.setCancelled(true);
					Chat.send(damager, "PVP_DISABLED_OTHERS", attacked.getDisplayName());
				} else {
					Util.setCooldownTime(damager);
					Util.setCooldownTime(attacked);
				}
			}
		} else if (event.getDamager() instanceof LightningStrike && !event.getDamager().getMetadata("TRIDENT").isEmpty() && event.getEntity() instanceof Player attacked) {
            Boolean attackedState = PvPToggle.instance.players.get(attacked.getUniqueId());
			if (attackedState != null && attackedState) {
				event.setCancelled(true);
			}
		} else if (event.getDamager() instanceof Firework && event.getEntity() instanceof Player attacked) {
            Boolean attackedState = PvPToggle.instance.players.get(attacked.getUniqueId());
			if (attackedState != null && attackedState) {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	//fired when a player is shot with a flaming arrow
	public void onFlameArrow(EntityCombustByEntityEvent event) {
		if (PvPToggle.blockedWorlds.contains(event.getEntity().getWorld().getName())) {
			return;
		}
		
		if (event.getCombuster() instanceof Arrow arrow) {
            if (arrow.getShooter() instanceof Player damager && event.getEntity() instanceof Player attacked) {
                Boolean damagerState = PvPToggle.instance.players.get(damager.getUniqueId());
                Boolean attackedState = PvPToggle.instance.players.get(attacked.getUniqueId());
				if (damagerState) {
					event.setCancelled(true);
				} else if (attackedState != null && attackedState) {
					event.setCancelled(true);
				} else {
					Util.setCooldownTime(damager);
					Util.setCooldownTime(attacked);
				}
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	//fired when a splash potion is thrown
	public void onPotionSplash(PotionSplashEvent event) {
		if (PvPToggle.blockedWorlds.contains(event.getEntity().getWorld().getName())) {
			return;
		}
		
		if (event.getPotion().getShooter() instanceof Player damager) {
			   for (LivingEntity entity : event.getAffectedEntities()) {
			        if (entity instanceof Player attacked) {
                        Boolean damagerState = PvPToggle.instance.players.get(damager.getUniqueId());
                        Boolean attackedState = PvPToggle.instance.players.get(attacked.getUniqueId());
						if (damager != attacked) {
				    		if (damagerState) {
				    		    Collection<LivingEntity> affected = event.getAffectedEntities();
				    		    for (LivingEntity ent : affected) {
				    		        if (ent instanceof Player && ent != damager) {
				    		            event.setIntensity(ent, 0);
				    		        }
				    		    }
				    			Chat.send(damager, "PVP_DISABLED");
				    		} else if (attackedState != null && attackedState) {
				    		    Collection<LivingEntity> affected = event.getAffectedEntities();
				    		    for (LivingEntity ent : affected) {
				    		        if (ent instanceof Player && ent != damager) {
				    		            event.setIntensity(ent, 0);
				    		        }
				    		    }
				    			Chat.send(damager, "PVP_DISABLED_OTHERS", attacked.getDisplayName());
				    		} else {
								Util.setCooldownTime(damager);
								Util.setCooldownTime(attacked);
							}
						}
			        }
			   }
		}
	}
	
    @EventHandler(ignoreCancelled = true)
    //fired when lingering potion cloud is active
    public void onCloudEffects(AreaEffectCloudApplyEvent event) {
		if (PvPToggle.blockedWorlds.contains(event.getEntity().getWorld().getName())) {
			return;
		}
    	
    	if (event.getEntity().getSource() instanceof Player damager) {
    		Iterator<LivingEntity> it = event.getAffectedEntities().iterator();
        	while (it.hasNext()) {
        		LivingEntity entity = it.next();
        		if (entity instanceof Player attacked) {
                    Boolean damagerState = PvPToggle.instance.players.get(damager.getUniqueId());
                    Boolean attackedState = PvPToggle.instance.players.get(attacked.getUniqueId());
    	    		if (attackedState != null && attackedState) {
    	    			it.remove();	
    	    		} else if (damagerState) {
    	    			it.remove();	
    	    		} else {
    	    			Util.setCooldownTime(damager);
    	    			Util.setCooldownTime(attacked);
    	    		}
        		}
        	}
    	}
    }
    
    @EventHandler(ignoreCancelled = true)
    //fired when a player uses a fishing rod
    public void onPlayerFishing (PlayerFishEvent event) {
		if (PvPToggle.blockedWorlds.contains(event.getPlayer().getWorld().getName())) {
			return;
		}
    	
        if (event.getCaught() instanceof Player attacked) {
            final Player damager = event.getPlayer();
            Boolean damagerState = PvPToggle.instance.players.get(damager.getUniqueId());
            Boolean attackedState = PvPToggle.instance.players.get(attacked.getUniqueId());
            if (damager.getInventory().getItemInMainHand().getType() == Material.FISHING_ROD || damager.getInventory().getItemInOffHand().getType() == Material.FISHING_ROD) {
    			if (damagerState) {
    				event.setCancelled(true);
    				Chat.send(damager, "PVP_DISABLED");
    			} else if (attackedState != null && attackedState) {
    				event.setCancelled(true);
    				Chat.send(damager, "PVP_DISABLED_OTHERS", attacked.getDisplayName());
    			} else {
    				Util.setCooldownTime(damager);
    				Util.setCooldownTime(attacked);
    			}
            }
        }
    }
    
    //Tag lightning strike as from a trident
	@EventHandler(ignoreCancelled = true)
	public void onLightningStrike(LightningStrikeEvent event) {
		if (event.getCause() == LightningStrikeEvent.Cause.TRIDENT) {
			event.getLightning().setMetadata("TRIDENT", new FixedMetadataValue(PvPToggle.instance, event.getLightning().getLocation()));
		}
	}
}
