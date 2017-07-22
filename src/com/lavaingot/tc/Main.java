package com.lavaingot.tc;
import java.io.File;
import java.io.IOException;

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
import com.lavaingot.lavalibs.Lib;
import com.lavaingot.tc.Group;

public class Main extends JavaPlugin implements Listener{
	String[] badder;
	public File playersfile = new File(getDataFolder(), "players.yml");
	public static FileConfiguration players;
	public File groupsfile = new File(getDataFolder() + "/groups", "groups.yml");
	public static FileConfiguration groups;
	public static FileConfiguration config;
	public static Main instance = null;
	
	@Override
	public void onEnable() {
		instance = this;
		getLogger().info(ChatColor.GOLD + "Hello World!");
		getServer().getPluginManager().registerEvents(this, this);
		
		//resources init
		config = getConfig();
		getConfig().addDefault("MotDNewplayer", true);
		File conf = new File(getDataFolder(), "config.yml");
		checkFileExists("config.yml", conf);
		checkFileExists("players.yml", playersfile);
		checkFileExists("groups/groups.yml", groupsfile);
		
		String bad = config.getString("curse_words");
		badder = bad.split(", ");
		int x = 0;
		for (String badword : badder) {
			badword = ' ' + badword + ' ';
			badder[x] = badword;
			x += 1;
		}
		
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
		getLogger().info("Goodbye World!");
		savePlayers();
	}

	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		String message = event.getMessage();
		String format = event.getFormat();
		Lib.print(format);
		
		for (String curse : badder) {
			while (message.contains(curse)) {
				String replacement = new String();
				char[] curses = curse.toCharArray();
				for (char letter : curses) {
					replacement += '*';
					letter = '*';
					curses[replacement.length()-1] = letter;
				}
				message = message.replaceAll(curse, replacement);
			}
		}
		if (!event.getMessage().equals(message)) {
			Lib.sendMsg(getConfig().getString("curse_message"), player, null);
		}
		
		if (player.hasPermission("tc.colorchat")) {
			message = Lib.cc(message);
		}
		
		if (getPlayers().getBoolean(player.getName() + ".muted")) {
			event.setCancelled(true);
			Lib.sendMsg(getConfig().getString("mute_message"), player, null);
		} else {
			String newFormat = format.replaceFirst("<", Lib.cc(getPlayers().getString(player.getName() + ".prefix")));
			newFormat = newFormat.replaceFirst(">", Lib.cc(getPlayers().getString(player.getName() + ".suffix") + "&r"));
			event.setFormat(newFormat);
			event.setMessage(message);
		}
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
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
	}
	
	@EventHandler
	public void onPing(ServerListPingEvent e) {
		System.out.print(getConfig().getBoolean("MotDNewplayer"));
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
		getCommand("send").setExecutor(new PrivateChat());
		getCommand("reply").setExecutor(new PrivateChat());
		getCommand("mute").setExecutor(new Muter());
		getCommand("unmute").setExecutor(new Muter());
		getCommand("group").setExecutor(new Group(this));
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
			
			Lib.print(filename + " not found, creating!");
			if (filename.equals("config.yml")) {
				saveDefaultConfig();
			}
			else {
				saveResource(filename, true);
			}
			
			
		} else {
			
		    getLogger().info(filename + " found, loading!");
		    System.out.println(getResource(filename));
		}
	}
	
	public void saveGroups() {
		try {
			players.save(groupsfile);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}
