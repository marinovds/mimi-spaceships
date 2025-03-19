package com.dam.demo.enemies;

import static com.dam.demo.game.Scene.ENEMIES;
import static com.dam.demo.util.AssetUtil.screenHeight;
import static com.dam.demo.util.AssetUtil.screenWidth;
import static com.dam.demo.util.MathUtil.inCooldown;
import static com.dam.util.RandomUtil.RANDOM;

import com.dam.demo.controls.behaviour.ControlsUtil;
import com.dam.demo.controls.behaviour.movement.BomberMovement;
import com.dam.demo.controls.behaviour.movement.ChaserMovement;
import com.dam.demo.controls.behaviour.movement.CruiserMovement;
import com.dam.demo.game.SpaceshipDefinitions;
import com.dam.demo.model.Dimensions;
import com.dam.demo.model.Spaceship;
import com.dam.demo.model.upgrade.UpgradeUtil;
import com.dam.demo.util.AssetUtil;
import com.dam.demo.util.LangUtil;
import com.jme3.math.Vector3f;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public enum EnemySpawner {
  ;

  public static final EnemyDef BOSS_1 = new EnemyDef(
      SpaceshipDefinitions.BOSS_1_DEF,
      SpawnCriteria.NONE,
      CruiserMovement::new
  );

  public static final EnemyDef CRUISER_DEF = new EnemyDef(
      SpaceshipDefinitions.CRUISER_DEF,
      new SpawnCriteria(Duration.ofSeconds(1), 150, 5),
      CruiserMovement::new
  );


  public static final EnemyDef BOMBER_DEF = new EnemyDef(
      SpaceshipDefinitions.BOMBER_DEF,
      new SpawnCriteria(Duration.ofSeconds(3), 300, 3),
      BomberMovement::new
  );

  public static final EnemyDef CHASER_DEF = new EnemyDef(
      SpaceshipDefinitions.CHASER_DEF,
      new SpawnCriteria(Duration.ofSeconds(1), 300, 1),
      ChaserMovement::new
  );

  public static Optional<Spaceship> createEnemy(EnemyDef definition) {
    var enemyDef = UpgradeUtil.upgrade(definition.spaceship());
    var criteria = definition.spawn();
    var enemies = getEnemiesOfType(enemyDef.name());
    if (shouldNotSpawn(enemyDef.name(), criteria, enemies)) {

      return Optional.empty();
    }
    ENEMIES.setUserData(enemyDef.name(), Instant.now().toString());
    var enemy = AssetUtil.spaceship(enemyDef);
    var spatial = enemy.spatial();
    spatial.setName(enemyDef.name() + "_" + UUID.randomUUID());
    spatial.setLocalTranslation(getSpawnPosition(enemy.dimensions()));
    var movement = definition.movement().apply(enemy);
    spatial.addControl(ControlsUtil.spaceshipControl(enemy, movement));

    return Optional.of(enemy);
  }

  private static boolean shouldNotSpawn(String enemy, SpawnCriteria criteria, List<Spaceship> enemies) {
    if (criteria == SpawnCriteria.NONE) {
      return false;
    }
    return enemies.size() >= criteria.maxNumber()
        || inCooldown(lastSpawned(enemy), criteria.cooldown())
        || RANDOM.nextInt(criteria.random()) != 0;
  }

  private static Instant lastSpawned(String name) {
    return LangUtil.mapNull(ENEMIES.getUserData(name), Instant::parse);
  }

  private static List<Spaceship> getEnemiesOfType(String type) {
    return ENEMIES.getChildren()
        .stream()
        .filter(x -> x.getName().startsWith(type))
        .map(Spaceship::of)
        .toList();
  }

  private static Vector3f getSpawnPosition(Dimensions dims) {

    return new Vector3f(screenWidth() - dims.width() / 2f,
        RANDOM.nextFloat(screenHeight() - dims.height() / 2),
        0);
  }
}
