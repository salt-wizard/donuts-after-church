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
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

public class UserDAO {
	private static final Logger _logger = LoggerFactory.getLogger(UserDAO.class);
	
	private static final String SELECT_USER_BY_ID = "SELECT USER_ID, USER_NAME, DISCRIMINATOR, USER_TAG, USER_AVATAR, "
			+ "USER_FLAGS, DATE_CREATED, TOKENS, DONATED, BLESSING, LAST_REDEEM FROM USERS WHERE USER_ID=?;";
	
	private static final String INSERT_USER = "INSERT INTO USERS (USER_ID, USER_NAME, DISCRIMINATOR, USER_TAG, "
			+ "USER_AVATAR, USER_FLAGS, DATE_CREATED, TOKENS, DONATED, BLESSING, LAST_REDEEM) "
			+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING USER_ID, USER_NAME, DISCRIMINATOR, USER_TAG, "
			+ "USER_AVATAR, USER_FLAGS, DATE_CREATED, TOKENS, DONATED, BLESSING, LAST_REDEEM;";
	
	private static final String UPDATE_USER_TOKEN = "CALL UPDATE_USER_TOKENS(?, ?)";
	private static final String UPDATE_USER_DONATED = "CALL UPDATE_USER_DONATED(?, ?)";
	private static final String UPDATE_USER_BLESSING = "CALL UPDATE_USER_BLESSING(?, ?)";
	private static final String UPDATE_USER_CLAIMED = "CALL UPDATE_USER_CLAIMED(?, ?)";
	
	private static final int secondsInDay = 86400;
	
	public static JsonObject selectUserById(long userId) {
		JsonObject result = new JsonObject();
		
		try (Connection conn = HikariCPProvider.getConnection();) {
			PreparedStatement ps = conn.prepareStatement(SELECT_USER_BY_ID);
			ps.setLong(1, userId);
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				result.put("userId",rs.getLong("USER_ID"));
				result.put("userName",rs.getString("USER_NAME"));
				result.put("discriminator",rs.getString("DISCRIMINATOR"));
				result.put("userTag",rs.getString("USER_TAG"));
				result.put("userAvatar",rs.getString("USER_AVATAR"));
				result.put("userFlags",rs.getInt("USER_FLAGS"));
				result.put("dateCreated", rs.getString("DATE_CREATED"));
				result.put("tokens",rs.getInt("TOKENS"));
				result.put("donated",rs.getInt("DONATED"));
				result.put("blessing",rs.getInt("BLESSING"));
				result.put("lastRedeem",rs.getString("LAST_REDEEM"));
			}
			conn.close();
		} catch (SQLException e) {
			_logger.error("SQL Exception occurred :: {}", e.getMessage());
			return null;
		}
		
		return result;
	}
	
	
	public static JsonObject addNewUser(User _user) {
		JsonObject result = new JsonObject();
		
		try (Connection conn = HikariCPProvider.getConnection();) {
			
			PreparedStatement ps = conn.prepareStatement(INSERT_USER, Statement.RETURN_GENERATED_KEYS);
			int index = 1;
			ps.setLong(index++, _user.getIdLong());
			ps.setString(index++, _user.getName());
			ps.setString(index++, _user.getDiscriminator());
			ps.setString(index++, _user.getAsTag());
			ps.setString(index++, _user.getEffectiveAvatarUrl());
			ps.setInt(index++, _user.getFlagsRaw());
			ps.setTimestamp(index++, Timestamp.from(Instant.now()));
			ps.setInt(index++, 0); // Initial token value should be 0!!!
			ps.setInt(index++, 0); // Initial donated value should be 0!!!
			ps.setInt(index++, 0); // Initial blessing value should be 0!!!
			ps.setTimestamp(index++, Timestamp.from(Instant.now().minusSeconds(secondsInDay))); // 1 day before
			
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				result.put("userId",rs.getLong("USER_ID"));
				result.put("userName",rs.getString("USER_NAME"));
				result.put("discriminator",rs.getString("DISCRIMINATOR"));
				result.put("userTag",rs.getString("USER_TAG"));
				result.put("userAvatar",rs.getString("USER_AVATAR"));
				result.put("userFlags",rs.getInt("USER_FLAGS"));
				result.put("dateCreated", rs.getString("DATE_CREATED"));
				result.put("tokens",rs.getInt("TOKENS"));
				result.put("donated",rs.getInt("DONATED"));
				result.put("blessing",rs.getInt("BLESSING"));
				result.put("lastRedeem",rs.getString("LAST_REDEEM"));
			}
			conn.close();
		} catch (SQLException e) {
			_logger.error("SQL Exception occurred :: {}", e.getMessage());
			return null;
		}
		
		return result;
	}
	
	public static JsonObject updateUserTokens(long userId, int tokenVal) {
		JsonObject result = new JsonObject();
		
		try (Connection conn = HikariCPProvider.getConnection();) {
			
			CallableStatement cs = conn.prepareCall(UPDATE_USER_TOKEN);
			int index = 1;
			cs.setInt(index++, tokenVal);
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
				result.put("donated",rs.getInt("DONATED"));
				result.put("blessing",rs.getInt("BLESSING"));
				result.put("lastRedeem",rs.getString("LAST_REDEEM"));
			}
			conn.close();
		} catch (SQLException e) {
			_logger.error("SQL Exception occurred :: {}", e.getMessage());
			return null;
		}
		
		return result;
	}
	
	public static JsonObject updateUserDonated(long userId, int donated) {
		JsonObject result = new JsonObject();
		
		try (Connection conn = HikariCPProvider.getConnection();) {
			
			CallableStatement cs = conn.prepareCall(UPDATE_USER_DONATED);
			int index = 1;
			cs.setInt(index++, donated);
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
				result.put("donated",rs.getInt("DONATED"));
				result.put("blessing",rs.getInt("BLESSING"));
				result.put("lastRedeem",rs.getString("LAST_REDEEM"));
			}
			conn.close();
		} catch (SQLException e) {
			_logger.error("SQL Exception occurred :: {}", e.getMessage());
			return null;
		}
		
		return result;
	}
	
	public static JsonObject updateUserBlessing(long userId, int blessingCount) {
		JsonObject result = new JsonObject();
		
		try (Connection conn = HikariCPProvider.getConnection();) {
			
			CallableStatement cs = conn.prepareCall(UPDATE_USER_BLESSING);
			int index = 1;
			cs.setInt(index++, blessingCount);
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
				result.put("donated",rs.getInt("DONATED"));
				result.put("blessing",rs.getInt("BLESSING"));
				result.put("lastRedeem",rs.getString("LAST_REDEEM"));
			}
			conn.close();
		} catch (SQLException e) {
			_logger.error("SQL Exception occurred :: {}", e.getMessage());
			return null;
		}
		
		return result;
	}
	
	public static JsonObject updateLastRedeem(long userId) {
		JsonObject result = new JsonObject();
		
		try (Connection conn = HikariCPProvider.getConnection();) {
			
			CallableStatement cs = conn.prepareCall(UPDATE_USER_CLAIMED);
			int index = 1;
			cs.setTimestamp(index++, Timestamp.from(Instant.now()));
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
				result.put("donated",rs.getInt("DONATED"));
				result.put("blessing",rs.getInt("BLESSING"));
				result.put("lastRedeem",rs.getString("LAST_REDEEM"));
			}
			conn.close();
		} catch (SQLException e) {
			_logger.error("SQL Exception occurred :: {}", e.getMessage());
			return null;
		}
		
		return result;
	}
}
