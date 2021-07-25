package bot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import bot.clients.JokeClient;
import bot.services.discord.JokeService;
import bot.services.gw2.AccountService;
import bot.services.gw2.KeyService;

@Configuration
public class AppliationConfig {

  @Bean
  public KeyService keyService(final AccountService accountService) {
    return new KeyService(accountService);
  }

  @Bean
  public JokeService jokeService(final JokeClient jokeClient) {
    return new JokeService(jokeClient);
  }
}
