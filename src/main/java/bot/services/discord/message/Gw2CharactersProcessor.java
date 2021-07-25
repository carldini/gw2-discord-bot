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
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

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

  private final FreeMarkerConfigurer freeMarkerConfigurer;
  private final KeyService keyService;
  private final AccountService accountService;

  public Gw2CharactersProcessor(
      final FreeMarkerConfigurer freeMarkerConfigurer,
      final KeyService keyService,
      final AccountService accountService) {
    this.freeMarkerConfigurer = freeMarkerConfigurer;
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
      if (token.getPermissions().contains("characters")) {
        final List<Character> characters = accountService.getCharacters(apiKey);
        return processResponse(characters, user, requestedBy);
      } else {
        return "Sorry " + requestedBy + ", that api-key doesn't have characters permissions";
      }
    } catch (Exception e) {
      LOGGER.warn("Unable to use apikey for " + user, e);
      return "Sorry " + requestedBy + ", I couldn't validate that api-key, please double check it and try again.";
    }
  }

  private String processResponse(final List<Character> characters, final String user, final String requestedBy) {
    try {
      CharacterModel model = new CharacterModel();
      model.setUser(user);
      model.setCharacters(characters);
      return FreeMarkerTemplateUtils.processTemplateIntoString(
        freeMarkerConfigurer.getConfiguration().getTemplate("characters.ftlh"), model);
    } catch (Exception e) {
      LOGGER.error("unable to format characters for " + user, e);
    }

    // TODO delete the falbback stuff
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

  public class CharacterModel {
    private String user;
    private List<Character> characters;

    public String getUser() {
      return user;
    }
    public void setUser(String user) {
      this.user = user;
    }
    public List<Character> getCharacters() {
      return characters;
    }
    public void setCharacters(List<Character> characters) {
      this.characters = characters;
    }
  }
}
