package bot.model.gw2;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonAlias;

public record Account(
    String id,
    String name,
    Long age,
    @JsonAlias("last_modified") String lastModified,
    String created,
    Long world,
    List<String> guilds) {
}
