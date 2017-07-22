package com.lavaingot.tc;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.lavaingot.lavalibs.Lib;
import com.lavaingot.tc.Main;

public class PrivateChat implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player player = (Player) sender;
		String playername = player.getName();
		
		//send
		if (Lib.checkCmdPermission(cmd, sender, "tc.privatechat", "send")){
			if (args.length > 1) {
			Player reciever = Bukkit.getPlayer(args[0]);
			
			String msg = new String();
			for (String arg : args) {
				if (!arg.equals(args[0])) {
					if (arg.equals(args[1])) {
						msg = msg + arg;
					}
					else {
						msg = msg + ' ' + arg;
					}
				}
			}
			
			msg = "&o<$sender$>--> &r&o" + msg;
			msg = msg.replaceFirst("<", Main.players.getString(playername + ".prefix"));
			msg = msg.replaceFirst(">", Main.players.getString(playername + ".suffix"));
			
			Lib.sendMsg(msg, reciever, player);
			Main.players.set(playername + ".lastchat", reciever.getName());
			}
			else if (args.length == 1) {
				Lib.sendMsg(Main.config.getString("no_message"), player, null);
			} 
			else {
				return false;
			}
		}
		else if  (Lib.checkCmdPermission(cmd, sender, "tc.privatechat.reply", "reply")){
			if (args.length > 1) {
				Player reciever = Bukkit.getPlayer(Main.players.getString(playername + ".lastchat"));
				String msg = new String();
				for (String arg : args) {
					if (arg.equals(args[0])) {
						msg = msg + arg;
					}
					else {
						msg = msg + ' ' + arg;
					}
				}
				
				msg = "&o<&o$sender$>--> &r&o" + msg;
				msg = msg.replaceFirst("<", Main.players.getString(playername + ".prefix"));
				msg = msg.replaceFirst(">", Main.players.getString(playername + ".suffix"));
				
				Lib.sendMsg(msg, reciever, player);
			}
			else {
				return false;
			}
			
		}
		
		return true;
	}
}
	
