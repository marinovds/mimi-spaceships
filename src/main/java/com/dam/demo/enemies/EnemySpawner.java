package com.dam.demo.enemies;

import static com.dam.demo.game.Scene.ENEMIES;
import static com.dam.demo.util.AssetUtil.screenHeight;
import static com.dam.demo.util.AssetUtil.screenWidth;
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
import com.dam.demo.util.MathUtil;
import com.jme3.math.Vector3f;
import java.time.Duration;
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

  public static Optional<Spaceship> createEnemy(EnemyDef definition, float tpf) {
    var enemyDef = UpgradeUtil.upgrade(definition.spaceship());
    var criteria = definition.spawn();
    var enemy = enemyDef.name();
    var enemies = getEnemiesOfType(enemy);
    if (shouldNotSpawn(enemy, criteria, enemies)) {
      var cooldown = MathUtil.subtractDuration(getEnemyCooldown(enemy), tpf);
      setEnemyCooldown(enemy, cooldown);

      return Optional.empty();
    }

    setEnemyCooldown(enemy, definition.spawn().cooldown());
    var spaceship = AssetUtil.spaceship(enemyDef);
    var spatial = spaceship.spatial();
    spatial.setName(enemy + "_" + UUID.randomUUID());
    spatial.setLocalTranslation(getSpawnPosition(spaceship.dimensions()));
    var movement = definition.movement().apply(spaceship);
    spatial.addControl(ControlsUtil.spaceshipControl(spaceship, movement));

    return Optional.of(spaceship);
  }

  private static boolean shouldNotSpawn(
      String enemy,
      SpawnCriteria criteria,
      List<Spaceship> enemies) {
    if (criteria == SpawnCriteria.NONE) {
      return false;
    }

    return enemies.size() >= criteria.maxNumber()
        || getEnemyCooldown(enemy).isPositive()
        || RANDOM.nextInt(criteria.random()) != 0;
  }

  private static Duration getEnemyCooldown(String name) {
    var result = LangUtil.mapNull(ENEMIES.getUserData(name), Duration::parse);
    return result == null
        ? Duration.ZERO
        : result;
  }

  private static void setEnemyCooldown(String name, Duration duration) {
    ENEMIES.setUserData(name, duration.toString());
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
