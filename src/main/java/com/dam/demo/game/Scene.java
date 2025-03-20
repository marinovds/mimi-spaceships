package com.dam.demo.game;

import static com.dam.demo.game.SpaceshipDefinitions.PLAYER_DEF;
import static com.dam.demo.util.AssetUtil.manager;
import static com.dam.demo.util.AssetUtil.screenHeight;
import static com.dam.demo.util.AssetUtil.screenWidth;
import static com.dam.demo.util.AssetUtil.spaceship;

import com.dam.demo.controls.SpaceshipControl;
import com.dam.demo.controls.behaviour.spaceship.PlayerBehaviour;
import com.dam.demo.model.Spaceship;
import com.dam.demo.util.AssetUtil;
import com.jme3.font.BitmapText;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

public enum Scene {
  ;

  public static final Node BUFFS = new Node("buffs");
  public static final Node PLAYER_BULLETS = new Node("playerBullets");
  public static final Node ENEMIES = new Node("enemies");
  public static final Node ENEMY_BULLETS = new Node("enemyBullets");
  public static final Node PARTICLES = new Node("particles");
  public static final BitmapText GAME_OVER = createGameOver();

  public static Spaceship PLAYER;
  public static Hud HUD;
  private static Node guiNode;

  public static void initialize(Node guiNode) {
    Scene.guiNode = guiNode;
    PLAYER = player();
    HUD = Hud.initialize();
    guiNode.attachChild(PLAYER.spatial());
    guiNode.attachChild(PLAYER_BULLETS);
    guiNode.attachChild(ENEMY_BULLETS);
    guiNode.attachChild(ENEMIES);
    guiNode.attachChild(BUFFS);
    guiNode.attachChild(PARTICLES);
    guiNode.attachChild(HUD.spatial());
  }

  private static BitmapText createGameOver() {
    var font = manager.loadFont("Interface/Fonts/Default.fnt");
    var result = new BitmapText(font);
    result.setSize(90);

    result.setName("paused");

    return result;
  }

  private static Spaceship player() {
    Spaceship result = spaceship(SpaceshipDefinitions.PLAYER_DEF);
    var dimensions = result.dimensions();
    var spatial = result.spatial();
    spatial.setLocalTranslation(dimensions.radius(), screenHeight() / 2f, 0);
    spatial.addControl(new SpaceshipControl(result, new PlayerBehaviour(result)));

    return result;
  }

  public static void unpause() {
    pause("", true);
  }

  public static void pause(String message) {
    pause(message, false);
  }

  private static void pause(String message, boolean active) {
    var unused = active
        ? GAME_OVER.removeFromParent()
        : guiNode.attachChild(setMessage(GAME_OVER, message));

    AssetUtil.pause(ENEMIES, active);
    AssetUtil.pause(ENEMY_BULLETS, active);

    AssetUtil.pause(PLAYER.spatial(), active);
    AssetUtil.pause(PLAYER_BULLETS, active);

    AssetUtil.pause(BUFFS, active);
  }

  private static Node setMessage(BitmapText node, String message) {
    node.setText(message);
    var xTranslation = (screenWidth() - node.getLineWidth()) / 2f;
    var yTranslation = (screenHeight() + node.getHeight()) / 2f;
    node.setLocalTranslation(new Vector3f(xTranslation, yTranslation, 0));
    return node;
  }

  public static void addEnemy(Spaceship enemy) {
    ENEMIES.attachChild(enemy.spatial());
  }

  public static void kill(Spaceship spaceship) {
    spaceship.spatial().removeFromParent();
  }

  public static void reset() {
    var spatial = PLAYER.spatial();
    PLAYER_DEF.applyTo(spatial);
    spatial.setLocalTranslation(PLAYER.dimensions().radius(), screenHeight() / 2f, 0);
    guiNode.attachChild(spatial);

    ENEMIES.getChildren().forEach(Spatial::removeFromParent);
    ENEMY_BULLETS.getChildren().forEach(Spatial::removeFromParent);
    PLAYER_BULLETS.getChildren().forEach(Spatial::removeFromParent);
    BUFFS.getChildren().forEach(Spatial::removeFromParent);
  }
}
