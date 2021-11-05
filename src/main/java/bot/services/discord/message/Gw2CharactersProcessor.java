package bot.services.discord.message;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bot.model.discord.DiscordReply;
import bot.model.gw2.Character;
import bot.model.gw2.TokenInfo;
import bot.services.gw2.AccountService;
import bot.services.gw2.KeyService;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;
import reactor.core.publisher.Sinks;
import reactor.core.publisher.Sinks.EmitResult;

public class Gw2CharactersProcessor {

  private final static Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final static String COMMAND = "botdini characters";

  private final KeyService keyService;
  private final AccountService accountService;
  private final Sinks.Many<Message> messagesReceived;
  private final Sinks.Many<DiscordReply<List<Consumer<EmbedCreateSpec>>>> embedMessagesToSend;

  public Gw2CharactersProcessor(
      final KeyService keyService,
      final AccountService accountService,
      final Sinks.Many<Message> messagesReceived,
      final Sinks.Many<DiscordReply<List<Consumer<EmbedCreateSpec>>>> embedMessagesToSend) {
    this.keyService = keyService;
    this.accountService = accountService;
    this.messagesReceived = messagesReceived;
    this.embedMessagesToSend = embedMessagesToSend;

    this.messagesReceived.asFlux()
        .filter(message -> filter(message))
        .subscribe(message -> process(message));
  }

  public boolean filter(Message message) {
    return StringUtils.startsWithIgnoreCase(message.getContent(), COMMAND);
  }

  public void process(Message message) {
    LOGGER.debug("received {} from {}", message.getContent(), message.getAuthor());
    final User discordUser = message.getAuthor().get();
    final String requestedBy = discordUser.getUsername() + "#" + discordUser.getDiscriminator();
    final List<Consumer<EmbedCreateSpec>> characters = getCharacters(requestedBy).stream().map(character -> {
      final Consumer<EmbedCreateSpec> template = spec -> {
        spec.setTitle(character.name());
        spec.setColor(getColor(character));
        spec.addField("Profession", getGoofyProfession(character), true);
        spec.addField("Level", String.valueOf(character.level()), true);
        spec.addField("Deaths", character.deaths(), true);
        spec.addField("Created On", character.created(), true);
        spec.addField("Last Login", character.lastModified(), true);
        spec.setThumbnail(getProfessionImage(character));
      };
      return template;
    }).collect(Collectors.toList());
    final DiscordReply<List<Consumer<EmbedCreateSpec>>> reply = new DiscordReply<List<Consumer<EmbedCreateSpec>>>(message, characters);
    final EmitResult tryEmitNext = embedMessagesToSend.tryEmitNext(reply);
    LOGGER.debug("Emit succeeded", tryEmitNext.isSuccess());
  }

  private List<Character> getCharacters(final String user) {
    final String apiKey = keyService.retrieveApiKey(user);

    if (StringUtils.isBlank(apiKey)) {
      throw new RuntimeException("Sorry, I don't have an api key for " + user + ", ask them to enrol?");
    }
    
    try {
      final TokenInfo token = accountService.getTokenInfo(apiKey);
      if (token.permissions().contains("characters")) {
        return accountService.getCharacters(apiKey);
      }
      throw new RuntimeException("Sorry, the api-key for " + user + " doesn't have characters permissions");
    } catch (Exception e) {
      LOGGER.warn("Unable to use apikey for " + user, e);
      throw new RuntimeException("Sorry, I couldn't validate the api-key, please double check it and try again.");
    }
  }

  private Color getColor(final Character character) {
    switch (character.profession()) {
      case "Mesmer":
        return Color.of(180, 121, 224);
      case "Engineer":
        return Color.of(130, 83, 33);
      case "Revenant":
        return Color.of(201, 105, 95);
      case "Ranger":
        return Color.of(153, 189, 94);
      case "Guardian":
        return Color.of(114, 193, 219);
      case "Elementalist":
        return Color.of(227, 100, 127);
      case "Thief":
        return Color.of(94, 90, 91);
      case "Necromancer":
        return Color.of(54, 107, 58);
      case "Warrior":
        return Color.of(224, 190, 85);
      default:
        return Color.DISCORD_BLACK;
    }
  }

  private String getGoofyProfession(Character character) {
    if (StringUtils.equals(character.profession(), "Mesmer")) {
      if (! StringUtils.equals(character.name(), "Carldini")) {
        return "2nd Rate Mesmer";
      }
    }
    return character.profession();
  }

  private String getProfessionImage(Character character) {
    if (StringUtils.equals(character.profession(), "Mesmer")) {
      if (! StringUtils.equals(character.name(), "Carldini")) {
        return "https://static.staticwars.com/quaggans/lost.jpg";
      }
    }

    if (StringUtils.equals(character.name(), "Rarakablah")) {
      return "https://render.guildwars2.com/file/BAF43C0425BA631B9C49E771F9EB16E32C1E57E1/1203237.png";
    }

    switch (character.profession()) {
      case "Mesmer":
        return "https://render.guildwars2.com/file/AF61567E16A83F145D6FB35D63BF01074A3A5AB9/156635.png";
      case "Engineer":
        return "https://render.guildwars2.com/file/A94D00911BD47CDE39A104F90C7D07DE623554ED/156631.png";
      case "Revenant":
        return "https://render.guildwars2.com/file/696A48DD61EE01FD1F4FBBBDB82D74611E04EA39/965717.png";
      case "Ranger":
        return "https://render.guildwars2.com/file/FEF2479DC197D40758A8D6E95201F4A7996EB357/156639.png";
      case "Guardian":
        return "https://render.guildwars2.com/file/6E0D0AC6E0CE5C0C29B3D736ABEA070F4A58540E/156633.png";
      case "Elementalist":
        return "https://render.guildwars2.com/file/BBED46EB20C80D0DDE0F99402493C7E6FFAE1530/156629.png";
      case "Thief":
        return "https://render.guildwars2.com/file/13A2C0EF23F23FF2084875629465279DDA807E3D/103581.png";
      case "Necromancer":
        return "https://render.guildwars2.com/file/CA5A4E96080FCF057C9DA0ED35C693477580421C/156637.png";
      case "Warrior":
        return "https://render.guildwars2.com/file/0A76324239946B79C061762095FAB2BDF7A1D8D7/156642.png";
      default:
        return "https://static.staticwars.com/quaggans/404.jpg";
    }
  }
}
