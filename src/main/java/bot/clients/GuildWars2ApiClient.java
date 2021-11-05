package bot.clients;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import bot.model.gw2.Account;
import bot.model.gw2.Achievement;
import bot.model.gw2.Character;
import bot.model.gw2.TokenInfo;

@FeignClient(name = "gw2", url = "https://api.guildwars2.com/v2?v=latest")
public interface GuildWars2ApiClient {

  @GetMapping(value = "tokeninfo")
  TokenInfo getTokenInfo(@RequestHeader("Authorization") String authHeader);

  @GetMapping(value = "account")
  Account getAccount(@RequestHeader("Authorization") String authHeader);

  @GetMapping(value = "characters?ids=all")
  List<Character> getCharacters(@RequestHeader("Authorization") String authHeader);

  @GetMapping(value = "account/achievements")
  List<Achievement> getAccountAchievments(@RequestHeader("Authorization") String authHeader);

  @GetMapping(value = "achievements?ids=2646,3012")
  List<Achievement> getLegendaryArmorAchievments(@RequestHeader("Authorization") String authHeader);


  default String createBearer(final String apiKey) {
    return "Bearer " + apiKey;
  }
}
