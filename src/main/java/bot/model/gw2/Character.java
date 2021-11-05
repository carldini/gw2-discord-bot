package bot.model.gw2;

import com.fasterxml.jackson.annotation.JsonAlias;

public record Character(
  String name,
  String race,
  String gender,
  String profession,
  Long level,
  String guild,
  Long age,
  @JsonAlias("last_modified") String lastModified,
  String created,
  String deaths) {}
