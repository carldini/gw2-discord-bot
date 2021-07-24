package bot.controllers;

import java.lang.invoke.MethodHandles;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import bot.model.gw2.Account;
import bot.model.gw2.Character;
import bot.model.gw2.TokenInfo;
import bot.services.gw2.AccountService;

@RestController
@RequestMapping("gw2")
public class Gw2Controller {

  private final static Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final AccountService accountService;

  public Gw2Controller(final AccountService accountService) {
    this.accountService = accountService;
    LOGGER.info("Setting up {}", this.getClass());
  }

  @GetMapping("token")
  public TokenInfo tokenInfo(@RequestParam(name = "token") final String apiKey) {
    return accountService.getTokenInfo(apiKey);
  }

  @GetMapping("account")
  public Account account(@RequestParam(name = "token") final String apiKey) {
    return accountService.getAccount(apiKey);
  }

  @GetMapping("characters")
  public List<Character> characters(@RequestParam(name = "token") final String apiKey) {
    return accountService.getCharacters(apiKey);
  }
}
