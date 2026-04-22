package com.github.aasmus.pvptoggle;
import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import com.github.aasmus.pvptoggle.listeners.PlayerJoin;
import com.github.aasmus.pvptoggle.listeners.PlayerLeave;
import com.github.aasmus.pvptoggle.listeners.PvP;
import com.github.aasmus.pvptoggle.listeners.PlayerChangeWorld;
import com.github.aasmus.pvptoggle.utils.PersistentData;
import com.github.aasmus.pvptoggle.utils.PlaceholderAPIHook;
public class PvPToggle extends JavaPlugin implements Listener {

	public static List<String> blockedWorlds;
	public static PvPToggle instance;

	public FileConfiguration config;

	public Map<UUID, Boolean> players = new ConcurrentHashMap<>(); // False is PvP on True is PvP off
	public Map<UUID, Date> cooldowns = new ConcurrentHashMap<>();

	public PersistentData dataUtils;

	@Override
	public void onEnable() {
		instance = this;
		this.config = getConfig();
		// Save config
        this.saveDefaultConfig();
        File PVPData = new File(getDataFolder(), "Data");
		dataUtils = new PersistentData(PVPData);

		// Register events
		getServer().getPluginManager().registerEvents(this, this);
		Bukkit.getPluginManager().registerEvents(new PlayerJoin(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerLeave(), this);
		Bukkit.getPluginManager().registerEvents(new PvP(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerChangeWorld(), this);
		// Register command
		Objects.requireNonNull(this.getCommand("pvp")).setExecutor(new PvPCommand());

		blockedWorlds = config.getStringList("SETTINGS.BLOCKED_WORLDS");

		if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
			new PlaceholderAPIHook(this).register();
		}		
	}
}
