package bot.controllers;

import java.util.List;

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
public record Gw2Controller(AccountService accountService) {

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
