package bot.config;

import java.lang.invoke.MethodHandles;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import bot.services.discord.EventListener;
import bot.services.discord.MessageCreateListener;
import bot.services.discord.MessageUpdateListener;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.Event;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.object.entity.User;

@Configuration
//@Profile("discord")
public class DiscordBotConfig {

  private final static Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  @Value("${discord.token}")
  private String token;

  @Bean
  public <T extends Event> GatewayDiscordClient gatewayDiscordClient(List<EventListener<T>> eventListeners) {
    GatewayDiscordClient client = DiscordClientBuilder.create(token).build().login().block();

    for (EventListener<T> listener : eventListeners) {
      client.on(listener.getEventType())
        .flatMap(listener::execute)
        .onErrorResume(listener::handleError)
        .subscribe();
    }

    client.getEventDispatcher().on(ReadyEvent.class)
      .subscribe(event -> {
        final User self = event.getSelf();
        LOGGER.info("Logged in as {}#{}", self.getUsername(), self.getDiscriminator());
      });

    return client;
  }

  @Bean
  public MessageCreateListener messageCreateListener() {
    return new MessageCreateListener();
  }

  @Bean
  public MessageUpdateListener messageUpdateListener() {
    return new MessageUpdateListener();
  }
}