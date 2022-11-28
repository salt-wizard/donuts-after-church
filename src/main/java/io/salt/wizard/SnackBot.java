package io.salt.wizard;

import java.awt.Color;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.salt.wizard.actions.TokenHandler;
import io.salt.wizard.db.HikariCPProvider;
import io.salt.wizard.db.SnacksDAO;
import io.salt.wizard.db.UserDAO;
import io.salt.wizard.db.UserSnacksDAO;
import io.salt.wizard.pages.DebugPage;
import io.salt.wizard.pages.RollPage;
import io.salt.wizard.pages.MainMenu;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class SnackBot extends ListenerAdapter {
	private final static String TOKEN_LOCATION = "./tokens/tokens.json";
	private static final Logger _logger = LoggerFactory.getLogger(SnackBot.class);
	private static Properties _properties;

	/**
	 * Main method. Initialize database connects, prepare configuration, and start bot
	 */
	public static void main(String[] args) {
		_logger.info("Initializing bot...");
		
		// Make connection to the database through HikariCP datasource
		_logger.info("Establishing database connectivity...");
		try(InputStream input = new FileInputStream("dbconfig/hikaricp.properties")){
			Properties prop = new Properties();
			prop.load(input);
			HikariCPProvider.init(prop);
		} catch (IOException ex) {
			_logger.error("Error occurred when trying to open properties for HikariCP :: {}", ex.getMessage());
			return;
		}
		
		// Pull the list of snacks from the database
		JsonArray snacks = SnacksDAO.returnAllSnacks();
		if(snacks == null || snacks.size() < 1) {
			_logger.error("Unable to pull sacks back. Shutting app down.");
			return;
		}
		_logger.debug("List of snacks :: {}", snacks.encodePrettily());
		SnackRoller.init(snacks);
		
		// Begin the connectivity to Discord
		try {
			_logger.info("Beginning connectivity to Discord...");
			JsonObject tokens = new JsonObject(IOUtils.toString(new FileReader(TOKEN_LOCATION)));
			final String PRIVATE_TOKEN = tokens.getString("privateToken");
			
			_properties = new Properties();
			_properties.load(SnackBot.class.getClassLoader().getResourceAsStream("app-version.properties"));
			
			// Create the intents for the bot
			Collection<GatewayIntent> gatewayIntents = new ArrayList<GatewayIntent>();
			gatewayIntents.add(GatewayIntent.GUILD_MESSAGES);
			gatewayIntents.add(GatewayIntent.MESSAGE_CONTENT);
			
			_logger.info("Setting up following GatewayIntents :: {}", gatewayIntents.toString());
			
			// Set up and build the bot
			JDA jda = JDABuilder
					.createDefault(PRIVATE_TOKEN)
					.addEventListeners(new SnackBot())
					.setActivity(Activity.competing(" snack collecting."))
					.enableIntents(gatewayIntents)
					.setStatus(OnlineStatus.ONLINE)
					.build();
			
			jda.upsertCommand(SlashCommands.MENU, "This bot returns info").queue();
			jda.upsertCommand(SlashCommands.ROLL, "Give a token for a random donut.").queue();
			jda.upsertCommand(SlashCommands.CREDITS, "Return credits").queue();
			jda.upsertCommand("debug", "Debug menu - Remove later").queue();
			
		} catch (FileNotFoundException e) {
			_logger.error("Unable to find file :: {}", e.getMessage());
		} catch (IOException e) {
			_logger.error("Encountered IO error with file :: {}", e.getMessage());
		}
	}
	
	/**
	 * Handle slash commands from users.
	 */
	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
		User _user = event.getUser();
		
		JsonObject userDetails = validateUser(_user);

		switch(event.getName()) {
			case "debug":
				DebugPage.returnDebugPage(event, userDetails);
				break;
			case SlashCommands.MENU:
				MainMenu.returnMainMenu(event);
				break;
			case SlashCommands.ROLL:
				RollPage.returnRollPage(event, userDetails);
				break;
			case SlashCommands.CREDITS:
				String author = "Author: salt_wizard#1029";
				String version = "Version: " + _properties.getProperty("app.version");
				event.reply(author + "\n" + version)
					.setEphemeral(true)
					.queue();
				break;
			default:
				return;
		}
	}
	
	@Override
	public void onButtonInteraction(ButtonInteractionEvent event) {
		// Return user making interaction
		JsonObject userJson = validateUser(event.getUser());
		
		switch(event.getComponentId()) {
			// Debug Page
			case Buttons.DEBUG_INC_TOKEN_ID:
				DebugPage.incrementToken(event, userJson);
				break;
			case Buttons.DEBUG_DEC_TOKEN_ID:
				DebugPage.decrementToken(event, userJson);
				break;	
			case Buttons.DEBUG_INC_10_TOKEN_ID:
				DebugPage.increment10Tokens(event, userJson);
				break;
			case Buttons.DEBUG_DEC_10_TOKEN_ID:
				DebugPage.decrement10Tokens(event, userJson);
				break;
			case Buttons.DEBUG_ROLL_DONUT_ID:
				DebugPage.rollForDonutDebug(event, userJson);
				break;

			// Roll Page
				
				
			default:
				return;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private JsonObject validateUser(User _user) {
		// Check if the user already exists in the database
		_logger.trace("Retrieving details for user {}...", _user.getAsTag());
		JsonObject userJson = UserDAO.selectUserById(_user.getIdLong());
		if(userJson != null) {
			_logger.trace("User {} has the following snack ID :: {}", _user.getAsTag(), userJson.encodePrettily());
		}
		
		// If the user does not exist, create a new entry in the USERS table
		if(userJson != null && userJson.isEmpty()) {
			_logger.trace("User does not have an existing entry in the DB!");
			_logger.info("Creating new entry for user {}...", _user.getAsTag());
			userJson = UserDAO.addNewUser(_user);
			_logger.info("New user :: {}", userJson.encodePrettily());
			_logger.info("Creating snack rows for user...");
			UserSnacksDAO.addNewUserSnacks(userJson.getLong("userId"), SnackRoller.returnSnacks());
			
		}

		// If there is a null, return a null
		if(userJson == null) {
			_logger.error("There was an error trying to obtain the snackId for user {}.",
					_user.getAsTag());
			return null;
		} 
		
		return userJson;
	}
}
