package io.salt.wizard.pages;

import java.awt.Color;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;

import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.salt.wizard.Buttons;
import io.vertx.core.json.JsonObject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.callbacks.IMessageEditCallback;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageData;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

public class MenuPage extends Page {
	private final Logger _logger = LoggerFactory.getLogger(this.getClass());
	
	public MenuPage(JsonObject userJson) {
		super(userJson);
	}
	
	@Override
	protected void setSubMapping() {
		// TODO Auto-generated method stub
	}
	
	@Override
	protected MessageEmbed buildEmbed() {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle("Main Menu")
		.setColor(Color.pink)
		.setDescription("Main Menu Description")
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

		Button invButton = Button.primary(Buttons.MAIN_TO_INVENTORY_ID, Buttons.MAIN_TO_INVENTORY_LABEL);
		Button rollButton = Button.primary(Buttons.MAIN_TO_ROLL_ID, Buttons.MAIN_TO_ROLL_LABEL);
		Button redeemButton = Button.primary(Buttons.MAIN_TO_REDEEM_ID, Buttons.MAIN_TO_REDEEM_LABEL);
		Button quizButton = Button.primary(Buttons.MAIN_TO_QUIZ_ID, Buttons.MAIN_TO_QUIZ_LABEL);
		Button donateButton = Button.primary(Buttons.MAIN_TO_DONATE_ID, Buttons.MAIN_TO_DONATE_LABEL);
		
		MessageData data = null;
		if(event instanceof SlashCommandInteractionEvent) {
			data = new MessageCreateBuilder()
						.addEmbeds(me)
						.setComponents(
								ActionRow.of(invButton, rollButton, redeemButton, quizButton),
								ActionRow.of(donateButton)
						)
						.build();
		}
		if(event instanceof ButtonInteractionEvent) {
			data = new MessageEditBuilder()
					.setEmbeds(me)
					.setComponents(
							ActionRow.of(invButton, rollButton, redeemButton, quizButton),
							ActionRow.of(donateButton)
					)
					.build();
		}

		return data;
	}
}
