package bot.model.discord;

import discord4j.core.object.entity.Message;

public record DiscordReply<T>(Message originalMessage, T reply) { }
