package io.salt.wizard.pages.redeem;

import org.apache.commons.text.StringSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.salt.wizard.actions.UserHandler;
import io.vertx.core.json.JsonObject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public class RedeemedPage extends RedeemPage {
	private final Logger _logger = LoggerFactory.getLogger(this.getClass());
	
	protected String claimed = "You received a token! Come back tomorrow, friend!";
	
	public RedeemedPage(JsonObject userJson) {
		super(userJson);
	}
	
	/**
	 * Constructs the embed
	 * @return
	 */
	@Override
	protected MessageEmbed buildEmbed() {
		String description = DESCRIPTION_MAIN + claimed;
		
		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle(TITLE)
		.setColor(0x49814a)
		.setDescription(description)
		.addField(TOKEN_FIELD_TITLE, (new StringSubstitutor(valuesMap)).replace(TOKEN_FIELD_DESC), false);
		return eb.build();
	}
	
	public void claimToken(ButtonInteractionEvent event) {
		_logger.trace("------ BEGIN claimToken ------");
		// Increment tokens and updated last claimed date
		this.userJson = UserHandler.incrementToken(userJson, 1);
		this.userJson = UserHandler.updateLastRedeem(userJson);
		_logger.trace("------ END claimToken ------");
	}
}
