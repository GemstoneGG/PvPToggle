package com.github.aasmus.pvptoggle;

import com.github.aasmus.pvptoggle.events.PvPToggleEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.github.aasmus.pvptoggle.utils.Chat;
import com.github.aasmus.pvptoggle.utils.Util;
import org.jspecify.annotations.NonNull;

public class PvPCommand implements CommandExecutor {

	@Override
	public boolean onCommand(@NonNull CommandSender sender, @NonNull Command cmd, @NonNull String label, String @NonNull [] args) {
		if (sender instanceof ConsoleCommandSender console) { //check if command sender is console
            if (args.length == 0) {
				Chat.send(console, "HELP_HEADER");
				Chat.send(console, "HELP_SET_OTHERS");
			} else {
				try {
					Player other = Bukkit.getPlayerExact(args[1]);
					if (other == null) { //make sure the player is online
					    Chat.send(console, "NO_PLAYER", args[1]);
					} else { //set pvp state
						Boolean current = PvPToggle.instance.players.get(other.getUniqueId());
						if (args[0].equals("reload")) {
							reloadConfig();
							return true;
						} else if (args[0].equals("toggle")) {
							if (current == true) {
							    if (Util.setPlayerState(other, false, console)) {
									Bukkit.getPluginManager().callEvent(new PvPToggleEvent(other));
                                    Chat.send(other, "PVP_STATE_ENABLED");
                                    if (PvPToggle.instance.getConfig().getBoolean("SETTINGS.PARTICLES")) {
                                        Util.particleEffect(other.getPlayer());
                                    }
                                }
							} else {
							    if (Util.setPlayerState(other, true, console)) {
									Bukkit.getPluginManager().callEvent(new PvPToggleEvent(other));
                                    Chat.send(other, "PVP_STATE_DISABLED");
                                }
							}
						} else if (args[0].equalsIgnoreCase("on")) {
						    if (Util.setPlayerState(other, false, console)) {
								Bukkit.getPluginManager().callEvent(new PvPToggleEvent(other));
                                Chat.send(other, "PVP_STATE_ENABLED");
                                if (current == true) {
                                	if (PvPToggle.instance.getConfig().getBoolean("SETTINGS.PARTICLES")) {
                                        Util.particleEffect(other.getPlayer());
                                	}
                                }
                            }
						} else if (args[0].equalsIgnoreCase("off")) {
						    if (Util.setPlayerState(other, true, console)) {
								Bukkit.getPluginManager().callEvent(new PvPToggleEvent(other));
                                Chat.send(other, "PVP_STATE_DISABLED");
                            }
						}
						current = PvPToggle.instance.players.get(other.getUniqueId());
						Chat.send(console, "PVP_STATE_CHANGED_OTHERS", other.getDisplayName(), current);
					}		
				} catch (Exception e) {
					//nothing needs to be done
				}
			}
		} else if (sender instanceof Player player) { //check if command sender is player
			if (cmd.getName().equalsIgnoreCase("pvp")) {
                if (args.length == 0) {
					Chat.send(player, "PVP_STATUS", null, PvPToggle.instance.players.get(player.getUniqueId()));
					Chat.send(player, "HELP_HEADER");
					Chat.send(player, "HELP_GENERAL_USEAGE");
					if (player.hasPermission("pvptoggle.others"))
						Chat.send(player, "HELP_VIEW_OTHERS");
					if (player.hasPermission("pvptoggle.others.set"))
						Chat.send(player, "HELP_SET_OTHERS");
				} else if (args.length == 1) {
					if (args[0].equals("reload") && player.hasPermission("pvptoggle.reload")) {
						reloadConfig();
						return true;
					}

					if (player.hasPermission("pvptoggle.allow")) {
						if (!Util.getCooldown(player) || player.hasPermission("pvptoggle.bypass")) {
							Boolean current = PvPToggle.instance.players.get(player.getUniqueId());
							if (args[0].equals("toggle")) {
								if (current == true) {
									Util.setCooldownTime(player);
									if (Util.setPlayerState(player, false, player)) {
										Bukkit.getPluginManager().callEvent(new PvPToggleEvent(player));
                                        Chat.send(player, "PVP_STATE_ENABLED");
                                        if (PvPToggle.instance.getConfig().getBoolean("SETTINGS.PARTICLES")) {
                                            Util.particleEffect(player.getPlayer());
                                        }
                                    }
								} else {
								    if (Util.setPlayerState(player, true, player)) {
										Bukkit.getPluginManager().callEvent(new PvPToggleEvent(player));
                                        Chat.send(player, "PVP_STATE_DISABLED");
                                    }
								}
							} else if (args[0].equalsIgnoreCase("on")) {
								Util.setCooldownTime(player);
								if (Util.setPlayerState(player, false, player)) {
									Bukkit.getPluginManager().callEvent(new PvPToggleEvent(player));
                                    Chat.send(player, "PVP_STATE_ENABLED");
                                    if (current == true) {
                                    	if (PvPToggle.instance.getConfig().getBoolean("SETTINGS.PARTICLES")) {
                                            Util.particleEffect(player.getPlayer());	
                                    	}
                                    }
                                }
							} else if (args[0].equalsIgnoreCase("off")) {
							    if (Util.setPlayerState(player, true, player)) {
									Bukkit.getPluginManager().callEvent(new PvPToggleEvent(player));
                                    Chat.send(player, "PVP_STATE_DISABLED");
                                }
							} else if (args[0].equalsIgnoreCase("status")) {
								Chat.send(player, "PVP_STATUS", null, current);
							} else {
								if (sender.hasPermission("pvptoggle.others")) {
									Player other = Bukkit.getPlayerExact(args[0]);
									if (other == null) {
										Chat.send(player, "NO_PLAYER", args[0]);	
									} else {
										current = PvPToggle.instance.players.get(other.getUniqueId());
										Chat.send(player, "PVP_STATUS_OTHERS", other.getDisplayName(), current);
									}
								} else {
									if (!args[0].contains("\\")) {
										Chat.send(player, "COMMAND_INVALID_PARAMETER", args[0]);	
									}
								}
							}
						}
					}
				} else if (args.length == 2) {
					if (sender.hasPermission("pvptoggle.others.set")) {
						Player other = Bukkit.getPlayerExact(args[1]);
						if (other == null) {
							Chat.send(player, "NO_PLAYER", args[1]);
						} else {
							Boolean current = PvPToggle.instance.players.get(other.getUniqueId());
							if (args[0].equals("toggle")) {
								if (current == true) {
								    if (Util.setPlayerState(other, false, sender)) {
										Bukkit.getPluginManager().callEvent(new PvPToggleEvent(other));
                                        Chat.send(other, "PVP_STATE_ENABLED");
                                        if (PvPToggle.instance.getConfig().getBoolean("SETTINGS.PARTICLES")) {
                                            Util.particleEffect(other.getPlayer());
                                        }
                                    }
								} else {
								    if (Util.setPlayerState(other, true, sender)) {
										Bukkit.getPluginManager().callEvent(new PvPToggleEvent(other));
                                        Chat.send(other, "PVP_STATE_DISABLED");
                                    }
								}
							} else if (args[0].equalsIgnoreCase("on")) {
							    if (Util.setPlayerState(other, false, sender)) {
									Bukkit.getPluginManager().callEvent(new PvPToggleEvent(other));
                                    if (current == true) {
                                    	if (PvPToggle.instance.getConfig().getBoolean("SETTINGS.PARTICLES")) {
                                            Util.particleEffect(other.getPlayer());	
                                    	}
                                    }
                                    Chat.send(other, "PVP_STATE_ENABLED");
                                }
							} else if (args[0].equalsIgnoreCase("off")) {
							    if (Util.setPlayerState(other, true, sender)) {
									Bukkit.getPluginManager().callEvent(new PvPToggleEvent(other));
                                    Chat.send(other, "PVP_STATE_DISABLED");
                                }
							}
							current = PvPToggle.instance.players.get(other.getUniqueId());
							Chat.send(player, "PVP_STATE_CHANGED_OTHERS", other.getDisplayName(), current);
						}
					} else {
						Chat.send(player, "COMMAND_NO_PERMISSION");
					}
				}
			}
			return true;
		}
		return false;
	}
	
    public void reloadConfig() {
    	PvPToggle.instance.reloadConfig();
    }
}
