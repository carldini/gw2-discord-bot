package bot.model.gw2;

import java.util.List;

public record AccountAchievement(
  Long id,
  List<Long> bits,
  Long current,
  Long max,
  Boolean done,
  Boolean unlocked) {}
