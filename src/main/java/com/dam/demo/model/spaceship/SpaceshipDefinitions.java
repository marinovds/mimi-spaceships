package com.dam.demo.model.spaceship;

import static com.dam.demo.enemies.Tag.ArmorType.HEAVY;
import static com.dam.demo.enemies.Tag.ArmorType.LIGHT;
import static com.dam.demo.enemies.Tag.EnemyType.BOMBER;
import static com.dam.demo.enemies.Tag.EnemyType.CHASER;
import static com.dam.demo.enemies.Tag.EnemyType.CRUISER;
import static com.dam.demo.enemies.Tag.ShipType.ENEMY;
import static com.dam.demo.model.attack.Damage.bullet;
import static com.dam.demo.model.attack.Damage.rocket;

import com.dam.demo.enemies.Tag.ArmorType;
import com.dam.demo.enemies.Tag.ShipType;
import com.dam.demo.enemies.Tag.SpatialType;
import com.dam.demo.model.attack.Shot;
import com.dam.demo.model.behaviour.spaceship.BomberBehaviour.BomberAttack;
import com.dam.demo.model.behaviour.spaceship.Boss1Behaviour.Boss1Attack;
import com.dam.demo.model.behaviour.spaceship.ChaserBehaviour.ChaserAttack;
import com.dam.demo.model.behaviour.spaceship.CruiserBehaviour.CruiserAttack;
import com.dam.demo.model.behaviour.spaceship.PlayerBehaviour.PlayerAttack;
import java.time.Duration;
import java.util.Set;

public enum SpaceshipDefinitions {
  ;

  public static final SpaceshipDefinition BOSS_1_DEF = new SpaceshipDefinition(
      "boss1",
      Set.of(SpatialType.SPACESHIP, ShipType.BOSS, ShipType.ENEMY, ArmorType.HEAVY),
      new Boss1Attack(
          new Shot(bullet(50), 2200, Duration.ofSeconds(1)),
          new Shot(rocket(20), 600, Duration.ofMillis(850)),
          new Shot(bullet(5), 900, Duration.ofMillis(500)),
          Duration.ofSeconds(3),
          Duration.ofMillis(500)
      ),
      300,
      350,
      500,
      0
  );

  public static final SpaceshipDefinition PLAYER_DEF = new SpaceshipDefinition(
      "player",
      Set.of(ShipType.PLAYER),
      new PlayerAttack(
          new Shot(bullet(10), 1100, Duration.ofMillis(500)),
          new Shot(rocket(20), 700, Duration.ofMillis(500))
      ),
      600,
      100,
      0,
      0
  );

  public static final SpaceshipDefinition CRUISER_DEF =
      new SpaceshipDefinition(
          "cruiser",
          Set.of(CRUISER, ENEMY),
          new CruiserAttack(new Shot(bullet(10), 1000, Duration.ofMillis(600)), 300),
          400,
          20,
          10,
          100
      );


  public static final SpaceshipDefinition BOMBER_DEF =
      new SpaceshipDefinition(
          "bomber",
          Set.of(BOMBER, ENEMY, HEAVY),
          new BomberAttack(30),
          200,
          100,
          25,
          300
      );

  public static final SpaceshipDefinition CHASER_DEF = new SpaceshipDefinition(
      "chaser",
      Set.of(CHASER, ENEMY, LIGHT),
      new ChaserAttack(30, 1.02f),
      500,
      10,
      15,
      70
  );
}
