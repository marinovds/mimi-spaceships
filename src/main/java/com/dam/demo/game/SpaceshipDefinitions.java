package com.dam.demo.game;

import static com.dam.demo.enemies.Tag.ArmorType.HEAVY;
import static com.dam.demo.enemies.Tag.ArmorType.LIGHT;
import static com.dam.demo.enemies.Tag.EnemyType.BOMBER;
import static com.dam.demo.enemies.Tag.EnemyType.CHASER;
import static com.dam.demo.enemies.Tag.EnemyType.CRUISER;
import static com.dam.demo.enemies.Tag.ShipType.ENEMY;
import static com.dam.demo.model.attack.Damage.bullet;
import static com.dam.demo.model.attack.Damage.collision;

import com.dam.demo.enemies.Tag.ArmorType;
import com.dam.demo.enemies.Tag.ShipType;
import com.dam.demo.enemies.Tag.SpatialType;
import com.dam.demo.model.SpaceshipDefinition;
import com.dam.demo.model.attack.Attack.Collision;
import com.dam.demo.model.attack.Attack.Parallel;
import com.dam.demo.model.attack.Attack.Random;
import com.dam.demo.model.attack.Attack.Rotary;
import com.dam.demo.model.attack.Attack.Shot;
import java.time.Duration;
import java.util.List;
import java.util.Set;

public enum SpaceshipDefinitions {
  ;

  public static final SpaceshipDefinition BOSS_1_DEF = new SpaceshipDefinition(
      "boss1",
      Set.of(SpatialType.SPACESHIP, ShipType.BOSS, ShipType.ENEMY, ArmorType.HEAVY),
      new Rotary(
          List.of(
              new Shot(bullet(20), 600, Duration.ofMillis(300)),
              new Shot(bullet(30), 900, Duration.ofMillis(500)),
              new Shot(bullet(50), 1500, Duration.ofMillis(700)),
              new Shot(bullet(70), 2200, Duration.ofMillis(900))
          ),
          Duration.ofSeconds(3),
          Duration.ofMillis(500)),
      300,
      350,
      500,
      900
  );

  public static final SpaceshipDefinition PLAYER_DEF = new SpaceshipDefinition(
      "player",
      Set.of(ShipType.PLAYER),
      new Parallel(List.of(
          new Shot(bullet(10), 1100, Duration.ofMillis(500))
      )),
      600,
      100,
      0,
      0
  );

  public static final SpaceshipDefinition CRUISER_DEF =
      new SpaceshipDefinition(
          "cruiser",
          Set.of(CRUISER, ENEMY),
          new Random(new Shot(bullet(10), 1000, Duration.ofMillis(600)), 300),
          400,
          20,
          10,
          100
      );


  public static final SpaceshipDefinition BOMBER_DEF =
      new SpaceshipDefinition(
          "bomber",
          Set.of(BOMBER, ENEMY, HEAVY),
          new Collision(collision(30)),
          200,
          100,
          25,
          300
      );

  public static final SpaceshipDefinition CHASER_DEF = new SpaceshipDefinition(
      "chaser",
      Set.of(CHASER, ENEMY, LIGHT),
      new Collision(collision(30)),
      500,
      10,
      15,
      70
  );
}
