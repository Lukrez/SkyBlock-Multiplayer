package me.lukas.skyblockmultiplayer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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

	public static void createTables() throws SQLException {
		for (Tables table : Tables.values()) {
			String createTableWithColumns = "CREATE TABLE IF NOT EXISTS " + table.tableName + " ( ";
			if (table.columns.length == 1) {
				createTableWithColumns += table.columns[0] + " );";
				stat.execute(createTableWithColumns);
			} else {
				for (int i = 0; i < table.columns.length; i++) {
					createTableWithColumns += table.columns[i];
					if (table.columns.length != i + 1) {
						createTableWithColumns += ",";
					}
				}
				createTableWithColumns += " );";
				stat.execute(createTableWithColumns);
			}
			System.out.println(createTableWithColumns);
		}
	}

	// @formatter:off
	private enum Tables {
		PLAYERS("players", new String[] { "playerId integer primary key autoincrement",
										  "playerName varchar"  }),
		OLDWORLD("oldworld", new String[] { "playerId integer",
											"inventoryId integer",
											"armorId integer",
											"health integer",
											"food integer",
											"exp integer",
											"level integer" }),
		SKYBLOCKWORLD("skyblockWorld", new String[] { "playerId integer",
													  "onIsland integer",
													  "hasIsland integer",
													  "isDead integer",
													  "livesLeft integer",
													  "islandsLeft integer",
													  "inventoryId integer",
													  "armorId varchar",
													  "health integer",
													  "food integer",
													  "exp integer",
													  "level integer",
													  "islandLocation varchar",
													  "homeLocation varchar",
													  "friends varchar" }),
		ISLANDLOCATIONS("islands", new String[] { "playerId integer",
													"islandLocation varchar",
													"islandNumber integer" }),
		INVENTORIES("inventories", new String[] { "inventoryId integer",
												  "inventory varchar" });
		
		private String tableName;
		private String[] columns;

		private Tables(String tableName, String[] columns) {
			this.tableName = tableName;
			this.columns = columns;
		}
	}
	// @formatter:on

	public static void addFieldIfNotExists(String tableName, String columnName, int playerId) throws SQLException {
		ResultSet rs = stat.executeQuery("SELECT * FROM " + tableName + " WHERE playerId = " + playerId + ";");
		boolean res = rs.next();
		if (res == true) {
			return;
		}

		PreparedStatement setField = conn.prepareStatement("insert into " + tableName + " values (?,?)");
		setField.setInt(1, playerId);
		setField.addBatch();

		conn.setAutoCommit(false);
		setField.executeBatch();
		conn.setAutoCommit(true);
	}

	public static boolean existsUserAlready(String playerName) throws SQLException {
		ResultSet rs = stat.executeQuery("SELECT * FROM players WHERE playerName = '" + playerName + "';");
		boolean res = rs.next();
		if (res == false) {
			return false;
		}
		return true;
	}

	public static void updatePlayerData(PlayerInfo pi) throws SQLException {
		if (!SQLInstructions.existsUserAlready(pi.getPlayerName())) {
			PreparedStatement addPlayer = conn.prepareStatement("insert into players values (?,?)");
			addPlayer.setString(2, pi.getPlayerName());
			addPlayer.addBatch();

			conn.setAutoCommit(false);
			addPlayer.executeBatch();
			conn.setAutoCommit(true);
		}
		/*.setFieldValue(pi.getPlayerName(), "placed", AchievementField.PLACED, SQLInstructions.getStringList(pi.getPlacedList()));
		SQLInstructions.setFieldValue(pi.getPlayerName(), "breaked", AchievementField.BREAKED, SQLInstructions.getStringList(pi.getBreakedList()));
		SQLInstructions.setFieldValue(pi.getPlayerName(), "collected", AchievementField.COLLECTED, SQLInstructions.getStringList(pi.getCollectedList()));
		SQLInstructions.setFieldValue(pi.getPlayerName(), "crafted", AchievementField.CRAFTED, SQLInstructions.getStringList(pi.getCraftedList()));
		SQLInstructions.setFieldValue(pi.getPlayerName(), "furnace", AchievementField.FURNACE, SQLInstructions.getStringList(pi.getFurnaceList()));*/
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
		pi.setFurnaceList(SQLInstructions.getItemList(rs.getString("furnace")));*/
		return pi;
	}
}
