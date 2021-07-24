package bot.model.gw2;

import com.fasterxml.jackson.annotation.JsonAlias;

public class Character {

  private String name;
  private String race;
  private String gender;
  private String profession;
  private Long level;
  private String guild;
  private Long age;
  @JsonAlias("last_modified")
  private String lastModified;
  private String created;
  private String deaths;

  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public String getRace() {
    return race;
  }
  public void setRace(String race) {
    this.race = race;
  }
  public String getGender() {
    return gender;
  }
  public void setGender(String gender) {
    this.gender = gender;
  }
  public String getProfession() {
    return profession;
  }
  public void setProfession(String profession) {
    this.profession = profession;
  }
  public Long getLevel() {
    return level;
  }
  public void setLevel(Long level) {
    this.level = level;
  }
  public String getGuild() {
    return guild;
  }
  public void setGuild(String guild) {
    this.guild = guild;
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
  public String getDeaths() {
    return deaths;
  }
  public void setDeaths(String deaths) {
    this.deaths = deaths;
  }
}
