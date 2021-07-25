package bot.services.discord;

import java.util.Optional;

import discord4j.core.object.entity.Message;
import reactor.util.function.Tuple2;

public interface MessageProcessor {

  boolean filter(Message message);
  Tuple2<Message, Optional<String>> process(Message message);
  
}
