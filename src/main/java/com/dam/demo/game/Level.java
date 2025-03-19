package com.dam.demo.game;

import static com.dam.demo.enemies.EnemySpawner.BOMBER_DEF;
import static com.dam.demo.enemies.EnemySpawner.BOSS_1;
import static com.dam.demo.enemies.EnemySpawner.CHASER_DEF;
import static com.dam.demo.enemies.EnemySpawner.CRUISER_DEF;
import static com.dam.demo.enemies.EnemySpawner.createEnemy;
import static com.dam.demo.game.Scene.ENEMIES;
import static com.dam.demo.game.Scene.HUD;
import static com.dam.demo.game.Scene.PLAYER;

import com.dam.demo.model.Spaceship;
import com.dam.demo.model.UserConstants;
import java.util.Optional;
import java.util.stream.Stream;

public enum Level {
  ;

  private static final int BASE_SCORE = 3_000;
  private static LevelState state = LevelState.ENEMY_SPAWNING;
  private static int level = 1;

  public static int level() {
    return level;
  }

  public static void tick() {
    HUD.update();
    var unused = switch (state) {
      case ENEMY_SPAWNING -> spawnEnemies();
      case BOSS_SPAWNING -> spawnBoss();
      case BOSS_FIGHTING, PAUSED, DEFEAT, VICTORY -> null; // Don't do anything
    };
  }

  private static Void spawnBoss() {
    if (!ENEMIES.getChildren().isEmpty()) {
      // Let the player kill the rest of the enemies before spawning the boss
      return null;
    }
    SoundUtil.music("boss1");
    Scene.addEnemy(createEnemy(BOSS_1).get());
    state = LevelState.BOSS_FIGHTING;
    return null;
  }

  private static int enemyPoints() {
    return ENEMIES.getChildren()
        .stream()
        .mapToInt(x -> x.getUserData(UserConstants.POINTS))
        .sum();
  }

  public static int getLevelScore(int level) {
    int result = BASE_SCORE;
    for (int i = 1; i < level; i++) {
      result = 2 * result + (result / 2);
    }
    return result;
  }

  private static Void spawnEnemies() {
    if (PLAYER.points() + enemyPoints() >= getLevelScore(level)) {
      state = LevelState.BOSS_SPAWNING;
      return null;
    }
    Stream.of(
            createEnemy(CRUISER_DEF),
            createEnemy(BOMBER_DEF),
            level > 1 ? createEnemy(CHASER_DEF) : Optional.<Spaceship>empty()
        )
        .filter(Optional::isPresent)
        .map(Optional::get)
        .forEach(Scene::addEnemy);
    return null;
  }

  public static void bossKilled() {
    state = LevelState.VICTORY;
    Scene.pause("Victory");
    SoundUtil.music("victory");
  }

  public static void pause() {
    state = switch (state) {
      case PAUSED -> {
        Scene.unpause();
        yield LevelState.ENEMY_SPAWNING;
      }
      case ENEMY_SPAWNING, BOSS_SPAWNING, BOSS_FIGHTING -> {
        Scene.pause("Paused");
        yield LevelState.PAUSED;
      }
      case DEFEAT -> {
        reset();
        yield LevelState.ENEMY_SPAWNING;
      }
      case VICTORY -> {
        ++level;
        SoundUtil.music("ambient");
        Scene.unpause();
        yield LevelState.ENEMY_SPAWNING;
      }
    };
  }

  private static void reset() {
    level = 1;
    SoundUtil.music("ambient");
    Scene.reset();
    Scene.unpause();
  }

  public static void playerKilled() {
    SoundUtil.play("gameOver");
    SoundUtil.music("lose");
    Scene.pause("Game Over");
    state = LevelState.DEFEAT;
  }

  public enum LevelState {
    PAUSED,
    ENEMY_SPAWNING,
    BOSS_SPAWNING,
    BOSS_FIGHTING,
    VICTORY,
    DEFEAT;
  }
}
