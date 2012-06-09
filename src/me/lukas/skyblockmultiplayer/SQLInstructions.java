package me.lukas.skyblockmultiplayer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.Location;


public class SQLInstructions {
	private static Connection conn;
	private static Statement stat;

	public static int bool2int(boolean b){
		if (b)
			return 1;
		return 0;
	}
	
	public static void initializeConnections() throws ClassNotFoundException {
		try {
			Class.forName("org.sqlite.JDBC");
			SQLInstructions.conn = DriverManager.getConnection("jdbc:sqlite:" + SkyBlockMultiplayer.getInstance().fileSQLite);
			SQLInstructions.stat = conn.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}	

	public static void closeConnections() throws SQLException {
		SQLInstructions.stat.close();
		SQLInstructions.conn.close();
	}

	// @formatter:off
	public static void createTables() throws SQLException {

		stat.execute("CREATE TABLE IF NOT EXISTS metadata (" +
				"version integer, "+
				"info varchar);");
	
		stat.execute("CREATE TABLE IF NOT EXISTS players (" +
					"playerName varchar primary key,"+
					"isOnIsland integer,"+
					"isDead integer,"+
					"livesLeft integer,"+
					"islandsLeft integer," +
					"homeLocation varchar);");
		
		stat.execute("CREATE TABLE IF NOT EXISTS islands (" +
					"islandNumber integer primary key AUTOINCREMENT,"+
					"islandLocation varchar,"+
					"x integer,"+
					"z integer,"+
					"playerName varchar);");
		
		stat.execute("CREATE TABLE IF NOT EXISTS oldWorld (" +
						"playerName varchar primary key REFERENCES player(playerName),"+
						"location varchar,"+
						"inventory varchar,"+
						"armor varchar,"+
						"health integer,"+
						"food integer,"+
						"exp integer,"+
						"level integer);");
		
		stat.execute("CREATE TABLE IF NOT EXISTS skyblockWorld (" +
						"playerName varchar REFERENCES player(playerName),"+
						"location varchar,"+
						"inventory varchar,"+
						"armor varchar,"+
						"health integer,"+
						"food integer,"+
						"exp integer,"+
						"level integer);");
		
		stat.execute("CREATE TABLE IF NOT EXISTS friends (" +
						"playername varchar REFERENCES player(playername),"+
						"friendname varchar REFERENCES player(playername))");
		
		
		
	}
	
	public static boolean writePartialPlayerData(PlayerData player){
		try {
			stat.execute("INSERT OR REPLACE INTO players (" +
					"playerName," +
					"isOnIsland," +
					"isDead," +
					"livesLeft," +
					"islandsLeft) VALUES (" +
					"'"+player.getPlayerName()+"',"+
					SQLInstructions.bool2int(player.isOnIsland())+","+
					SQLInstructions.bool2int(player.isDead())+","+
					player.getLivesLeft()+","+
					player.getIslandsLeft() + ");");
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static boolean writeIslandData(PlayerData pdata){
		try {
			stat.execute("INSERT OR REPLACE INTO skyblockWorld (" +
					"playerName,"+
					"location,"+
					"inventory,"+
					"armor,"+
					"health,"+
					"food,"+
					"exp,"+
					"level) VALUES ("+
					"'"+pdata.getPlayerName()+"',"+
					"'"+SkyBlockMultiplayer.getInstance().LocationToString(pdata.getIslandLocation())+"',"+
					"'"+ItemParser.InventoryToString(pdata.getIslandInventory())+"',"+
					"'"+ItemParser.InventoryToString(pdata.getIslandArmor())+"',"+
					pdata.getIslandHealth()+","+
					pdata.getIslandFood()+","+
					pdata.getIslandExp()+","+
					pdata.getIslandLevel()+");");
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static boolean writeOldWorldData(PlayerData pdata){
		try {
			stat.execute("INSERT OR REPLACE INTO oldWorld (" +
					"playerName,"+
					"location,"+
					"inventory,"+
					"armor,"+
					"health,"+
					"food,"+
					"exp,"+
					"level) VALUES ("+
					"'"+pdata.getPlayerName()+"',"+
					"'"+SkyBlockMultiplayer.getInstance().LocationToString(pdata.getOldLocation())+"',"+
					"'"+ItemParser.InventoryToString(pdata.getOldInventory())+"',"+
					"'"+ItemParser.InventoryToString(pdata.getOldArmor())+"',"+
					pdata.getOldHealth()+","+
					pdata.getOldFood()+","+
					pdata.getOldExp()+","+
					pdata.getOldLevel()+");");
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static boolean existsPlayer(String playerName){
		ResultSet rs;
		try {
			rs = stat.executeQuery("SELECT COUNT(*) FROM players WHERE playerName = '"+playerName+"';");
		rs.next();
		if (rs.getInt(1) == 0)
			return false;
		return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
		
	public static boolean loadPartialPlayerData(PlayerData pdata){
		ResultSet rs;
		try {
			rs = stat.executeQuery("SELECT * FROM players" +
									" JOIN islands ON islands.playerName = players.playerName" +
									" WHERE players.playerName = '"+pdata.getPlayerName()+"';");
		if (rs.next() == false)
			return true;

		pdata.setHasIslandS(rs.getBoolean("islandNumber"));
		pdata.setDeathStatus(rs.getBoolean("isDead"));
		pdata.setIslandsLeft(rs.getInt("islandsLeft"));
		pdata.setLivesLeft(rs.getInt("livesLeft"));
		pdata.setHomeLocation(SkyBlockMultiplayer.getInstance().StringToLocation(rs.getString("homeLocation")));
		return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static boolean loadOldWorldData(PlayerData pdata){
		ResultSet rs;	
		try {
			rs = stat.executeQuery("SELECT * FROM oldWorld " +
					"WHERE playerName = '"+pdata.getPlayerName()+"';");
		if (rs.next() == false)
			return true;

		pdata.setOldLocation(SkyBlockMultiplayer.getInstance().StringToLocation(rs.getString("location")));
		pdata.setOldInventory(ItemParser.StringToInventory(rs.getString("inventory"), 36));
		pdata.setOldArmor(ItemParser.StringToInventory(rs.getString("armor"), 4));
		pdata.setOldHealth(rs.getInt("health"));
		pdata.setOldFood(rs.getInt("food"));
		pdata.setOldExp(rs.getInt("exp"));
		pdata.setOldLevel(rs.getInt("level"));
		
		return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static boolean loadIslandData(PlayerData pdata){
		ResultSet rs;	
		try {
			rs = stat.executeQuery("SELECT * FROM skyblockWorld " +
					"WHERE playerName = '"+pdata.getPlayerName()+"';");
		if (rs.next() == false)
			return true;

		pdata.setIslandLocation(SkyBlockMultiplayer.getInstance().StringToLocation(rs.getString("location")));
		pdata.setIslandInventory(ItemParser.StringToInventory(rs.getString("inventory"), 36));
		pdata.setIslandArmor(ItemParser.StringToInventory(rs.getString("armor"), 4));
		pdata.setIslandHealth(rs.getInt("health"));
		pdata.setIslandFood(rs.getInt("food"));
		pdata.setIslandExp(rs.getInt("exp"));
		pdata.setIslandLevel(rs.getInt("level"));
		
		return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static boolean writeNewIsland(PlayerData pdata, CreateNewIsland island){
		
		try {
			stat.execute("INSERT OR REPLACE INTO islands (" +
					"islandLocation," +
					"x," +
					"z,"+
					"playerName) VALUES ("+
					"'"+SkyBlockMultiplayer.getInstance().LocationToString(island.Islandlocation)+"',"+
					island.Islandlocation.getBlockX()+","+
					island.Islandlocation.getBlockZ()+","+
					"'"+pdata.getPlayerName()+"');");
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} 
	}
	
	public int getNearestIsland(Location click, int distance){
		int xmin = click.getBlockX()-distance;
		int xmax = click.getBlockX()+distance;
		int zmin = click.getBlockZ()-distance;
		int zmax = click.getBlockZ()+distance;
		
		try {
			ResultSet rs = stat.executeQuery("SELECT islandNumber from islands " +
												"WHERE x >= "+xmin+
												"AND x <= "+xmax+
												"AND z >= "+zmin+
												"AND z <= "+zmax+";");
			if (!rs.next())
				return -1;
			return rs.getInt("islandNumber");
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
	
	}
	// @formatter:on
}
