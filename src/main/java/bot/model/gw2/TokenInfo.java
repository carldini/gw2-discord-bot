package bot.model.gw2;

import java.util.List;

public class TokenInfo {

  private String id;
  private List<String> permissions;

  public String getId() {
    return id;
  }
  public void setId(String id) {
    this.id = id;
  }
  public List<String> getPermissions() {
    return permissions;
  }
  public void setPermissions(List<String> permissions) {
    this.permissions = permissions;
  }
}
