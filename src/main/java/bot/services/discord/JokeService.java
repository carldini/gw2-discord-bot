package bot.services.discord;

import bot.clients.JokeClient;
import bot.model.joke.Joke;

public class JokeService {

  private final JokeClient jokeClient;

  public JokeService(final JokeClient jokeClient) {
    this.jokeClient = jokeClient;
  }

  public Joke joke() {
    return jokeClient.joke();
  }
}
