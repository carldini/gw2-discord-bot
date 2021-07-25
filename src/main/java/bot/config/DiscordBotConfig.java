package bot.config;

import java.lang.invoke.MethodHandles;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import bot.services.discord.MessageProcessor;
import bot.services.discord.message.EnrolGw2ApiKeyProcessor;
import bot.services.discord.message.Gw2AccountProcessor;
import bot.services.discord.message.HelpMessageProcessor;
import bot.services.discord.message.TodoMessageProcessor;
import bot.services.discord.message.UnhandledMessageProcessor;
import bot.services.gw2.AccountService;
import bot.services.gw2.KeyService;
import discord4j.common.ReactorResources;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.Event;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import reactor.core.scheduler.Schedulers;

@Configuration
//@Profile("discord")
public class DiscordBotConfig {

  private final static Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  @Value("${discord.token}")
  private String token;

  @Bean
  public <T extends Event> GatewayDiscordClient gatewayDiscordClient(List<MessageProcessor> processors) {
    ReactorResources reactorResources = ReactorResources.builder()
        .timerTaskScheduler(Schedulers.newParallel("discord-scheduler"))
        .blockingTaskScheduler(Schedulers.boundedElastic())
        .build();

    final GatewayDiscordClient client = DiscordClientBuilder
        .create(token)
        .setReactorResources(reactorResources)
        .build().login().block();

    client.getEventDispatcher()
      .on(MessageCreateEvent.class)
      .map(MessageCreateEvent::getMessage)
      .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
      .map(message -> getMessageProcessor(message, processors).process(message))
      .filter(tuple -> tuple.getT2().isPresent())
      .flatMap(tuple -> tuple.getT1().getChannel().block().createMessage(tuple.getT2().get()))
      .subscribe();
      
    client.getEventDispatcher()
      .on(ReadyEvent.class)
      .subscribe(event -> {
        final User self = event.getSelf();
        LOGGER.info("Logged in as {}#{}", self.getUsername(), self.getDiscriminator());
      });

    client.onDisconnect().subscribe();
    return client;
  }

  private MessageProcessor getMessageProcessor(Message message, final List<MessageProcessor> processors) {
    return processors.stream()
    .filter(processor -> processor.filter(message))
    .findFirst()
    .orElse(new UnhandledMessageProcessor());
  }

  @Bean
  public MessageProcessor helpMessageProcessor() {
    return new HelpMessageProcessor();
  }

  @Bean
  public MessageProcessor todoMessageProcessor() {
    return new TodoMessageProcessor();
  }

   @Bean
   public MessageProcessor enrolMessageprocessor(final KeyService keyService) {
     return new EnrolGw2ApiKeyProcessor(keyService);
   }

   @Bean
   public MessageProcessor accountMessageProcessor(final KeyService keyService, final AccountService accountService) {
     return new Gw2AccountProcessor(keyService, accountService);
   }
}