package org.rima_dcbot.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.rima_dcbot.configuration.ConfigurationUtil;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@NoArgsConstructor
@Entity
public class Options {
  public Options(String discordId, String user, String discriminator, Double chanceWeight) {
    this.discordId = discordId;
    this.user = user;
    this.discriminator = discriminator;
    this.isIgnored = false;
    this.chanceWeight = chanceWeight;
    this.timeout = 0;
  }

  @Id
  private String discordId;

  private String user;
  private String discriminator;
  private Boolean isIgnored;
  private Double chanceWeight;
  private Integer timeout;
}
