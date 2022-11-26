package io.salt.wizard.actions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.salt.wizard.db.UserDAO;
import io.vertx.core.json.JsonObject;

/**
 * Increment and decrement token values appropriately. 
 * 
 * @author salt_wizard
 *
 */
public class TokenHandler {
	private static final Logger _logger = LoggerFactory.getLogger(TokenHandler.class);
	
	public static JsonObject incrementToken(JsonObject userJson, int inc) {
		int newTokens = userJson.getInteger("tokens") + inc;
		_logger.trace("Incrementing user {}'s tokens to {}.", userJson.getString("userTag"), newTokens);
		return UserDAO.updateUserTokens(userJson.getLong("userId"), newTokens);
	}

	public static JsonObject decrementToken(JsonObject userJson, int dec) {
		int newTokens = userJson.getInteger("tokens") - dec;
		if(newTokens < 0) {
			_logger.error("Unable to decrement tokens further.");
			return userJson;
		} else {
			_logger.trace("Decrementing user {}'s tokens to {}.", userJson.getString("userTag"), newTokens);
			return UserDAO.updateUserTokens(userJson.getLong("userId"), newTokens);
		}
	}
}
