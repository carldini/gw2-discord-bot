package bot.services.discord.message;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bot.model.discord.DiscordReply;
import bot.model.gw2.Account;
import bot.model.gw2.TokenInfo;
import bot.services.gw2.AccountService;
import bot.services.gw2.KeyService;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.spec.EmbedCreateSpec;
import reactor.core.publisher.Sinks;
import reactor.core.publisher.Sinks.EmitResult;

public class Gw2AccountProcessor {

  private final static Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final static String COMMAND = "botdini account";
  private final KeyService keyService;
  private final AccountService accountService;
  private final Sinks.Many<Message> messagesReceived;
  private final Sinks.Many<DiscordReply<List<Consumer<EmbedCreateSpec>>>> embedMessagesToSend;

  public Gw2AccountProcessor(
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
    LOGGER.debug("Received {} from {}", message.getContent(), message.getAuthor());
    final User discordUser = message.getAuthor().get();
    final String requestedBy = discordUser.getUsername() + "#" + discordUser.getDiscriminator();
    final Consumer<EmbedCreateSpec> template = spec -> {
      spec.setAuthor(
        requestedBy, null, discordUser.getAvatarUrl());
      spec.setTitle("Guild Wars 2 Account");
      try {
        final Account account = getAccount(requestedBy);
        spec.addField("Name", account.getName(), false);
        spec.addField("World", String.valueOf(account.getWorld()), false);
        spec.addField("Created On", account.getCreated(), false);
        spec.addField("Last Login", account.getLastModified(), false);
      } catch (Exception e) {
        spec.addField("Error", e.getMessage(), false);
      }
    };
    final DiscordReply<List<Consumer<EmbedCreateSpec>>> reply = new DiscordReply<List<Consumer<EmbedCreateSpec>>>(message, List.of(template));
    final EmitResult tryEmitNext = embedMessagesToSend.tryEmitNext(reply);
    LOGGER.debug("Emit succeeded", tryEmitNext.isSuccess());
  }

  private Account getAccount(final String user) {
    final String apiKey = keyService.retrieveApiKey(user);

    if (StringUtils.isBlank(apiKey)) {
      throw new RuntimeException("Sorry, I don't have an api key for " + user + ", ask them to enrol?");
    }
    
    try {
      final TokenInfo token = accountService.getTokenInfo(apiKey);
      if (token.getPermissions().contains("account")) {
        return accountService.getAccount(apiKey);
      }
      throw new RuntimeException("Sorry, the api-key for " + user + " doesn't have account permissions");
    } catch (Exception e) {
      LOGGER.warn("Unable to use apikey for " + user, e);
      throw new RuntimeException("Sorry, I couldn't validate the api-key, please double check it and try again.");
    }
  }

}
