package com.dam.demo.model.spaceship;

import static com.dam.demo.model.UserConstants.ATTACK;
import static com.dam.demo.model.UserConstants.BUFF;
import static com.dam.demo.model.UserConstants.COINS;
import static com.dam.demo.model.UserConstants.HEALTH;
import static com.dam.demo.model.UserConstants.POINTS;
import static com.dam.demo.model.UserConstants.TAGS;
import static com.dam.demo.model.UserConstants.UPGRADE;

import com.dam.demo.enemies.Tag;
import com.dam.demo.model.Dimensions;
import com.dam.demo.model.UserConstants;
import com.dam.demo.model.attack.SpaceshipAttack;
import com.dam.demo.model.upgrade.Buff;
import com.dam.demo.model.upgrade.Upgrade;
import com.dam.demo.model.upgrade.UpgradeUtil;
import com.dam.demo.util.JsonUtil;
import com.dam.demo.util.LangUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

public record Spaceship(
    Spatial spatial,
    Dimensions dimensions,
    Set<Tag> tags) {

  public static Spaceship of(Spatial spatial) {
    return new Spaceship(
        spatial,
        Dimensions.of(spatial),
        Tag.parse(spatial.getUserData(TAGS))
    );
  }

  public int health() {
    return spatial.getUserData(UserConstants.HEALTH);
  }

  public int points() {
    return spatial.getUserData(UserConstants.POINTS);
  }

  public int coins() {
    return spatial.getUserData(UserConstants.COINS);
  }

  public int speed() {
    return spatial.getUserData(UserConstants.SPEED);
  }

  public Vector3f location() {
    return spatial.getWorldTranslation();
  }

  public <T extends SpaceshipAttack> T attack(Class<T> clazz) {
    return JsonUtil.read(spatial.getUserData(ATTACK), clazz);
  }

  public Spaceship addCoins(int amount) {
    var coins = coins() + amount;
    spatial.setUserData(COINS, coins);

    return this;
  }

  public Spaceship addHealth(int amount) {
    var health = health() + amount;
    spatial.setUserData(HEALTH, health);

    return this;
  }

  public Spaceship addPoints(int amount) {
    var points = points() + amount;
    spatial.setUserData(POINTS, points);

    return this;
  }

  public List<Upgrade> upgrades() {
    var buffs = spatial.<String>getUserData(UPGRADE);
    if (buffs == null) {
      return List.of();
    }

    return JsonUtil.read(buffs, new TypeReference<>() {
    });
  }

  public Spaceship addUpgrade(Upgrade upgrade) {

    var updated = LangUtil.replace(upgrades(), upgrade, x -> x.type() == upgrade.type());
    spatial.setUserData(UPGRADE, JsonUtil.write(updated));

    return this;
  }

  public List<Buff> buffs() {
    var buffs = spatial.<String>getUserData(BUFF);
    if (buffs == null) {
      return List.of();
    }

    return JsonUtil.read(buffs, new TypeReference<>() {
    });
  }

  public Spaceship addBuff(Buff buff) {
    var updated = LangUtil.replace(buffs(), buff, x -> x.upgrade().type() == buff.upgrade().type());
    return setBuffs(updated);
  }

  public Spaceship setBuffs(List<Buff> buffs) {
    Function<List<Buff>, List<Upgrade>> f = x -> x.stream().map(Buff::upgrade).toList();
    var existing = buffs();
    UpgradeUtil.applyColors(spatial(), f.apply(existing), f.apply(buffs));

    spatial.setUserData(BUFF, JsonUtil.write(buffs));
    return this;
  }

  public List<Upgrade> improvements() {
    return Stream.concat(
            buffs().stream().map(Buff::upgrade),
            upgrades().stream()
        )
        .toList();
  }

  public boolean is(Tag tag) {
    return tags.contains(tag);
  }

  public int maxHealth() {
    return spatial.getUserData(UserConstants.MAX_HEALTH);
  }
}
