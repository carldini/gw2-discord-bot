package bot.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import bot.clients.GuildWars2ApiClient;
import bot.services.gw2.AccountService;

@Configuration
@EnableFeignClients(basePackageClasses = GuildWars2ApiClient.class)
public class ApiClientConfig {

  @Bean
  protected AccountService accountService(GuildWars2ApiClient client) {
    return new AccountService(client);
  } 

}
