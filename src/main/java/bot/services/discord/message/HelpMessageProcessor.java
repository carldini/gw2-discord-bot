package bot.services.discord.message;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bot.services.discord.MessageProcessor;
import discord4j.core.object.entity.Message;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

public class HelpMessageProcessor implements MessageProcessor {

  private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private static final String COMMAND = "botdini help";

  @Override
  public boolean filter(Message message) {
    return StringUtils.startsWithIgnoreCase(message.getContent(), COMMAND);
  }

  @Override
  public Tuple2<Message, Optional<String>> process(Message message) {
    LOGGER.debug("received {} from {}", message.getContent(), message.getAuthor());
    final String response = StringUtils.join(
      List.of(
        "Valid commands are:",
        "- help (this command)",
        "- todo",
        "- enrol <api-key> (You should do this in a direct message, not a group)",
        "- account",
        "- characters",
        "- joke"
      ), "\n");
    return Tuples.of(message, Optional.of(response));
  }
  
}
