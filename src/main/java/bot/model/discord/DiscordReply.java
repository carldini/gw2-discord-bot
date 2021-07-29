package bot.model.discord;

import discord4j.core.object.entity.Message;

public class DiscordReply<T> {

  private final Message originalMessage;
  private final T reply;

  public DiscordReply(final Message originalMessage, T reply) {
    this.originalMessage = originalMessage;
    this.reply = reply;
  }

  public Message getOriginalMessage() {
    return originalMessage;
  }

  public T getReply() {
    return reply;
  }

}
