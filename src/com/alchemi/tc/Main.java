package com.alchemi.tc;
import java.io.File;
import java.io.IOException;
import java.sql.Array;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.alchemi.tc.Group;
import com.lavaingot.lavalibs.Lib;

import net.milkbowl.vault.chat.Chat;

public class Main extends JavaPlugin implements Listener{
	public static String pluginname;
	
	
	List<String> curse_words;
	public File playersfile = new File(getDataFolder(), "players.yml");
	public static FileConfiguration players;
	public File groupsfile = new File(getDataFolder() + "/groups", "groups.yml");
	public static FileConfiguration groups;
	public static FileConfiguration config;
	public static boolean VaultPresent = false;
	public static Main instance = null;
	private static Chat chat = null;
	
	@Override
	public void onEnable() {
		
		pluginname = getDescription().getName();
		instance = this;
		Lib.print(ChatColor.GOLD + "Hello World!", pluginname);;
		getServer().getPluginManager().registerEvents(this, this);
		
		//resources init
		config = getConfig();
		getConfig().addDefault("MotDNewplayer", true);
		File conf = new File(getDataFolder(), "config.yml");
		checkFileExists("config.yml", conf);
		checkFileExists("players.yml", playersfile);
		checkFileExists("groups/groups.yml", groupsfile);
		
		String bad = config.getString("curse_words");
		curse_words = Arrays.asList(bad.split(", "));
		
		players = new YamlConfiguration();
		try {
			players.load(playersfile);
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (InvalidConfigurationException ex) {
			ex.printStackTrace();
		}
		
		groups = new YamlConfiguration();
		try {
			groups.load(groupsfile);
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (InvalidConfigurationException ex) {
			ex.printStackTrace();
		}
		
		//register things
		registerCommands();
		
		
	}
	
	@Override
	public void onDisable() {
		instance = null;
		Lib.print("Goodbye World!", pluginname);;
		savePlayers();
	}

	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		String message = event.getMessage();
		String format = event.getFormat();
		
		if (getPlayers().getBoolean(player.getName() + ".muted")) {
			event.setCancelled(true);
			Lib.sendMsg(getConfig().getString("mute_message"), player, null);
		}
		
		message = filter(message, player);
		
		if (event.getMessage().toLowerCase().equals("hello there")) {
			message = message + "\n&7&o[Server] &n&8General &9Kenobi...";
		}
		
		if (player.hasPermission("tc.colorchat")) {
			message = Lib.cc(message);
		}
			
		String newFormat = format.replaceFirst("<", Lib.cc(getPlayers().getString(player.getName() + ".prefix")));
		newFormat = newFormat.replaceFirst(">", Lib.cc(getPlayers().getString(player.getName() + ".suffix") + "&r"));
		event.setFormat(newFormat);
		event.setMessage(message);
		
	}
	
	public String filter(String message, Player player) {
		
		String[] chop_message = message.split(" ");
		String n_message = "";
		for (String chop : chop_message) {
			if (curse_words.contains(chop)) {
				chop = new String(new char[chop.length()]).replace("\0", "*");
			}
			
			if (n_message == "") n_message = chop;
			else n_message = n_message + " " + chop;
		}
		
		if (!n_message.equals(message)) {
			Lib.sendMsg(getConfig().getString("curse_message"), player, null);
		}
		
		return n_message;
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		if (!VaultPresent) {
			Player player = event.getPlayer();
			String playername = player.getName();
			if (!getPlayers().contains(playername)) {
				players.createSection(playername);
				players.set(playername + ".muted", false);
				players.set(playername + ".prefix", getConfig().getString("prefixes.default"));
				players.set(playername + ".suffix", getConfig().getString("suffixes.default"));
				players.set(playername + ".rank", "Default");
				players.set(playername + ".lastchat", null);
				players.createSection(playername + ".groups");
				Lib.sendMsg(getConfig().getString("first_join"), player, null);
				Group.selected_groups.put(player, null);
				savePlayers();
			}
		} else {
			Player player = event.getPlayer();
			String playername = player.getName();
			if (!getPlayers().contains(playername)) {
				players.createSection(playername);
				players.set(playername + ".muted", false);
				players.set(playername + ".prefix", "YO MOMMA");
				players.set(playername + ".suffix", getConfig().getString("suffixes.default"));
				players.set(playername + ".rank", "Default");
				players.set(playername + ".lastchat", null);
				players.createSection(playername + ".groups");
				Lib.sendMsg(getConfig().getString("first_join"), player, null);
				Group.selected_groups.put(player, null);
				savePlayers();
			}
		}
	}
	
	@EventHandler
	public void onPing(ServerListPingEvent e) {
		Lib.print(getConfig().getBoolean("MotDNewplayer"), pluginname);;
		if (getConfig().getBoolean("MotDNewplayer")){
			String newestPlayer = null;
			for (String key : players.getKeys(true)) {
				newestPlayer = key;
			}
			newestPlayer = newestPlayer.replaceAll(".groups", "");
			e.setMotd("Welcome " + newestPlayer + " to " + getServer().getServerName());
		}
		
	}
	
	private void registerCommands() {
		getCommand("tc send").setExecutor(new PrivateChat());
		getCommand("tc reply").setExecutor(new PrivateChat());
		getCommand("tc mute").setExecutor(new Muter());
		getCommand("tc unmute").setExecutor(new Muter());
		getCommand("tc group").setExecutor(new Group(this));
	}
	
	public FileConfiguration getPlayers() {
		return Main.players;
	}
	
	public void savePlayers() {
		try {
			players.save(playersfile);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public void checkFileExists(String filename, File file) {
		if (!file.exists()) {
			
			Lib.print(filename + " not found, creating!", pluginname);
			if (filename.equals("config.yml")) {
				saveDefaultConfig();
			}
			else {
				saveResource(filename, true);
			}
			
		} else {
			
			Lib.print(filename + " found, loading!", pluginname);
			
		}
	}
	
	public void saveGroups() {
		try {
			groups.save(groupsfile);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}
