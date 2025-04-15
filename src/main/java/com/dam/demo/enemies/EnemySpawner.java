package com.dam.demo.enemies;

import static com.dam.demo.util.AssetUtil.screenHeight;
import static com.dam.demo.util.AssetUtil.screenWidth;
import static com.dam.util.RandomUtil.RANDOM;

import com.dam.demo.controls.SpaceshipControl;
import com.dam.demo.model.Dimensions;
import com.dam.demo.model.Ticker;
import com.dam.demo.model.behaviour.spaceship.BomberBehaviour;
import com.dam.demo.model.behaviour.spaceship.Boss1Behaviour;
import com.dam.demo.model.behaviour.spaceship.ChaserBehaviour;
import com.dam.demo.model.behaviour.spaceship.CruiserBehaviour;
import com.dam.demo.model.spaceship.Spaceship;
import com.dam.demo.model.spaceship.SpaceshipDefinitions;
import com.dam.demo.model.upgrade.Upgrade;
import com.dam.demo.model.upgrade.UpgradeType;
import com.dam.demo.model.upgrade.UpgradeUtil;
import com.dam.demo.util.AssetUtil;
import com.dam.demo.util.LangUtil;
import com.dam.util.RandomUtil;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public enum EnemySpawner {
  ;

  public static final EnemyDef BOSS_1 = new EnemyDef(
      SpaceshipDefinitions.BOSS_1_DEF,
      SpawnCriteria.none(1),
      Boss1Behaviour::new
  );

  public static final EnemyDef BOSS_2 = new EnemyDef(
      SpaceshipDefinitions.BOSS_1_DEF,
      SpawnCriteria.none(2),
      Boss1Behaviour::new
  );

  public static final EnemyDef BOSS_3 = new EnemyDef(
      SpaceshipDefinitions.BOSS_1_DEF,
      SpawnCriteria.none(3),
      Boss1Behaviour::new
  );

  public static final EnemyDef CRUISER_DEF = new EnemyDef(
      SpaceshipDefinitions.CRUISER_DEF,
      new SpawnCriteria(Duration.ofSeconds(1), 150, 5, 1),
      CruiserBehaviour::new
  );


  public static final EnemyDef BOMBER_DEF = new EnemyDef(
      SpaceshipDefinitions.BOMBER_DEF,
      new SpawnCriteria(Duration.ofSeconds(3), 300, 3, 2),
      BomberBehaviour::new
  );

  public static final EnemyDef CHASER_DEF = new EnemyDef(
      SpaceshipDefinitions.CHASER_DEF,
      new SpawnCriteria(Duration.ofSeconds(1), 300, 1, 3),
      ChaserBehaviour::new
  );

  public static List<Spaceship> spawnRegularEnemies(Node enemies, int level, float tpf) {
    return Stream.of(
            CRUISER_DEF,
            BOMBER_DEF,
            CHASER_DEF
        )
        .filter(x -> x.spawn().level() <= level)
        .map(x -> createEnemy(enemies, level, x, tpf))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .toList();
  }

  public static Spaceship spawnBoss(EnemyDef definition, int level) {
    return spawn(definition, level);
  }

  private static Optional<Spaceship> createEnemy(Node enemyNode, int level, EnemyDef definition,
      float tpf) {
    var criteria = definition.spawn();
    var enemy = definition.spaceship().name();
    var enemies = getEnemiesOfType(enemyNode, enemy);
    var enemyCooldown = getEnemyCooldown(enemyNode, enemy);
    if (shouldNotSpawn(enemyCooldown, criteria, enemies)) {
      // Tick the cooldown
      var cooldown = Ticker.of(enemyCooldown).tick(tpf);
      setEnemyCooldown(enemyNode, enemy, cooldown.currentDuration());

      return Optional.empty();
    }

    // Reset the cooldown
    setEnemyCooldown(enemyNode, enemy, criteria.cooldown());

    return Optional.of(spawn(definition, level));
  }

  private static List<Upgrade> upgrades(int level, EnemyDef definition) {
    // TODO: Calculate which is the last level to spawn a unique enemy and do ot hardcode the chaser
    var lastMobLevel = CHASER_DEF.spawn().level();
    if (level <= lastMobLevel) {
      // No upgrades this early on
      return List.of();
    }
    // EG: at lvl 4, cruiser will get 1 upgrade roll
    var initialSpawnLevel = lastMobLevel + definition.spawn().level() - 1;

    return IntStream.range(initialSpawnLevel, level)
        .filter(x -> RANDOM.nextInt(2) == 0)
        .mapToObj(x -> RandomUtil.formallyDistributed(UpgradeType.values()))
        .map(x -> new Upgrade(20, x))
        .toList();
  }

  private static boolean shouldNotSpawn(
      Duration enemyCooldown,
      SpawnCriteria criteria,
      List<Spaceship> enemies) {
    if (!criteria.cooldown().isPositive()) {
      return false;
    }

    return enemies.size() >= criteria.maxNumber()
        || enemyCooldown.isPositive()
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

  private static Spaceship spawn(EnemyDef def, int level) {
    var spaceshipDef = def.spaceship();
    var spaceship = AssetUtil.spaceship(spaceshipDef);
    var upgrades = upgrades(level, def);
    upgrades.forEach(spaceship::addUpgrade);

    var spatial = spaceship.spatial();
    UpgradeUtil.applyColors(spatial, List.of(), upgrades);
    spatial.setName(spaceshipDef.name());
    spatial.setLocalTranslation(getSpawnPosition(spaceship.dimensions()));
    spatial.addControl(new SpaceshipControl(def.behaviour().apply(spaceship)));

    return spaceship;
  }
}
