package io.salt.wizard.pages.roll;

import java.awt.Color;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.text.StrSubstitutor;
import org.apache.commons.text.StringSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.salt.wizard.Buttons;
import io.salt.wizard.SnackRoller;
import io.salt.wizard.actions.UserHandler;
import io.salt.wizard.db.UserDAO;
import io.salt.wizard.db.UserSnacksDAO;
import io.salt.wizard.pages.Page;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.Message.Interaction;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.entities.emoji.CustomEmoji;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.callbacks.IMessageEditCallback;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageData;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

public class RollPage extends Page {
	private final Logger _logger = LoggerFactory.getLogger(this.getClass());
	
	private final String TITLE = "Donut Gachapon";
	private final String DESCRIPTION_MAIN = 
		"Welcome to the Donut Gachapon!" +
		"\n" + 
		"\n" +
		"Here is where you can submit 1 token for a random donut.";
	
	private final String DESCRIPTION_ROLL_RESULT = 
		"You received a *${donut}*!" + 
				"\n" + 
				"\n";
	
	private final String TOKEN_FIELD_TITLE = "Tokens";
	private final String TOKEN_FIELD_DESC = "Current tokens: **${tokens}**";
	
	public RollPage(JsonObject userJson) {
		super(userJson);
	}
	
	@Override
	protected void setSubMapping() {
		int tokenCount = userJson.getInteger("tokens");
		valuesMap = new HashMap<>();
		valuesMap.put("tokens", tokenCount + "");
	}
	
	/**
	 * Constructs the embed
	 * @return
	 */
	protected MessageEmbed buildEmbed(Map<String, String> valuesMap) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle(TITLE)
		.setColor(Color.WHITE)
		.setDescription(DESCRIPTION_MAIN)
		.addField(TOKEN_FIELD_TITLE, (new StringSubstitutor(valuesMap)).replace(TOKEN_FIELD_DESC), false);
		return eb.build();
	}
	
	protected MessageData buildPage(GenericEvent event) {	
		int tokenCount = userJson.getInteger("tokens");
		
		setSubMapping();
		MessageEmbed me = buildEmbed(valuesMap);
		
		Button rollButton = Button.success(Buttons.ROLL_GET_DONUT_ID, Buttons.ROLL_GET_DONUT_LABEL);
		Button cancelButton = Button.secondary(Buttons.ROLL_TO_MAIN_ID, Buttons.ROLL_TO_MAIN_LABEL);
		
		MessageData data = null;
		if(event instanceof SlashCommandInteractionEvent) {
			if(tokenCount > 0) {
				data = new MessageCreateBuilder()
						.addEmbeds(me)
						.addActionRow(rollButton, cancelButton)
						.build();
			} else {
				data = new MessageCreateBuilder()
						.addEmbeds(me)
						.addActionRow(cancelButton)
						.build();
			}
		}
		if(event instanceof ButtonInteractionEvent) {
			if(tokenCount > 0) {
				data = new MessageEditBuilder()
						.setEmbeds(me)
						.setActionRow(rollButton, cancelButton)
						.build();
			} else {
				data = new MessageEditBuilder()
						.setEmbeds(me)
						.setActionRow(cancelButton)
						.build();
			}
		}

		return data;
	}
	
	private MessageEditData createRollResultPage(GenericEvent event, JsonObject userJson, String donut) {	
		int tokenCount = userJson.getInteger("tokens");
		Map<String, String> valuesMap = new HashMap<>();
		valuesMap.put("tokens", tokenCount + "");
		valuesMap.put("donut", donut);
		
		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle(TITLE)
		.setColor(Color.WHITE)
		.setDescription((new StringSubstitutor(valuesMap)).replace(DESCRIPTION_ROLL_RESULT))
		.addField(TOKEN_FIELD_TITLE, (new StringSubstitutor(valuesMap)).replace(TOKEN_FIELD_DESC), false);
		MessageEmbed me = eb.build();
		
		MessageEditData data = null;
		if(tokenCount > 0) {
			data = new MessageEditBuilder()
					.setEmbeds(me)
					.build();
		} else {
			data = new MessageEditBuilder()
					.setEmbeds(me)
					.build();
		}

		return data;
	}
	


	
	private String getUserSnacks(JsonObject userJson) {
		JsonArray userSnacks = UserSnacksDAO.selectUserSnacksById(userJson.getLong("userId"));
		StringBuilder donuts = new StringBuilder();
		
		JsonArray fullSnackList = SnackRoller.getFullUserSnacks(userSnacks);
		
		fullSnackList.forEach(obj -> {
			JsonObject userSnack = (JsonObject) obj;
			donuts.append(userSnack.getString("snackName") + ": " + userSnack.getInteger("quantity") + "\n");
		});
		return donuts.toString();
	}
	
	public void rollForDonut(ButtonInteractionEvent event) {
		int tokenCount = userJson.getInteger("tokens");
		
		int snackId = -1;
		if(tokenCount > 0) {
			// Decrement token, roll for snack
			userJson = UserHandler.decrementToken(userJson, 1);
			JsonObject snack = SnackRoller.rollForSnack();

			// Update the donut count
			long userId = userJson.getLong("userId");
			snackId = snack.getInteger("snackId");
			int snackQuantity = SnackRoller.getSnackQuantity(userId, snackId);
			userJson = UserSnacksDAO.updateSnackQuantity(userId, snackId, snackQuantity + 1);
		}

		// Update Buttons
		Button rollButton = Button.success(Buttons.ROLL_AGAIN_DONUT_ID, Buttons.ROLL_AGAIN_DONUT_LABEL);
		Button cancelButton = Button.secondary(Buttons.ROLL_TO_MAIN_ID, Buttons.ROLL_TO_MAIN_LABEL);
		
		MessageEditData data = null;
		if(snackId < 0) {
			data = createRollResultPage(event, userJson, "null");
		} else {
			data = createRollResultPage(event, userJson, SnackRoller.getSnackName(snackId));
		}
		
		if(userJson.getInteger("tokens") > 0) {
			event.editMessage(data).setActionRow(rollButton, cancelButton).queue();
		} else {
			event.editMessage(data).setActionRow(cancelButton).queue();
		}
	}
}