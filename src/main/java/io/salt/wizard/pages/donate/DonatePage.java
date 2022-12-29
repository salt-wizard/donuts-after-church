package io.salt.wizard.pages.donate;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

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
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageData;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;

public class DonatePage extends Page {
	private final Logger _logger = LoggerFactory.getLogger(this.getClass());
	
	protected String TITLE = "Donate Tokens";
	protected String DESCRIPTION_MAIN = 
		"This poor little chat down on luck is asking for some spare tokens." + 
		"\n" + 
		"Do you wish to give chat a " + DonutEmojis.token + " token?";
	
	protected final String TOKEN_FIELD_TITLE = "Tokens";
	protected final String TOKEN_FIELD_DESC = "Current tokens: " + DonutEmojis.token + "**${tokens}**";
	
	protected final String DONATED_FIELD_TITLE = "Donated";
	protected final String DONATED_FIELD_DESC = "You have donated " + DonutEmojis.token + "**${donated}** to chat so far";
	
	public DonatePage(JsonObject userJson) {
		super(userJson);
	}
	
	@Override
	protected void setSubMapping() {
		int tokenCount = userJson.getInteger("tokens");
		int donatedCount = userJson.getInteger("donated");
		valuesMap = new HashMap<>();
		valuesMap.put("tokens", tokenCount + "");
		valuesMap.put("donated", donatedCount + "");
	}
	
	@Override
	protected MessageEmbed buildEmbed() {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle(TITLE)
		.setColor(0x49814a)
		.setDescription(DESCRIPTION_MAIN)
		.addField(TOKEN_FIELD_TITLE, (new StringSubstitutor(valuesMap)).replace(TOKEN_FIELD_DESC), false)
		.addField(DONATED_FIELD_TITLE, (new StringSubstitutor(valuesMap)).replace(DONATED_FIELD_DESC), false)
		.setImage("attachment://donate_chat.png");
		return eb.build();
	}
	
	@Override
	protected MessageData buildPage(GenericEvent event) {
		int tokenCount = userJson.getInteger("tokens");
		setSubMapping();
		
		// Upload chat image
		InputStream input = null;
		try {
			input = new FileInputStream("src/main/resources/images/donate_chat.png");
		} catch (FileNotFoundException e) {
			_logger.error("FilesNotFoundException caught :: {}", e.getMessage());
		}
		FileUpload fu = FileUpload.fromData(input, "donate_chat.png");

		MessageEmbed me = buildEmbed();

		Button donateButton = Button.success(Buttons.DONATE_TO_CHAT_ID, Buttons.DONATE_TO_CHAT_LABEL);
		Button backButton = Button.secondary(Buttons.DONATE_TO_MAIN_ID, Buttons.DONATE_TO_MAIN_LABEL);
		
		MessageData data = null;
		if(event instanceof SlashCommandInteractionEvent) {
			if(tokenCount > 0) {
				data = new MessageCreateBuilder()
						.addFiles(fu)	
						.addEmbeds(me)
						.setComponents(
								ActionRow.of(donateButton, backButton)
						)
						.setContent(null)
						.build();
			} else {
				data = new MessageCreateBuilder()
						.addFiles(fu)	
						.addEmbeds(me)
						.setComponents(
								ActionRow.of(backButton)
						)
						.setContent(null)
						.build();
			}

		}
		if(event instanceof ButtonInteractionEvent) {
			if(tokenCount > 0) {
				data = new MessageEditBuilder()
						.setFiles(fu)
						.setEmbeds(me)
						.setComponents(
								ActionRow.of(donateButton, backButton)
						)
						.setContent(null)
						.build();
			} else {
				data = new MessageEditBuilder()
						.setFiles(fu)
						.setEmbeds(me)
						.setComponents(
								ActionRow.of(backButton)
						)
						.setContent(null)
						.build();
			}
		}

		return data;
	}
}
