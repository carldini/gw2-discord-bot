package bot.services.discord.message;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bot.model.gw2.Account;
import bot.model.gw2.TokenInfo;
import bot.services.discord.MessageProcessor;
import bot.services.gw2.AccountService;
import bot.services.gw2.KeyService;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

public class Gw2AccountProcessor implements MessageProcessor {

  private final static Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final static String COMMAND = "botdini account";
  private final KeyService keyService;
  private final AccountService accountService;

  public Gw2AccountProcessor(final KeyService keyService, final AccountService accountService) {
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
        final Account account = accountService.getAccount(apiKey);
        return processAccount(account, user, requestedBy);
      } else {
        return "Sorry " + requestedBy + ", that api-key doesn't have account permissions";
      }
    } catch (Exception e) {
      LOGGER.warn("Unable tp enrol apikey for " + user, e);
      return "Sorry " + requestedBy + ", I couldn't validate that api-key, please double check it and try again.";
    }
  }

  private String processAccount(final Account account, final String user, final String requestedBy) {
    return StringUtils.join(
      List.of(
        "Discord User: " + user,
        "Name: " + account.getName(),
        "World: " + account.getWorld(),
        "Last Login: " + account.getLastModified()
      ), "\n"
    );
  }
}
