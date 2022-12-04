package io.salt.wizard.pages.donate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.salt.wizard.DonutEmojis;
import io.salt.wizard.actions.UserHandler;
import io.vertx.core.json.JsonObject;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public class DonateResultPage extends DonatePage {
	private final Logger _logger = LoggerFactory.getLogger(this.getClass());
	
	protected String DESCRIPTION_MAIN = 
			"Chat looks up at you and gives you a smile." + 
			"\n" + 
			"Do you wish to give chat another " + DonutEmojis.token + " token?";
	
	public DonateResultPage(JsonObject userJson) {
		super(userJson);
	}
	
	/**
	 * Donates a token to poor chat
	 * @param event
	 */
	public void donateToken(ButtonInteractionEvent event) {
		_logger.trace("------ BEGIN donateToken ------");
		int tokenCount = userJson.getInteger("tokens");
		
		if(tokenCount > 0) {
			this.userJson = UserHandler.decrementToken(userJson, 1);
			this.userJson = UserHandler.incrementDonated(userJson, 1);
		} else {
			_logger.error("Not enough tokens to donate, exiting.");
		}
		_logger.trace("------ END donateToken ------");
	}
}
