package io.salt.wizard.pages;

import java.awt.Color;

import io.salt.wizard.Buttons;
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

public class InventoryPage extends Page {

	public InventoryPage(JsonObject userJson) {
		super(userJson);
	}
	
	@Override
	protected void setSubMapping() {
		// TODO Auto-generated method stub
	}
	
	@Override
	protected MessageEmbed buildEmbed() {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle("Inventory")
		.setColor(Color.red)
		.setDescription("Inventory Description")
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

		Button backButton = Button.secondary(Buttons.INVENTORY_TO_MAIN_ID, Buttons.INVENTORY_TO_MAIN_LABEL);


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
