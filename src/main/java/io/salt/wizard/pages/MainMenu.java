package io.salt.wizard.pages;

import java.awt.Color;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.salt.wizard.db.UserDAO;
import io.vertx.core.json.JsonObject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.FileUpload;

public class MainMenu {
	private static final Logger _logger = LoggerFactory.getLogger(MainMenu.class);
	
	private final static String NL = "\n";
	private final static String ITEM1 = "**Main Menu**";
	private final static String ITEM2 = "Current User: ";
	
	public static synchronized void returnMainMenu(SlashCommandInteractionEvent event) {
		String header = createHeader(event.getUser());
		
		// Create MessageEmbed
		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle("Main Menu")
		.setColor(Color.pink)
		.setDescription("Main Menu Description")
		.addField("title of field", "test of field", false)
		.addBlankField(false)
		.setImage("attachment://test.png");
		MessageEmbed me = eb.build();

		// Upload file
		InputStream input = null;
		try {
			input = new FileInputStream("src/main/resources/images/test.png");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		FileUpload fu = FileUpload.fromData(input, "test.png");
		
		event.replyEmbeds(me)
			.addFiles(fu)
			.addActionRow(
					Button.primary("page:1", "Click Me"),
					Button.success("page:2", "meh")
					)
			.setEphemeral(true)
			.queue();
		
		//MessageChannelUnion channel = event.getChannel();
		//channel.sendMessage("<@" + event.getUser().getId() + "> test").queue();
		
		return;
	}
	
	private static String createHeader(User _user) {
		String header = "__**MAIN MENU**__\n" + 
				"\n" + 
				"User: " + _user.getAsTag() + "\n" +
				"\n" + 
				"Tokens: " + "\n" + 
				"Donut Count: " + "\n" + 
				"\n";
	
		
		return header;
	}
}
