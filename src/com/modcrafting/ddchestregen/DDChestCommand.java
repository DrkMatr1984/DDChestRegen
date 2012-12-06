package com.modcrafting.ddchestregen;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DDChestCommand implements CommandExecutor{
	DDChestRegen plugin;
	public DDChestCommand(DDChestRegen plugin){
		this.plugin=plugin;
	}

    @Override
    public boolean onCommand(CommandSender sender, Command command,
            String commandLabel, String[] args)
    {
        if (!(sender instanceof Player)
                || !sender.hasPermission(command.getPermission()))
        {
            sender.sendMessage(ChatColor.RED + "You cannot run this command.");
            return true;
        }
        if(args.length<1) return false;
        Player player = (Player) sender;
        if(args[0].equalsIgnoreCase("add")){
            Block block = player.getTargetBlock(null, 7);
            if(block.getState() instanceof Chest&&!plugin.blocks.containsKey(block)){
            	plugin.blocks.put(block, new ArrayList<String>());
                sender.sendMessage(ChatColor.GREEN + "Chest is set to regen.");
            }else{
                sender.sendMessage(ChatColor.RED + "Unable to set to regen");
            	
            }
        }
        if(args[0].equalsIgnoreCase("remove")){
            final Block block = player.getTargetBlock(null, 7);
            if(plugin.blocks.containsKey(block)){
            	plugin.blocks.remove(block);
				plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable(){
					@Override
					public void run() {
						plugin.db.remove(block.getLocation());
					}
				});
                sender.sendMessage(ChatColor.GREEN + "Chest is set to not regen.");
            }else{
                sender.sendMessage(ChatColor.RED + "Unable to set to regen");
            	
            }
        	
        }
		return true;
	}

}
