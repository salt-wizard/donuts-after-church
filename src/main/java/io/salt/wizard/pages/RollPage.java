package io.salt.wizard.pages;

import java.awt.Color;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.salt.wizard.Buttons;
import io.salt.wizard.SnackRoller;
import io.salt.wizard.actions.TokenHandler;
import io.salt.wizard.db.UserDAO;
import io.salt.wizard.db.UserSnacksDAO;
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
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageData;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

public class RollPage {
	private static final Logger _logger = LoggerFactory.getLogger(RollPage.class);
	
	
	
	private static MessageCreateData createRollPage(GenericEvent event, JsonObject userJson) {	
		int tokenCount = userJson.getInteger("tokens");

		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle("Donut Gachapon")
		.setColor(Color.WHITE)
		.setDescription("Welcome to the Donut Gachapon!")
		.appendDescription("\n")
		.appendDescription("\n")
		.appendDescription("Here is where you can submit 1 token for a random donut.")
		.appendDescription("\n")
		.appendDescription("\n")
		.appendDescription("You can also roll for 10 donuts with 10 tokens.")
		.appendDescription("\n")
		.addField("Tokens", "Current tokens: **" + tokenCount + "**", false);
		MessageEmbed me = eb.build();
		
		Button rollButton = Button.success(Buttons.ROLL_GET_DONUT_ID, Buttons.ROLL_GET_DONUT_LABEL);
		Button roll50Button = Button.success("AA", "Spend 40 Tokens");
		Button cancelButton = Button.secondary("A", "Cancel");
		
		MessageCreateData data = null;
		if(userJson.getInteger("tokens") > 39) {
			data = new MessageCreateBuilder()
					.addEmbeds(me)
					.addActionRow(rollButton, roll50Button, cancelButton)
					.build();
		} else {
			data = new MessageCreateBuilder()
					.addEmbeds(me)
					.addActionRow(rollButton, cancelButton)
					.build();
		}

		return data;
	}
	
	private static MessageEditData editDebugPage(GenericEvent event, JsonObject userJson) {	
		int tokenCount = userJson.getInteger("tokens");
		
		String donuts = getUserSnacks(userJson);
		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle("Donut Gachapon")
		.setColor(Color.WHITE)
		.setDescription("Welcome to the Donut Gachapon!")
		
		.addField("Tokens", "Current tokens: **" + tokenCount + "**", false)
		.addField("Donuts", donuts, false);
		//.addBlankField(false);
		MessageEmbed me = eb.build();
		
		MessageEditData data = new MessageEditBuilder()
			.setEmbeds(me)
			.build();

		return data;
	}
	
	
	
	private static String getUserSnacks(JsonObject userJson) {
		JsonArray userSnacks = UserSnacksDAO.selectUserSnacksById(userJson.getLong("userId"));
		StringBuilder donuts = new StringBuilder();
		
		JsonArray fullSnackList = SnackRoller.getFullUserSnacks(userSnacks);
		
		fullSnackList.forEach(obj -> {
			JsonObject userSnack = (JsonObject) obj;
			donuts.append(userSnack.getString("snackName") + ": " + userSnack.getInteger("quantity") + "\n");
		});
		return donuts.toString();
	}
	
	
	public static void returnRollPage(SlashCommandInteractionEvent event, JsonObject userJson) {
		MessageCreateData data = createRollPage(event, userJson);
		event.reply(data).setEphemeral(true).queue();
		
		//MessageChannelUnion channel = event.getChannel();
		//channel.sendMessage("<@" + event.getUser().getId() + "> test").queue();
	}
	
	public static void incrementToken(ButtonInteractionEvent event, JsonObject userJson) {
		userJson = TokenHandler.incrementToken(userJson, 1);
		MessageEditData data = editDebugPage(event, userJson);
		event.editMessage(data).queue();
	}
	
	public static void decrementToken(ButtonInteractionEvent event, JsonObject userJson) {
		userJson = TokenHandler.decrementToken(userJson, 1);
		MessageEditData data = editDebugPage(event, userJson);
		event.editMessage(data).queue();
	}
	
	public static void increment10Tokens(ButtonInteractionEvent event, JsonObject userJson) {
		userJson = TokenHandler.incrementToken(userJson, 10);
		MessageEditData data = editDebugPage(event, userJson);
		event.editMessage(data).queue();
	}
	
	public static void decrement10Tokens(ButtonInteractionEvent event, JsonObject userJson) {
		userJson = TokenHandler.decrementToken(userJson, 10);
		MessageEditData data = editDebugPage(event, userJson);
		event.editMessage(data).queue();
	}
	
	public static void rollForDonutDebug(ButtonInteractionEvent event, JsonObject userJson) {
		JsonObject snack = SnackRoller.rollForSnack();
		_logger.debug("Rolled snack :: {}", snack.encodePrettily());
		long userId = userJson.getLong("userId");
		int snackId = snack.getInteger("snackId");
		
		int snackQuantity = SnackRoller.getSnackQuantity(userId, snackId);
		userJson = UserSnacksDAO.updateSnackQuantity(userId, snackId, snackQuantity + 1);

		MessageEditData data = editDebugPage(event, userJson);
		event.editMessage(data).queue();
	}
}