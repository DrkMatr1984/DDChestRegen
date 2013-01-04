package com.modcrafting.ddchestregen;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.Material;

public class Database {
	private DDChestRegen plugin;
	public Database(DDChestRegen plugin){
		this.plugin=plugin;
	}
	public Connection getSQLConnection() {
		File dataFolder = new File(plugin.getDataFolder(), "sqlite.db");
		if (!dataFolder.exists()){
			try {
				dataFolder.createNewFile();
			} catch (IOException e) {
				plugin.getLogger().log(Level.SEVERE, "File write error: sqlite.db");
			}
		}
		try {
			Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
    		return conn;
		} catch (SQLException ex) {
			plugin.getLogger().log(Level.SEVERE,"SQLite exception on initialize", ex);
	    } catch (ClassNotFoundException ex) {
	    	plugin.getLogger().log(Level.SEVERE, "You need the SQLite JBDC library. Google it. Put it in /lib folder.");
	    }
		return null;
    }
	public String createTable = "CREATE TABLE IF NOT EXISTS chests (" +
			"`x` INTEGER," +
			"`y` INTEGER," + 
			"`z` INTEGER," + 
			"`world` TEXT," + 
			"`player` TEXT," + 
			"`id` INTEGER PRIMARY KEY" + 
			");";

	public void load() {
		Connection conn = getSQLConnection();
		Statement s;
		try {
			s = conn.createStatement();
			s.executeUpdate(createTable);
			s.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		initialize();
	}
	public void initialize(){
		Connection conn = getSQLConnection();
		if(conn != null){
			PreparedStatement ps = null;
			ResultSet rs = null;
			try{
				ps = conn.prepareStatement("SELECT * FROM chests");
	            rs = ps.executeQuery();
				while (rs.next()){
					int x = rs.getInt("x");
					int y = rs.getInt("y");
					int z = rs.getInt("z");
					String world = rs.getString("world");
					String player = rs.getString("player");
					Location loc = new Location(plugin.getServer().getWorld(world),x,y,z);
					if(plugin.blocks.containsKey(loc)){
						List<String> names = plugin.blocks.get(loc);
						names.add(player);
						plugin.blocks.put(loc.getBlock(),names);
					}else if(loc.getBlock().getType().equals(Material.CHEST)){
						List<String> names = new ArrayList<String>();
						names.add(player);
						plugin.blocks.put(loc.getBlock(),names);
					}
				}
				close(conn,ps,rs);
			} catch (SQLException ex) {
				plugin.getLogger().log(Level.SEVERE, "Unable to retreive connection", ex);
			}
		}else{
			plugin.getLogger().log(Level.SEVERE, "Unable to retreive connection");
		}
	}

	public void add(Location loc,String name){
		Connection conn;
		PreparedStatement ps;
		try {
			conn = getSQLConnection();
			ps = conn.prepareStatement("REPLACE INTO chests (x,y,z,world,player) VALUES(?,?,?,?,?)");
			ps.setInt(1, (int) loc.getX());
			ps.setInt(2, (int) loc.getY());
			ps.setInt(3, (int) loc.getZ());
			ps.setString(4, loc.getWorld().getName());
			ps.setString(5, name);
			ps.executeUpdate();
			close(conn,ps,null);
		} catch (SQLException ex) {
			plugin.getLogger().log(Level.SEVERE, "Unable to retreive connection", ex);
		}
	}
	public void remove(Location loc){
		Connection conn;
		PreparedStatement ps;
		try {
			conn = getSQLConnection();
			ps = conn.prepareStatement("DELETE FROM chests WHERE x = ? AND y = ? AND z = ? AND world = ?");
			ps.setInt(1, (int) loc.getX());
			ps.setInt(2, (int) loc.getY());
			ps.setInt(3, (int) loc.getZ());
			ps.setString(4, loc.getWorld().getName());
			ps.executeUpdate();
			close(conn,ps,null);
		} catch (SQLException ex) {
			plugin.getLogger().log(Level.SEVERE, "Unable to retreive connection", ex);
		}		
	}
	public void remove(Location loc,String playername){
		Connection conn;
		PreparedStatement ps;
		try {
			conn = getSQLConnection();
			ps = conn.prepareStatement("DELETE FROM chests WHERE x = ? AND y = ? AND z = ? AND world = ? AND player=?");
			ps.setInt(1, (int) loc.getX());
			ps.setInt(2, (int) loc.getY());
			ps.setInt(3, (int) loc.getZ());
			ps.setString(4, loc.getWorld().getName());
			ps.setString(5, playername);
			ps.executeUpdate();
			close(conn,ps,null);
		} catch (SQLException ex) {
			plugin.getLogger().log(Level.SEVERE, "Unable to retreive connection", ex);
		}		
	}
	public void close(Connection conn,PreparedStatement ps,ResultSet rs){
		try {
			if (ps != null)
				ps.close();
			if (conn != null)
				conn.close();
			if (rs != null)
				rs.close();
		} catch (SQLException ex) {
			plugin.getLogger().log(Level.SEVERE, "Unable to retreive connection", ex);
		}
	}
}
