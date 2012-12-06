package com.modcrafting.ddchestregen;

import java.util.HashMap;
import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class DDChestRegen extends JavaPlugin{
	public HashMap<Block,List<String>> blocks = new HashMap<Block,List<String>>();
	public com.modcrafting.diablodrops.DiabloDrops dd;
	public Database db;
	public void onEnable(){
		dd = getDiabloDrops();
		if(dd==null){
			this.getLogger().info(" Dependency DiabloDrops not found. Disabled.");
			this.setEnabled(false);
			return;
		}
		this.getDataFolder().mkdir();
		db = new Database(this);
		db.load();
		this.getServer().getPluginManager().registerEvents(new DDChestListener(this), this);
		this.getCommand("ddchest").setExecutor(new DDChestCommand(this));
	}

	public com.modcrafting.diablodrops.DiabloDrops getDiabloDrops() {
		Plugin plugin = getServer().getPluginManager().getPlugin("DiabloDrops");

		if (plugin == null || !(plugin instanceof com.modcrafting.diablodrops.DiabloDrops)) {
			return null;
		}

		return (com.modcrafting.diablodrops.DiabloDrops) plugin;
	}
}
