package bot.services.discord.message;

import java.lang.invoke.MethodHandles;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bot.services.discord.MessageProcessor;
import discord4j.core.object.entity.Message;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

public class UnhandledMessageProcessor implements MessageProcessor {

  private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  @Override
  public boolean filter(Message message) {
    return false;
  }

  @Override
  public Tuple2<Message, Optional<String>> process(Message message) {
    LOGGER.debug("received {} from {}", message.getContent(), message.getAuthor());
    return Tuples.of(message, Optional.ofNullable(null));
  }
  
}
