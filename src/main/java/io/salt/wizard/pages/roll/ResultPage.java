package io.salt.wizard.pages.roll;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.text.StringSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.salt.wizard.Buttons;
import io.salt.wizard.pages.AbstractPage;
import io.salt.wizard.pages.Page;
import io.vertx.core.json.JsonObject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.callbacks.IMessageEditCallback;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageData;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

public class ResultPage extends Page {
	private final Logger _logger = LoggerFactory.getLogger(this.getClass());
	
	private final String TITLE = "Donut Gachapon";
	private final String DESCRIPTION_ROLL_RESULT = 
		"You received a *${donut}*!" + 
		"\n" + 
		"\n";
	private final String TOKEN_FIELD_TITLE = "Tokens";
	private final String TOKEN_FIELD_DESC = "Current tokens: **${tokens}**";
	
	public ResultPage(JsonObject userJson) {
		super(userJson);
	}
	
	@Override
	protected void setSubMapping() {
		int tokenCount = userJson.getInteger("tokens");
		valuesMap = new HashMap<>();
		valuesMap.put("tokens", tokenCount + "");
	}
	
	protected MessageEmbed buildEmbed(Map<String, String> valuesMap) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle(TITLE)
		.setColor(Color.WHITE)
		.setDescription((new StringSubstitutor(valuesMap)).replace(DESCRIPTION_ROLL_RESULT))
		.addField(TOKEN_FIELD_TITLE, (new StringSubstitutor(valuesMap)).replace(TOKEN_FIELD_DESC), false);
		return eb.build();
	}
	
	protected MessageData buildPage(GenericEvent event, JsonObject userJson) {	
		int tokenCount = userJson.getInteger("tokens");
		
		
		
		Map<String, String> valuesMap = new HashMap<>();
		valuesMap.put("tokens", tokenCount + "");
		valuesMap.put("donut", "");
		
		MessageEmbed me = buildEmbed(valuesMap);
		
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
}
