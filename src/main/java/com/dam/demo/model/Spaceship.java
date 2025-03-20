package com.dam.demo.model;

import static com.dam.demo.model.UserConstants.ATTACK;
import static com.dam.demo.model.UserConstants.COINS;
import static com.dam.demo.model.UserConstants.HEALTH;
import static com.dam.demo.model.UserConstants.POINTS;
import static com.dam.demo.model.UserConstants.TAGS;

import com.dam.demo.controls.SpaceshipControl;
import com.dam.demo.enemies.Tag;
import com.dam.demo.model.upgrade.Buff;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import java.util.Set;

public record Spaceship(
    Spatial spatial,
    Dimensions dimensions,
    Set<Tag> tags,
    String attack) {

  public static Spaceship of(Spatial spatial) {
    return new Spaceship(
        spatial,
        Dimensions.of(spatial),
        Tag.parse(spatial.getUserData(TAGS)),
        spatial.getUserData(ATTACK)
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
    var points =  points() + amount;
    spatial.setUserData(POINTS, points);

    return this;
  }

  public Spaceship addBuff(Buff buff) {
    spatial.getControl(SpaceshipControl.class).addBuff(buff);

    return this;
  }

  public boolean is(Tag tag) {
    return tags.contains(tag);
  }

  public SpaceshipControl control() {
    return spatial.getControl(SpaceshipControl.class);
  }
}
