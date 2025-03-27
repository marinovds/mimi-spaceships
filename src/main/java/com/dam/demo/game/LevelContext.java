package com.dam.demo.game;

import static com.dam.demo.enemies.EnemySpawner.BOMBER_DEF;
import static com.dam.demo.enemies.EnemySpawner.CHASER_DEF;
import static com.dam.demo.enemies.EnemySpawner.CRUISER_DEF;
import static com.dam.demo.util.AssetUtil.screenHeight;
import static com.dam.demo.util.AssetUtil.spaceship;

import com.dam.demo.controls.SpaceshipControl;
import com.dam.demo.enemies.EnemyDef;
import com.dam.demo.enemies.EnemySpawner;
import com.dam.demo.listeners.KeyboardListener.Input;
import com.dam.demo.model.UserConstants;
import com.dam.demo.model.behaviour.spaceship.PlayerBehaviour;
import com.dam.demo.model.spaceship.Spaceship;
import com.dam.demo.model.spaceship.SpaceshipDefinitions;
import com.dam.demo.util.SoundUtil;
import com.dam.util.RandomUtil;
import com.jme3.app.SimpleApplication;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public final class LevelContext implements GameContext {

  private static final int BASE_SCORE = 3_000;

  private final Node guiNode;
  private final Hud hud;

  private LevelState state;
  public Spaceship player;

  // TODO: refactor
  public static int level = 1;

  LevelContext(SimpleApplication app) {
    this.guiNode = app.getGuiNode();
    this.player = player();
    this.hud = Hud.initialize();
    this.state = LevelState.INIT;
  }

  @Override
  public void enable() {
    state = LevelState.ENEMY_SPAWNING;
    SoundUtil.music("ambient");

    guiNode.attachChild(Scene.BUFFS);
    guiNode.attachChild(Scene.PLAYER_BULLETS);
    guiNode.attachChild(Scene.ENEMIES);
    guiNode.attachChild(Scene.ENEMY_BULLETS);
    guiNode.attachChild(Scene.PARTICLES);

    guiNode.attachChild(player.spatial());
    guiNode.attachChild(hud.spatial());
  }

  @Override
  public void onTick(float tpf) {
    hud.update(player);
    var unused = switch (state) {
      case INIT -> throw new IllegalStateException("Level not initialized");
      case ENEMY_SPAWNING -> spawnEnemies(tpf);
      case BOSS_SPAWNING -> spawnBoss();
    };
  }

  @Override
  public void disable() {
    Scene.BUFFS.removeFromParent();
    Scene.PLAYER_BULLETS.removeFromParent();
    Scene.ENEMIES.removeFromParent();
    Scene.ENEMY_BULLETS.removeFromParent();
    Scene.PARTICLES.removeFromParent();

    resetPlayer();
    hud.spatial().removeFromParent();
  }

  private void resetPlayer() {
    var behaviour = playerBehaviour();
    behaviour.onInput(Input.UP, false);
    behaviour.onInput(Input.DOWN, false);
    behaviour.onInput(Input.SHOOT, false);

    player.spatial().removeFromParent();
  }

  @Override
  public void onInput(Input input, boolean isPressed) {
    var behaviour = playerBehaviour();
    behaviour.onInput(input, isPressed);
  }

  public void reset() {
    resetPlayer();
    player = player();
    level = 1;

    Scene.ENEMIES.getChildren().forEach(Spatial::removeFromParent);
    Scene.ENEMY_BULLETS.getChildren().forEach(Spatial::removeFromParent);
    Scene.PLAYER_BULLETS.getChildren().forEach(Spatial::removeFromParent);
    Scene.BUFFS.getChildren().forEach(Spatial::removeFromParent);
    Scene.PARTICLES.getChildren().forEach(Spatial::removeFromParent);
  }

  private Spaceship player() {
    Spaceship result = spaceship(SpaceshipDefinitions.PLAYER_DEF);
    var dimensions = result.dimensions();
    var spatial = result.spatial();
    spatial.setLocalTranslation(dimensions.radius(), screenHeight() / 2f, 0);
    spatial.addControl(new SpaceshipControl(new PlayerBehaviour(result)));

    return result;
  }


  private Void spawnBoss() {
    if (!Scene.ENEMIES.getChildren().isEmpty()) {
      // Let the player kill the rest of the enemies before spawning the boss
      return null;
    }
    var boss = selectBoss(level);
    SoundUtil.music(boss.spaceship().name());

    // Bosses are never in cooldown
    addEnemy(createEnemy(boss, 0).get());
    return null;
  }

  private static EnemyDef selectBoss(int level) {
    return switch (level) {
      case 1 -> EnemySpawner.BOSS_1;
      case 2 -> EnemySpawner.BOSS_2;
      case 3 -> EnemySpawner.BOSS_3;
      default -> RandomUtil.formallyDistributed(List.of(
          EnemySpawner.BOSS_1,
          EnemySpawner.BOSS_2,
          EnemySpawner.BOSS_3
      ));
    };
  }

  private int enemyPoints() {
    return Scene.ENEMIES.getChildren()
        .stream()
        .mapToInt(x -> x.getUserData(UserConstants.POINTS))
        .sum();
  }

  private static int getLevelScore(int level) {
    int result = BASE_SCORE;
    for (int i = 1; i < level; i++) {
      result = 2 * result + (result / 2);
    }
    return result;
  }

  private Void spawnEnemies(float tpf) {
    if (player.points() + enemyPoints() >= getLevelScore(level)) {
      state = LevelState.BOSS_SPAWNING;
      return null;
    }
    Stream.of(
            createEnemy(CRUISER_DEF, tpf),
            createEnemy(BOMBER_DEF, tpf),
            level > 1 ? createEnemy(CHASER_DEF, tpf) : Optional.<Spaceship>empty()
        )
        .filter(Optional::isPresent)
        .map(Optional::get)
        .forEach(this::addEnemy);
    return null;
  }

  private Optional<Spaceship> createEnemy(EnemyDef def, float tpf) {
    return EnemySpawner.createEnemy(Scene.ENEMIES, def, tpf);
  }

  private void addEnemy(Spaceship spaceship) {
    Scene.ENEMIES.attachChild(spaceship.spatial());
  }

  private PlayerBehaviour playerBehaviour() {
    return (PlayerBehaviour) player.spatial().getControl(SpaceshipControl.class).getBehaviour();
  }

  public void bossKilled() {
    Contexts.switchContext(ShopContext.class);
  }

  public void playerKilled() {
    state = LevelState.INIT;
    SoundUtil.play("gameOver");
    player.spatial().removeFromParent();
    Contexts.switchContext(HighScoreContext.class);
  }

  public void nextLevel() {
    ++LevelContext.level;
    state = LevelState.ENEMY_SPAWNING;
  }

  public boolean inGame() {
    return state != LevelState.INIT;
  }

  public enum LevelState {
    INIT,
    ENEMY_SPAWNING,
    BOSS_SPAWNING,
  }
}
