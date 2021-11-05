package bot.model.gw2;

import java.util.List;

public record Achievement(
    Long id,
    String name,
    List<AchievementBit> bits) {

  public record AchievementBit(
    Long id,
    String type) {}
}
