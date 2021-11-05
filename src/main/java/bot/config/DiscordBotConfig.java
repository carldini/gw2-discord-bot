package bot.config;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import bot.model.discord.DiscordReply;
import bot.services.discord.JokeService;
import bot.services.discord.MessageProcessor;
import bot.services.discord.message.EnrolGw2ApiKeyProcessor;
import bot.services.discord.message.Gw2AccountProcessor;
import bot.services.discord.message.Gw2CharactersProcessor;
import bot.services.discord.message.HelpMessageProcessor;
import bot.services.discord.message.JokeMessageProcessor;
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
import discord4j.core.spec.EmbedCreateSpec;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;

@Configuration
public class DiscordBotConfig {

  private final static Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  @Value("${discord.token}")
  private String token;

  @Bean
  protected <T extends Event> GatewayDiscordClient gatewayDiscordClient(
      final List<MessageProcessor> processors,
      final Sinks.Many<Message> messagesReceived,
      final Sinks.Many<DiscordReply<List<Consumer<EmbedCreateSpec>>>> embedMessagesToSend) {

    final ReactorResources reactorResources = ReactorResources.builder()
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
      .on(MessageCreateEvent.class)
      .map(MessageCreateEvent::getMessage)
      .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
      .filter(message -> StringUtils.startsWith(message.getContent(), "botdini "))
      .subscribe(
        message -> messagesReceived.tryEmitNext(message),
        error -> LOGGER.error("unable to process received message", error));

    embedMessagesToSend.asFlux()
      .subscribe(reply -> processEmbedMessage(reply));
      
    client.getEventDispatcher()
      .on(ReadyEvent.class)
      .subscribe(event -> {
        final User self = event.getSelf();
        LOGGER.info("Logged in as {}#{}", self.getUsername(), self.getDiscriminator());
      });

    client.onDisconnect().subscribe();
    return client;
  }

  private void processEmbedMessage(
      final DiscordReply<List<Consumer<EmbedCreateSpec>>> reply) {
    reply.originalMessage().getChannel()
      .flatMap(channel -> channel.createMessage(messageSpec -> {
        reply.reply().forEach(embed -> messageSpec.addEmbed(embed));
      }))
      .subscribe(
        event -> LOGGER.debug("event is {}", event),
        error -> LOGGER.error("error is {}", error));
  }

  @Bean
  protected Sinks.Many<Message> messagesReceived() {
    return Sinks.many().replay().all();
  }

  @Bean
  protected Sinks.Many<DiscordReply<List<Consumer<EmbedCreateSpec>>>> embedMessagesToSend() {
    return Sinks.many().replay().all();
  }

  private MessageProcessor getMessageProcessor(
      final Message message,
      final List<MessageProcessor> processors) {
    return processors.stream()
      .filter(processor -> processor.filter(message))
      .findFirst()
      .orElse(new UnhandledMessageProcessor());
  }

  @Bean
  protected MessageProcessor helpMessageProcessor() {
    return new HelpMessageProcessor();
  }

  @Bean
  protected MessageProcessor todoMessageProcessor() {
    return new TodoMessageProcessor();
  }

  @Bean
  protected MessageProcessor enrolMessageprocessor(final KeyService keyService) {
    return new EnrolGw2ApiKeyProcessor(keyService);
  }

  @Bean
  protected Gw2AccountProcessor accountMessageProcessor(
      final KeyService keyService,
      final AccountService accountService,
      final Sinks.Many<Message> messagesReceived,
      final Sinks.Many<DiscordReply<List<Consumer<EmbedCreateSpec>>>> embedMessagesToSend) {
    return new Gw2AccountProcessor(keyService, accountService, messagesReceived, embedMessagesToSend);
  }

  @Bean
  protected Gw2CharactersProcessor charactersMessageProcessor(
      final KeyService keyService,
      final AccountService accountService,
      final Sinks.Many<Message> messagesReceived,
      final Sinks.Many<DiscordReply<List<Consumer<EmbedCreateSpec>>>> embedMessagesToSend) {
    return new Gw2CharactersProcessor(
      keyService,
      accountService,
      messagesReceived,
      embedMessagesToSend);
  }

  @Bean
  protected JokeMessageProcessor jokeMessageProcessor(
      final JokeService jokeService) {
    return new JokeMessageProcessor(jokeService);
  }
}