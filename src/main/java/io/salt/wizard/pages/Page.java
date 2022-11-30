package io.salt.wizard.pages;

import io.vertx.core.json.JsonObject;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.utils.messages.MessageData;

public abstract class Page {
	protected abstract MessageEmbed buildEmbed();
	protected abstract MessageData buildPage(GenericEvent event, JsonObject userJson);
	public abstract void returnPage(GenericEvent event, JsonObject userJson);
}
