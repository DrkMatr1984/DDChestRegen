package com.modcrafting.ddchestregen;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import com.modcrafting.diablodrops.events.RuinGenerateEvent;

public class DDChestListener implements Listener {
	DDChestRegen plugin;
	public DDChestListener(DDChestRegen ddChestRegen) {
		this.plugin=ddChestRegen;
	}
	@EventHandler
	public void onRuinGenerate(RuinGenerateEvent event){
		if(!plugin.blocks.containsKey(event.getChest().getLocation())){
			plugin.blocks.put(event.getChest(),new ArrayList<String>());
		}
	}
	/**
	 * ddchestregen.reopen
	 * ddchestregen.open
	 * @param event
	 */
	@EventHandler
	public void onChestOpen(final PlayerInteractEvent event){
		if(event.getAction()==Action.RIGHT_CLICK_BLOCK){
			final Block blck = event.getClickedBlock();
			if(!(blck.getState() instanceof Chest))
				return;
			if(plugin.blocks.containsKey(blck)){
				if(!event.getPlayer().hasPermission("ddchestregen.open")){
					event.getPlayer().sendMessage(ChatColor.RED+"This chest has been locked.");
					event.setCancelled(true);
				}
				List<String> names = plugin.blocks.get(blck);
				if(names.contains(event.getPlayer().getName())
						&&!event.getPlayer().hasPermission("ddchestregen.reopen")){
					event.setCancelled(true);
					event.getPlayer().sendMessage(ChatColor.RED+"This chest has been locked.");
				}else{
					if(blck.getState() instanceof Chest){
						Chest chest = (Chest) blck.getState();
						chest.getBlockInventory().clear();
						if(plugin.getConfig().getBoolean("CustomItemsOnly",false)){
							for(int i=0;i<plugin.getConfig().getInt("CustomItemsRandAmt",3);i++){
								chest.getBlockInventory().addItem(plugin.getDiabloDrops().custom.get(plugin
								.getDiabloDrops()
								.getSingleRandom()
								.nextInt(plugin.getDiabloDrops().custom.size())));
							}
						}else{
							plugin.dd.getDropAPI().fillChest(blck);
						}
						plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
							@Override
							public void run() {
								plugin.db.add(blck.getLocation(), event.getPlayer().getName());
							}
						});
						names.add(event.getPlayer().getName());
						plugin.blocks.put(blck, names);
					}
				}
			}
		}
	}
	/**
	 * ddchestregen.break
	 * @param event
	 */
	@EventHandler
	public void onBlockDamage(BlockDamageEvent event){
		Block blck = event.getBlock();
		//TODO: Permissions
		if(event.getPlayer().hasPermission("ddchestregen.break"))
			return;
		if(plugin.blocks.containsKey(blck)){
			if(blck.getState() instanceof Chest){
				event.setCancelled(true);
			}
		}
	}
	/**
	 * ddchestregen.break
	 * @param event
	 */
	@EventHandler
	public void onBlockBreak(final BlockBreakEvent event){
		final Block blck = event.getBlock();
		if(plugin.blocks.containsKey(blck)){
			if(event.getPlayer().hasPermission("ddchestregen.break")){
				if(blck.getState() instanceof Chest){
					Chest chest = (Chest) blck.getState();
					chest.getBlockInventory().clear();
					plugin.blocks.remove(blck);
					plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
						@Override
						public void run() {
							plugin.db.remove(blck.getLocation());
						}
					});
				}
				return;
			}else{
				event.setCancelled(true);
			}
		}
	}
}
