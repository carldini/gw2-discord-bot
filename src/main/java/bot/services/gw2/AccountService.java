package bot.services.gw2;

import java.util.List;

import bot.clients.GuildWars2ApiClient;
import bot.model.gw2.Account;
import bot.model.gw2.Character;
import bot.model.gw2.TokenInfo;

public record AccountService(GuildWars2ApiClient client) {

  public TokenInfo getTokenInfo(final String apiKey) {
    return client.getTokenInfo(client.createBearer(apiKey));
  }

  public Account getAccount(final String apiKey) {
    return client.getAccount(client.createBearer(apiKey));
  }

  public List<Character> getCharacters(final String apiKey) {
    return client.getCharacters(client.createBearer(apiKey));
  }
}
