package bot.model.gw2;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonAlias;

public class Account {

  private String id;
  private String name;
  private Long age;
  @JsonAlias("last_modified")
  private String lastModified;
  private String created;
  private Long world;
  private List<String> guilds;

  public String getId() {
    return id;
  }
  public void setId(String id) {
    this.id = id;
  }
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public Long getAge() {
    return age;
  }
  public void setAge(Long age) {
    this.age = age;
  }
  public String getLastModified() {
    return lastModified;
  }
  public void setLastModified(String lastModified) {
    this.lastModified = lastModified;
  }
  public String getCreated() {
    return created;
  }
  public void setCreated(String created) {
    this.created = created;
  }
  public Long getWorld() {
    return world;
  }
  public void setWorld(Long world) {
    this.world = world;
  }
  public List<String> getGuilds() {
    return guilds;
  }
  public void setGuilds(List<String> guilds) {
    this.guilds = guilds;
  }
}
