package bot.services.discord.message;

import java.lang.invoke.MethodHandles;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bot.model.joke.Joke;
import bot.services.discord.JokeService;
import bot.services.discord.MessageProcessor;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

public class JokeMessageProcessor implements MessageProcessor {

  private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private static final String COMMAND = "botdini joke";

  private final JokeService jokeService;

  public JokeMessageProcessor(final JokeService jokeService) {
    this.jokeService = jokeService;
  }

  @Override
  public boolean filter(Message message) {
    return StringUtils.startsWithIgnoreCase(message.getContent(), COMMAND);
  }

  @Override
  public Tuple2<Message, Optional<String>> process(Message message) {
    LOGGER.debug("received {} from {}", message.getContent(), message.getAuthor());

    final Optional<User> author = message.getAuthor();
    if (author.isPresent()) {
      if (StringUtils.equals(author.get().getUsername(), "Poll_")) {
        Tuples.of(message, Optional.of("No Sam... No jokes for you."));
      }
    }

    final Joke joke = jokeService.joke();
    String jokeMessage;

    if (StringUtils.equals(joke.type(), "twopart")) {
      jokeMessage = String.format("""
                                  %s
                                  %s""", joke.setup(), joke.delivery());
    } else {
      jokeMessage = joke.joke();
    }

    return Tuples.of(message, Optional.of(jokeMessage));
  }
  
}
