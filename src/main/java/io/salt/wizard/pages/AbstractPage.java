package io.salt.wizard.pages;

import java.util.Map;

import io.vertx.core.json.JsonObject;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.utils.messages.MessageData;

public abstract class AbstractPage {
	protected abstract MessageEmbed buildEmbed();
	protected abstract void setSubMapping();
	protected abstract MessageData buildPage(GenericEvent event);
	public abstract void returnPage(GenericEvent event);
}
