package io.salt.wizard;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.salt.wizard.db.UserSnacksDAO;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class SnackRoller {
	private static final Logger _logger = LoggerFactory.getLogger(SnackRoller.class);
	
	private static JsonArray snacks;
	
	private static double totalPercentage = 0;
	private static Map<Integer, Double> snackVal;
	
	public static void init(JsonArray ja) {
		snackVal = new HashMap<Integer, Double>();
		snacks = ja;
		snacks.forEach(obj -> {
			JsonObject snack = (JsonObject) obj;
			snackVal.put(snack.getInteger("snackId"), totalPercentage += snack.getDouble("rarity"));
		});
		// Print out list of snacks and their values in trace.
		// Final snack should equal 1! Modifications should be done against the database!
		snackVal.forEach((snackId, snackVal) -> {
			_logger.trace("snackId :: {}, snackVal :: {}", snackId, snackVal);
		});
	}
	
	public static synchronized JsonArray returnSnacks() {
		return snacks;
	}
	
	/**
	 * Combines both the SNACKS and USER_SNACKS tables into a JSON for a particular user
	 * @param userSnacks
	 * @return combined JsonArray
	 */
	public static synchronized JsonArray getFullUserSnacks(JsonArray userSnacks) {
		JsonArray fullSnacks = new JsonArray();
		
		for(Object obj1 : snacks) {
			for(Object obj2 : userSnacks) {
				JsonObject snackDetail = (JsonObject) obj1;
				JsonObject userSnack = (JsonObject) obj2;
				if(snackDetail.getInteger("snackId") == userSnack.getInteger("snackId")){
					
					_logger.trace("snackDetail :: {}", snackDetail.encodePrettily());
					_logger.trace("userSnack :: {}", userSnack.encodePrettily());
					
					JsonObject t = new JsonObject();
					t.mergeIn(userSnack).mergeIn(snackDetail);
					fullSnacks.add(t);
				}
			}
		}
		_logger.trace("Combined snack details :: {}", fullSnacks.encodePrettily());
		
		return fullSnacks;
	}
	
	
	/**
	 * Roll for a random number to redeem a snack!
	 * @return snack details
	 */
	public static synchronized JsonObject rollForSnack() {
		
		// TODO - Might need to have a unique Math.random() for each thread later
		double roll = Math.random();
		//_logger.trace("Current roll :: {}", roll);
		
		int result = 0;
		for(Map.Entry<Integer, Double> entry : snackVal.entrySet()) {
			if( roll < entry.getValue()) {
				result = entry.getKey();
				break;
			}
		}
		//_logger.trace("Result :: {}", result);
		
		JsonObject snack = null;
		// Search for the key in the JsonArray to get the snack details
		for(Object obj : snacks) {
			JsonObject jo = (JsonObject) obj;
			if(result == jo.getInteger("snackId")) {
				snack = jo;
				//_logger.trace("Returning the following snack :: {}", snack.encodePrettily());
				break;
			}
		}
		
		return snack;
	}
	
	
	
	
	public static synchronized int getSnackQuantity(long userId, int snackId) {
		JsonArray userSnacks = UserSnacksDAO.selectUserSnacksById(userId);
		_logger.info("CURRENT USER SNACKS :: {}", userSnacks.encodePrettily());
		for(Object obj : userSnacks) {
			JsonObject userSnack = (JsonObject) obj;
			if(userSnack.getInteger("snackId") == snackId) {
				return userSnack.getInteger("quantity");
			}
		}
		return 0;
	}
	
}