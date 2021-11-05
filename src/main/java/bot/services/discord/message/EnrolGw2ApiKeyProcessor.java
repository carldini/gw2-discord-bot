package bot.services.discord.message;

import java.lang.invoke.MethodHandles;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bot.model.gw2.TokenInfo;
import bot.services.discord.MessageProcessor;
import bot.services.gw2.KeyService;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

public class EnrolGw2ApiKeyProcessor implements MessageProcessor {

  private final static Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final static String COMMAND = "botdini enrol";
  private final KeyService keyService;

  public EnrolGw2ApiKeyProcessor(final KeyService keyService) {
    this.keyService = keyService;
  }

  @Override
  public boolean filter(Message message) {
    return StringUtils.startsWithIgnoreCase(message.getContent(), COMMAND);
  }

  @Override
  public Tuple2<Message, Optional<String>> process(Message message) {
    LOGGER.debug("received {} from {}", message.getContent(), message.getAuthor());
    return Tuples.of(message, Optional.of(processMessage(message)));
  }

  private String processMessage(Message message) {
    final String trimmedContent = StringUtils.trim(message.getContent());
    final String apiKey = StringUtils.substringAfterLast(trimmedContent, " ");

    final User discordUser = message.getAuthor().get();
    final String user = discordUser.getUsername() + "#" + discordUser.getDiscriminator();
    if (StringUtils.isBlank(apiKey)) {
      return "Sorry " + user + ", I can't see an api-key in this command";
    }
    
    try {
      final TokenInfo token = keyService.addApiKey(user, apiKey);
      return processToken(user, token);
    } catch (Exception e) {
      LOGGER.warn("Unable tp enrol apikey for " + user, e);
      return "Sorry " + user + ", I couldn't validate that api-key, please double check it and try again.";
    }
  }

  private String processToken(final String user, final TokenInfo token) {
    return "Ok " + user + 
        " that token gave me access to see " + 
        StringUtils.join(token.permissions(), ",");
  }
}
