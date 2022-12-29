package io.salt.wizard.pages;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.text.StringSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.salt.wizard.DonutEmojis;
import io.vertx.core.json.JsonObject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.emoji.UnicodeEmoji;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.callbacks.IMessageEditCallback;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageData;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import net.dv8tion.jda.internal.entities.emoji.UnicodeEmojiImpl;
import net.dv8tion.jda.internal.requests.Route.Emojis;

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
			IReplyCallback callback = (IReplyCallback) event;
			callback.deferReply(true).queue();
			MessageCreateData data = (MessageCreateData) buildPage(event);
			callback.getHook().setEphemeral(true).sendMessage(data).queue();
		}
		
		if(event instanceof ButtonInteractionEvent) {
			IMessageEditCallback callback = (IMessageEditCallback) event;
			
			// Return ID and defer message
			long messageId = ((ButtonInteractionEvent) event).getMessageIdLong();
			callback.deferEdit().queue();
			
			// Update the embedded to have disabled buttons and a loading wheel to indicate a page is loading.
			MessageEditBuilder eb = new MessageEditBuilder();
			eb.setContent(DonutEmojis.loading + "Loading...");	
			List<ActionRow> rows = ((ButtonInteractionEvent) event).getMessage().getActionRows();
			List<LayoutComponent> list = new ArrayList<LayoutComponent>();
			for(ActionRow row : rows) {
				list.add(row.asDisabled());
			}
			eb.setComponents(list);
			MessageEditData wait = eb.build();
			callback.getHook().editMessageById(messageId, wait).queue();
			
			// Provide the completed edit afterwards.
			MessageEditData data = (MessageEditData) buildPage(event);
			callback.getHook().editMessageById(messageId, data).queue();
		}
	}
}
