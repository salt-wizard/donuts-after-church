package io.salt.wizard.pages.redeem;

import java.awt.Color;
import java.time.Duration;
import java.time.Instant;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.sql.Timestamp;

import org.apache.commons.text.StringSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.salt.wizard.Buttons;
import io.salt.wizard.DonutEmojis;
import io.salt.wizard.pages.Page;
import io.vertx.core.json.JsonObject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageData;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;

public class RedeemPage extends Page {
	private final Logger _logger = LoggerFactory.getLogger(this.getClass());
	
	protected String TITLE = "Redeem Token";
	protected String DESCRIPTION_MAIN = 
		"You can redeem a " + DonutEmojis.token + " token daily." + 
		"\n" + 
		"You can use this token to get a random donut or..." + 
		"\n" + 
		"\n";

	protected String eligible = "You are eligable to claim a free token!";
	protected String claimed = "You have already claimed a token. Come back tomorrow, friend!";
	
	protected final String TOKEN_FIELD_TITLE = "Tokens";
	protected final String TOKEN_FIELD_DESC = "Current tokens: " + DonutEmojis.token + "**${tokens}**";

	protected final long secondsInDay = 86400;
	
	public RedeemPage(JsonObject userJson) {
		super(userJson);
	}
	
	@Override
	protected void setSubMapping() {
		int tokenCount = userJson.getInteger("tokens");
		String lastRedeemed = userJson.getString("lastRedeem");
		valuesMap = new HashMap<>();
		valuesMap.put("tokens", tokenCount + "");
		valuesMap.put("lastRedeem", lastRedeemed + "");
	}
	
	/**
	 * Constructs the embed
	 * @return
	 */
	@Override
	protected MessageEmbed buildEmbed() {
		long diff = returnLastRedeemInSeconds();
		
		String description = DESCRIPTION_MAIN + claimed;
		if(diff >= secondsInDay) {
			description = DESCRIPTION_MAIN + eligible;
		}
		
		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle(TITLE)
		.setColor(0x49814a)
		.setDescription(description)
		.addField(TOKEN_FIELD_TITLE, (new StringSubstitutor(valuesMap)).replace(TOKEN_FIELD_DESC), false);
		return eb.build();
	}
	
	/**
	 * Create the main menu for the user
	 * @param event
	 * @param userJson
	 * @return
	 */
	@Override
	protected MessageData buildPage(GenericEvent event) {
		int tokenCount = userJson.getInteger("tokens");
		setSubMapping();
		
		MessageEmbed me = buildEmbed();

		Button redeemButton = Button.success(Buttons.REDEEM_TOKEN_ID, Buttons.REDEEM_TOKEN_LABEL);
		Button backButton = Button.secondary(Buttons.REDEEM_TO_MAIN_ID, Buttons.REDEEM_TO_MAIN_LABEL);
		
		MessageData data = null;
		if(event instanceof SlashCommandInteractionEvent) {
			data = new MessageCreateBuilder()
						.addEmbeds(me)
						.setComponents(
								ActionRow.of(redeemButton, backButton)
						)
						.build();
		}
		if(event instanceof ButtonInteractionEvent) {
			data = new MessageEditBuilder()
					.setEmbeds(me)
					.setComponents(
							ActionRow.of(redeemButton, backButton)
					)
					.build();
		}

		return data;
	}
	
	protected long returnLastRedeemInSeconds() {
		Instant now = Instant.now();
		Instant then = Timestamp.valueOf(userJson.getString("lastRedeem")).toInstant();
		Duration diff = Duration.between(then, now);
		
		_logger.trace("Now :: {}", now.toString());
		_logger.trace("Then :: {}", then.toString());
		_logger.trace("Diff :: {}", diff.toSeconds());
		
		return diff.toSeconds();
	}
}
