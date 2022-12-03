package io.salt.wizard.pages;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonObject;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.callbacks.IMessageEditCallback;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageData;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

public class Page extends AbstractPage{
	private final Logger _logger = LoggerFactory.getLogger(this.getClass());
	protected JsonObject userJson;
	protected Map<String, String> valuesMap;
	
	public Page(JsonObject userJson) {
		this.userJson = userJson;
	}
	
	@Override
	protected void setSubMapping() {
		// TODO Auto-generated method stub
	}
	
	@Override
	protected MessageEmbed buildEmbed() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected MessageData buildPage(GenericEvent event) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Creates or edits a page depending on if a slash command or button interaction event is provided.
	 * @param event The slash command or button interaction
	 * @param userJson JSON containing information about the user
	 */
	public void returnPage(GenericEvent event) {
		_logger.trace("Message :: " + event.toString());
		if(event instanceof SlashCommandInteractionEvent) {
			MessageCreateData data = (MessageCreateData) buildPage(event);
			((IReplyCallback) event).reply(data).setEphemeral(true).queue();
		}
		if(event instanceof ButtonInteractionEvent) {
			MessageEditData data = (MessageEditData) buildPage(event);
			((IMessageEditCallback) event).editMessage(data).queue();
		}
	}
}
