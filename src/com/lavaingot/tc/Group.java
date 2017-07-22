package com.lavaingot.tc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.lavaingot.lavalibs.Lib;

public class Group implements CommandExecutor{
	
	public static HashMap<Player, String> selected_groups = new HashMap<Player, String>();
	
	public Group(Main instance) {}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player player = (Player) sender;
		String playername = player.getName();
		
		if (Lib.checkCmdPermission(cmd, sender, "tc.group", "group")) {
			if (args.length < 2) {
				if (args[0].equals("help")) {
					Lib.sendMsg("Help me Obi Wan Kenobi, you're my only hope...", player, null);
				}
				else {
					return false;
				}
			} else {
				
				if (args[0].equals("make")) {
					if (args.length == 2) {
						List<String> members = new ArrayList<String>();
						members.add(playername);
						List<String> playergroups = Main.players.getStringList(playername + ".groups");
						playergroups.add(args[1]);
						Main.players.set(playername + ".groups", playergroups);
						Main.groups.createSection(args[1]);
						Main.groups.set(args[1] + ".admin", playername);
						Main.groups.set(args[1] + ".members", members);
						
						Main.instance.saveGroups();
						Main.instance.savePlayers();
					}
					else {
						Lib.sendMsg("Group name must be one word", player, null);
					}
				} 
				else if (args[0].equals("select")){
					selected_groups.put(player, args[1]);
				} 
				else if (args[0].equals("add")) {
					List<String> playergroups = Main.players.getStringList(args[1] + ".groups");
					playergroups.add(args[2]);
					Main.players.set(args[1] + ".groups", playergroups);
					
					List<String> players = Main.groups.getStringList(args[2] + ".members");
					Lib.print(players, Main.pluginname);
					players.add(args[1]);
					Main.groups.set(args[2] + ".members", players);
					
					Main.instance.saveGroups();
					Main.instance.savePlayers();
				}				
				else if (args[0].equals("remove")) {
					
					if (args.length > 2) {
						Lib.sendMsg("Group name must be one word", player, null);
					} else {

						List<String> members = Main.groups.getStringList(args[1] + ".members");
						Lib.print(members + ":" + args[1] + ".members", Main.pluginname);
						for (String member : members) {
							List<String> groupsMember = Main.players.getStringList(member + ".groups");
							groupsMember.remove(args[1]);
							Lib.print(groupsMember, Main.pluginname);
							Main.players.set(member + ".groups", groupsMember);
						}
						Main.groups.set(args[1], null);
						
						Lib.print(Main.groups.getKeys(true), Main.pluginname);
						Lib.print(Main.players.getStringList(playername + ".groups"), Main.pluginname);
						Main.instance.saveGroups();
						Main.instance.savePlayers();
					}
					
				}
				
			} 
		}
		
		return true;
	}
}
