package me.lukas.skyblockmultiplayer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import me.lukas.skyblockmultiplayer.*;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SQLInstructions {
	private static Connection conn;
	private static Statement stat;

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
					"playerName varchar UNIQUE,"+
					"isOnIsland integer,"+
					"isDead integer,"+
					"livesLeft integer,"+
					"islandsLeft integer," +
					"homeLocation varchar);");
		
		stat.execute("CREATE TABLE IF NOT EXISTS islands (" +
					"islandNumber integer,"+
					"islandLocation varchar,"+
					"playerName varchar UNIQUE);");
		
		stat.execute("CREATE TABLE IF NOT EXISTS oldWorld (" +
						"playerName varchar REFERENCES player(playerName),"+
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
	// @formatter:on


	public static void writeNewPlayerData(Player player) throws SQLException{
		stat.execute("INSERT OR REPLACE INTO players (" +
				"playerName," +
				"isOnIsland," +
				"isDead," +
				"livesLeft," +
				"islandsLeft) VALUES (" +
				"'"+player.getName()+"',"+
				"0,"+
				"0,"+
				Settings.pvp_livesPerIsland+"," +
				Settings.pvp_islandsPerPlayer + ");");
	}
	
	
	
	public static ResultSet loadSkyWorldData(String playername) throws SQLException{
		return stat.executeQuery("SELECT * FROM skyblockWorld " +
				"WHERE playerName = '" + playername + "';");
	}

	public static ResultSet loadFriendData(String playername) throws SQLException{
		return stat.executeQuery("SELECT friendName FROM friends " +
				"WHERE playerName = '" + playername + "';");
	}
	
	public static ResultSet loadOnlinePlayerData(String playername) throws SQLException{
		return stat.executeQuery("SELECT * FROM players " +
				"JOIN islands ON players.playerName = islands.playerName "+
				"JOIN oldWorld ON players.playerName = oldWorld.playerName "+
				"JOIN skyblockWorld ON players.playerName = skyblockWorld.playerName "+
				"WHERE playerName = '" + playername + "';");
	}
		

	/*public static void updatePlayerData(PlayerInfo pi) throws SQLException {
		if (!SQLInstructions.existsUserAlready(pi.getPlayerName())) {
			PreparedStatement addPlayer = conn.prepareStatement("insert into players values (?,?)");
			addPlayer.setString(2, pi.getPlayerName());
			addPlayer.addBatch();

			conn.setAutoCommit(false);
			addPlayer.executeBatch();
			conn.setAutoCommit(true);
		}
		.setFieldValue(pi.getPlayerName(), "placed", AchievementField.PLACED, SQLInstructions.getStringList(pi.getPlacedList()));
		SQLInstructions.setFieldValue(pi.getPlayerName(), "breaked", AchievementField.BREAKED, SQLInstructions.getStringList(pi.getBreakedList()));
		SQLInstructions.setFieldValue(pi.getPlayerName(), "collected", AchievementField.COLLECTED, SQLInstructions.getStringList(pi.getCollectedList()));
		SQLInstructions.setFieldValue(pi.getPlayerName(), "crafted", AchievementField.CRAFTED, SQLInstructions.getStringList(pi.getCraftedList()));
		SQLInstructions.setFieldValue(pi.getPlayerName(), "furnace", AchievementField.FURNACE, SQLInstructions.getStringList(pi.getFurnaceList()));
	}

	public static PlayerInfo getPlayerData(String playerName) throws SQLException {
		PlayerInfo pi = new PlayerInfo(playerName);
		ResultSet rs = stat.executeQuery("SELECT playerId FROM players WHERE playerName = '" + playerName + "';");
		if (rs.next() == false) {
			return null;
		}
		int playerId = rs.getInt("playerId");

		/*rs = stat.executeQuery("SELECT * FROM placed WHERE playerId = " + playerId + ";");
		pi.setPlacedList(SQLInstructions.getItemList(rs.getString("placed")));

		rs = stat.executeQuery("SELECT * FROM breaked WHERE playerId = " + playerId + ";");
		pi.setBreakedList(SQLInstructions.getItemList(rs.getString("breaked")));

		rs = stat.executeQuery("SELECT * FROM collected WHERE playerId = " + playerId + ";");
		pi.setCollectedList(SQLInstructions.getItemList(rs.getString("collected")));

		rs = stat.executeQuery("SELECT * FROM crafted WHERE playerId = " + playerId + ";");
		pi.setCraftedList(SQLInstructions.getItemList(rs.getString("crafted")));

		rs = stat.executeQuery("SELECT * FROM furnace WHERE playerId = " + playerId + ";");
		pi.setFurnaceList(SQLInstructions.getItemList(rs.getString("furnace")));
		return pi;
	}*/
}
