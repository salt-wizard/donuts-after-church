package io.salt.wizard.pages.redeem;

import java.awt.Color;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.salt.wizard.Buttons;
import io.salt.wizard.pages.Page;
import io.vertx.core.json.JsonObject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageData;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;

public class RedeemPage extends Page {
	private final Logger _logger = LoggerFactory.getLogger(this.getClass());
	
	public RedeemPage(JsonObject userJson) {
		super(userJson);
	}
	
	@Override
	protected void setSubMapping() {
		// TODO
	}
	
	/**
	 * Constructs the embed
	 * @return
	 */
	@Override
	protected MessageEmbed buildEmbed() {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle("Redeem Token")
		.setColor(Color.black)
		.setDescription("Redeem Token")
		.addField("title of field", "test of field", false)
		.addBlankField(false);
		//.setImage("attachment://test.png");
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
		MessageEmbed me = buildEmbed();

		Button backButton = Button.success(Buttons.REDEEM_TO_MAIN_ID, Buttons.REDEEM_TO_MAIN_LABEL);
		
		MessageData data = null;
		if(event instanceof SlashCommandInteractionEvent) {
			data = new MessageCreateBuilder()
						.addEmbeds(me)
						.addActionRow(backButton)
						.build();
		}
		if(event instanceof ButtonInteractionEvent) {
			data = new MessageEditBuilder()
					.setEmbeds(me)
					.setActionRow(backButton)
					.build();
		}

		return data;
	}
}
