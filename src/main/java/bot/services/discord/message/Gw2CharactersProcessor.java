package bot.services.discord.message;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bot.model.gw2.Character;
import bot.model.gw2.TokenInfo;
import bot.services.discord.MessageProcessor;
import bot.services.gw2.AccountService;
import bot.services.gw2.KeyService;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

public class Gw2CharactersProcessor implements MessageProcessor {

  private final static Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final static String COMMAND = "botdini characters";
  private final KeyService keyService;
  private final AccountService accountService;

  public Gw2CharactersProcessor(final KeyService keyService, final AccountService accountService) {
    this.keyService = keyService;
    this.accountService = accountService;
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
    final User discordUser = message.getAuthor().get();
    final String requestedBy = discordUser.getUsername() + "#" + discordUser.getDiscriminator();
    final String user = requestedBy;
    final String apiKey = keyService.retrieveApiKey(user);

    if (StringUtils.isBlank(apiKey)) {
      return "Sorry " + requestedBy + ", I don't have an api key for " + user + ", ask them to enrol?";
    }
    
    try {
      final TokenInfo token = accountService.getTokenInfo(apiKey);
      if (token.getPermissions().contains("account")) {
        final List<Character> characters = accountService.getCharacters(apiKey);
        return processResponse(characters, user, requestedBy);
      } else {
        return "Sorry " + requestedBy + ", that api-key doesn't have account permissions";
      }
    } catch (Exception e) {
      LOGGER.warn("Unable tp enrol apikey for " + user, e);
      return "Sorry " + requestedBy + ", I couldn't validate that api-key, please double check it and try again.";
    }
  }

  private String processResponse(final List<Character> characters, final String user, final String requestedBy) {
    final List<String> topLevel = new ArrayList<>();
    topLevel.add("Discord User: " + user);
    CollectionUtils.addAll(
      topLevel,
      characters.stream()
          .map(character -> processCharacter(character))
          .collect(Collectors.toList())
    );
    return StringUtils.join(topLevel, "\n\n");
  }

  private String processCharacter(Character character) {
    return StringUtils.join(
      List.of(
        "  Name: " + character.getName(),
        "  Profession: " + character.getProfession(),
        "  Race: " + character.getRace(),
        "  Level: " + character.getLevel(),
        "  Last Login: " + character.getLastModified(),
        "  Created On: " + character.getCreated(),
        "  Deaths: " + character.getDeaths()
      ), "\n"
    );
  }
}
