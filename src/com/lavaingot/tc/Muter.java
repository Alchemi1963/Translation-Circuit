package com.lavaingot.tc;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.lavaingot.lavalibs.Lib;

public class Muter implements CommandExecutor{
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player player = (Player) sender;
		if (Lib.checkCmdPermission(cmd, sender, "tc.mute", "mute")) {
			if (args.length < 1) {
				return false;
			} else {
				Player master = Bukkit.getPlayer(args[0]);
				master.setCustomName(Lib.cc("&8[muted]") + master.getDisplayName());
				master.setCustomNameVisible(true);
				Main.players.set(master.getName() + ".muted", true);
				Lib.sendMsg(Main.config.getString("mute_message_op"), player, null);
				Lib.sendMsg(Main.config.getString("mute_message"), master, null);
			}
		//unmute
		} else if (Lib.checkCmdPermission(cmd, sender, "tc.unmute", "unmute")) {
			if (args.length < 1) {
				return false;
			} else {
				Player master = Bukkit.getPlayer(args[0]);
				String dispname = master.getDisplayName();
				dispname = dispname.replaceFirst("&8[muted]", "");
				master.setCustomName(Lib.cc(dispname));
				Main.players.set(master.getName() + ".muted", false);
				Lib.sendMsg(Main.config.getString("unmute_message_op"), player, null);
				Lib.sendMsg(Main.config.getString("unmute_message"), master, null);
			} 
		}
		return true;
	}

}
