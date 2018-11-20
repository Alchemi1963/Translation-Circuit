package com.alchemi.tc;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.alchemi.al.Library;

public class Muter implements CommandExecutor{
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player player = (Player) sender;
		if (Library.checkCmdPermission(cmd, sender, "tc.mute", "tc mute")) {
			if (args.length < 1) {
				return false;
			} else {
				Player master = Bukkit.getPlayer(args[0]);
				master.setCustomName(Library.cc("&8[muted]") + master.getDisplayName());
				master.setCustomNameVisible(true);
				Main.players.set(master.getName() + ".muted", true);
				Library.sendMsg(Main.config.getString("mute_message_op"), player, null);
				Library.sendMsg(Main.config.getString("mute_message"), master, null);
			}
		//unmute
		} else if (Library.checkCmdPermission(cmd, sender, "tc.unmute", "tc unmute")) {
			if (args.length < 1) {
				return false;
				
			} else {
				Player master = Bukkit.getPlayer(args[0]);
				String dispname = master.getDisplayName();
				dispname = dispname.replaceFirst("&8[muted]", "");
				master.setCustomName(Library.cc(dispname));
				Main.players.set(master.getName() + ".muted", false);
				Library.sendMsg(Main.config.getString("unmute_message_op"), player, null);
				Library.sendMsg(Main.config.getString("unmute_message"), master, null);
			} 
		}
		return true;
	}

}
