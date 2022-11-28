package io.salt.wizard.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class SnacksDAO {
	private static final Logger _logger = LoggerFactory.getLogger(SnacksDAO.class);
	
	private static final String SELECT_ALL_SNACKS = "SELECT * FROM SNACKS";
	
	/**
	 * Return the user ID for the user based on the two unique snowflake IDs for both the user and the guild the message is
	 * being sent from.
	 * @param user - User interaction details
	 * @param guild - Guild interaction details
	 * @return The unique user ID of the unique user as a String.
	 */
	public static synchronized JsonArray returnAllSnacks() {
		JsonArray result = new JsonArray();
		
		try (Connection conn = HikariCPProvider.getConnection();) {
			PreparedStatement ps = conn.prepareStatement(SELECT_ALL_SNACKS);
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				JsonObject jo = new JsonObject();
				jo.put("snackId",rs.getInt("SNACK_ID"));
				jo.put("snackName",rs.getString("SNACK_NAME"));
				jo.put("snackDesc",rs.getString("SNACK_DESC"));
				jo.put("snackImg",rs.getString("SNACK_IMG"));
				jo.put("rarity",rs.getDouble("RARITY"));
				jo.put("specialMsg",rs.getString("SPECIAL_MSG"));
				result.add(jo);
			}
			conn.close();
		} catch (SQLException e) {
			_logger.error("SQL Exception occurred :: {}", e.getMessage());
			return null;
		}
		
		return result;
	}
}
