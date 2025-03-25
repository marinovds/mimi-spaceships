package com.dam.demo.enemies;

import static com.dam.demo.util.AssetUtil.screenHeight;
import static com.dam.demo.util.AssetUtil.screenWidth;
import static com.dam.util.RandomUtil.RANDOM;

import com.dam.demo.controls.SpaceshipControl;
import com.dam.demo.model.Dimensions;
import com.dam.demo.model.behaviour.spaceship.BomberBehaviour;
import com.dam.demo.model.behaviour.spaceship.Boss1Behaviour;
import com.dam.demo.model.behaviour.spaceship.ChaserBehaviour;
import com.dam.demo.model.behaviour.spaceship.CruiserBehaviour;
import com.dam.demo.model.spaceship.Spaceship;
import com.dam.demo.model.spaceship.SpaceshipDefinitions;
import com.dam.demo.util.AssetUtil;
import com.dam.demo.util.LangUtil;
import com.dam.demo.util.MathUtil;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public enum EnemySpawner {
  ;

  public static final EnemyDef BOSS_1 = new EnemyDef(
      SpaceshipDefinitions.BOSS_1_DEF,
      SpawnCriteria.NONE,
      Boss1Behaviour::new
  );

  public static final EnemyDef CRUISER_DEF = new EnemyDef(
      SpaceshipDefinitions.CRUISER_DEF,
      new SpawnCriteria(Duration.ofSeconds(1), 150, 5),
      CruiserBehaviour::new
  );


  public static final EnemyDef BOMBER_DEF = new EnemyDef(
      SpaceshipDefinitions.BOMBER_DEF,
      new SpawnCriteria(Duration.ofSeconds(3), 300, 3),
      BomberBehaviour::new
  );

  public static final EnemyDef CHASER_DEF = new EnemyDef(
      SpaceshipDefinitions.CHASER_DEF,
      new SpawnCriteria(Duration.ofSeconds(1), 300, 1),
      ChaserBehaviour::new
  );

  public static Optional<Spaceship> createEnemy(Node enemyNode, EnemyDef definition, float tpf) {
    var enemyDef = definition.spaceship();
    var criteria = definition.spawn();
    var enemy = enemyDef.name();
    var enemies = getEnemiesOfType(enemyNode, enemy);
    if (shouldNotSpawn(enemyNode, enemy, criteria, enemies)) {
      var cooldown = MathUtil.subtractDuration(getEnemyCooldown(enemyNode, enemy), tpf);
      setEnemyCooldown(enemyNode, enemy, cooldown);

      return Optional.empty();
    }

    setEnemyCooldown(enemyNode, enemy, definition.spawn().cooldown());
    var spaceship = AssetUtil.spaceship(enemyDef);
    var spatial = spaceship.spatial();
    spatial.setName(enemy + "_" + UUID.randomUUID());
    spatial.setLocalTranslation(getSpawnPosition(spaceship.dimensions()));
    spatial.addControl(new SpaceshipControl(definition.behaviour().apply(spaceship)));

    return Optional.of(spaceship);
  }

  private static boolean shouldNotSpawn(
      Node enemyNode,
      String enemy,
      SpawnCriteria criteria,
      List<Spaceship> enemies) {
    if (criteria == SpawnCriteria.NONE) {
      return false;
    }

    return enemies.size() >= criteria.maxNumber()
        || getEnemyCooldown(enemyNode, enemy).isPositive()
        || RANDOM.nextInt(criteria.random()) != 0;
  }

  private static Duration getEnemyCooldown(Node enemyNode, String name) {
    var result = LangUtil.mapNull(enemyNode.getUserData(name), Duration::parse);
    return result == null
        ? Duration.ZERO
        : result;
  }

  private static void setEnemyCooldown(Node enemyNode, String name, Duration duration) {
    enemyNode.setUserData(name, duration.toString());
  }

  private static List<Spaceship> getEnemiesOfType(Node enemyNode, String type) {
    return enemyNode.getChildren()
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
