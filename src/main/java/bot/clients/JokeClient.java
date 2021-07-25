package bot.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import bot.model.joke.Joke;

@FeignClient(name = "joke", url = "https://v2.jokeapi.dev/joke")
public interface JokeClient {

  @GetMapping(value = "Any?blacklistFlags=nsfw,religious,political,racist,sexist,explicit")
  Joke joke();
}
