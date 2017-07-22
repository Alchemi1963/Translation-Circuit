package com.lavaingot.tc;

import java.io.IOException;
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
						String[] members = {playername};
						List<String> playergroups = Main.players.getStringList(playername + ".groups");
						playergroups.add(args[1]);
						Main.players.set(playername + ".groups", playergroups);
						Main.groups.createSection(args[1]);
						Main.groups.set(args[1] + ".admin", playername);
						Main.groups.set(args[1] + ".members", members);
						
						try { 
							Main.groups.save(Main.instance.groupsfile);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					else {
						Lib.sendMsg("Group name must be one word", player, null);
					}
				} 
				else if (args[0].equals("select")){
					selected_groups.put(player, args[1]);
				} 
				else if (args[0].equals("remove")) {
					
					if (args.length > 2) {
						Lib.sendMsg("Group name must be one word", player, null);
					} else {

						List<String> members = Main.groups.getStringList(args[1]);
						for (String member : members) {
							List<String> groupsMember = Main.players.getStringList(member + ".groups");
							groupsMember.remove(args[1]);
							Main.players.set(member + ".groups", groupsMember);
						}
						Main.groups.set(args[1], null);
					}
					
				}
				
			} 
		}
		
		return true;
	}
}
