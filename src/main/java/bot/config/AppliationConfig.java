package bot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import bot.clients.JokeClient;
import bot.services.discord.JokeService;
import bot.services.gw2.AccountService;
import bot.services.gw2.KeyService;

@Configuration
public class AppliationConfig {

  @Bean
  protected KeyService keyService(final AccountService accountService) {
    return new KeyService(accountService);
  }

  @Bean
  protected JokeService jokeService(final JokeClient jokeClient) {
    return new JokeService(jokeClient);
  }

  @Bean
  protected FreeMarkerConfigurer freeMarkerConfigurer() {
    final FreeMarkerConfigurer freeMarkerConfigurer = new FreeMarkerConfigurer();
    freeMarkerConfigurer.setTemplateLoaderPath("classpath:templates");
    return freeMarkerConfigurer;
  }
}
