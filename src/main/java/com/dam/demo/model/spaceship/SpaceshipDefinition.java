package com.dam.demo.model.spaceship;

import static com.dam.demo.enemies.Tag.SpatialType.SPACESHIP;
import static com.dam.demo.model.UserConstants.ATTACK;
import static com.dam.demo.model.UserConstants.COINS;
import static com.dam.demo.model.UserConstants.HEALTH;
import static com.dam.demo.model.UserConstants.POINTS;
import static com.dam.demo.model.UserConstants.SPEED;
import static com.dam.demo.model.UserConstants.TAGS;

import com.dam.demo.enemies.Tag;
import com.dam.demo.model.attack.SpaceshipAttack;
import com.dam.demo.util.JsonUtil;
import com.jme3.scene.Spatial;
import java.util.Set;
import java.util.stream.Stream;

public record SpaceshipDefinition(
    String name,
    Set<Tag> tags,
    SpaceshipAttack attack,
    int speed,
    int health,
    int coins,
    int points) {

  private static Object[] tags(Set<Tag> tags) {
    return Stream.concat(
            tags.stream(),
            Stream.of(SPACESHIP)
        )
        .toArray(Object[]::new);
  }

  public void applyTo(Spatial spatial) {
    spatial.setUserData(HEALTH, health);
    spatial.setUserData(SPEED, speed);
    spatial.setUserData(ATTACK, JsonUtil.write(attack));
    spatial.setUserData(COINS, coins);
    spatial.setUserData(POINTS, points);
    spatial.setUserData(TAGS, tags(tags));
  }
}
