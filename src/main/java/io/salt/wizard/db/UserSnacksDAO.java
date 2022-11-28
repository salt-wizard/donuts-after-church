package io.salt.wizard.db;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class UserSnacksDAO {
	private static final Logger _logger = LoggerFactory.getLogger(UserDAO.class);
	
	private static final String SELECT_USER_SNACKS_BY_ID = "SELECT USER_ID, SNACK_ID, QUANTITY FROM USER_SNACKS WHERE USER_ID=?";
	
	private static final String UPDATE_USER_SNACKS = "CALL UPDATE_USER_SNACKS(?, ?, ?)";
	
	private static final String INSERT_NEW_USER_SNACKS = "INSERT INTO USER_SNACKS (USER_ID, SNACK_ID, QUANTITY) VALUES (?, ?, ?)"
			+ "RETURNING USER_ID, SNACK_ID, QUANTITY;";
	
	public static JsonArray selectUserSnacksById(long userId) {
		JsonArray result = new JsonArray();
		
		try (Connection conn = HikariCPProvider.getConnection();) {
			PreparedStatement ps = conn.prepareStatement(SELECT_USER_SNACKS_BY_ID);
			ps.setLong(1, userId);
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				JsonObject jo = new JsonObject();
				jo.put("userId",rs.getLong("USER_ID"));
				jo.put("snackId",rs.getInt("SNACK_ID"));
				jo.put("quantity",rs.getInt("QUANTITY"));
				result.add(jo);
			}
			conn.close();
		} catch (SQLException e) {
			_logger.error("SQL Exception occurred :: {}", e.getMessage());
			return null;
		}
		
		return result;
	}
	
	public static JsonObject updateSnackQuantity(long userId, int snackId, int quantity) {
		JsonObject result = new JsonObject();
		
		try (Connection conn = HikariCPProvider.getConnection();) {
			
			CallableStatement cs = conn.prepareCall(UPDATE_USER_SNACKS);
			int index = 1;
			cs.setInt(index++, snackId);
			cs.setInt(index++, quantity);
			cs.setLong(index++, userId);

			ResultSet rs = cs.executeQuery();
			while(rs.next()) {
				result.put("userId",rs.getLong("USER_ID"));
				result.put("userName",rs.getString("USER_NAME"));
				result.put("discriminator",rs.getString("DISCRIMINATOR"));
				result.put("userTag",rs.getString("USER_TAG"));
				result.put("userAvatar",rs.getString("USER_AVATAR"));
				result.put("userFlags",rs.getInt("USER_FLAGS"));
				result.put("dateCreated", rs.getString("DATE_CREATED"));
				result.put("tokens",rs.getInt("TOKENS"));
			}
			conn.close();
		} catch (SQLException e) {
			_logger.error("SQL Exception occurred :: {}", e.getMessage());
			return null;
		}
		
		return result;
	}
	
	public static JsonArray addNewUserSnacks(long userId, JsonArray snacks) {
		JsonArray result = new JsonArray();
		
		try (Connection conn = HikariCPProvider.getConnection();) {
			for(Object obj : snacks) {
				JsonObject snack = (JsonObject) obj;
				int snackId = snack.getInteger("snackId");
				PreparedStatement ps = conn.prepareStatement(INSERT_NEW_USER_SNACKS, Statement.RETURN_GENERATED_KEYS);
				int index = 1;
				ps.setLong(index++, userId);
				ps.setInt(index++, snackId);
				ps.setInt(index++, 0);
				
				ResultSet rs = ps.executeQuery();
				while(rs.next()) {
					JsonObject jo = new JsonObject();
					jo.put("userId",rs.getLong("USER_ID"));
					jo.put("userName",rs.getInt("SNACK_ID"));
					jo.put("discriminator",rs.getInt("QUANTITY"));
					result.add(jo);
				}
			}
			
			conn.close();
		} catch (SQLException e) {
			_logger.error("SQL Exception occurred :: {}", e.getMessage());
			return null;
		}
		
		return result;
	}
}