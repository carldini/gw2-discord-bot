package bot.services.gw2;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

import bot.model.gw2.TokenInfo;

public class KeyService {

  private static final Map<String, String> keys = new ConcurrentHashMap<>();
  private final AccountService accountService;

  public KeyService(final AccountService accountService) {
    this.accountService = accountService;
  }

  @CachePut("key-cache")
  public TokenInfo addApiKey(final String user, final String apiKey) {
    final TokenInfo tokenInfo = tokenInfo(apiKey);
    keys.put(user, apiKey);
    return tokenInfo;
  }

  @Cacheable("key-cache")
  public String retrieveApiKey(final String user) {
    return keys.get(user);
  }

  public TokenInfo tokenInfo(final String apiKey) {
    return accountService.getTokenInfo(apiKey);
  }
}
