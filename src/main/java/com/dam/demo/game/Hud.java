package com.dam.demo.game;

import static com.dam.demo.game.Scene.PLAYER;
import static com.dam.demo.util.AssetUtil.manager;
import static com.dam.demo.util.AssetUtil.screenHeight;
import static com.dam.demo.util.AssetUtil.screenWidth;

import com.jme3.font.BitmapText;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

public class Hud {

  private static final float OFFSET = 100;
  private final Node spatial;
  private final BitmapText health;
  private final BitmapText score;
  private final BitmapText coins;
  private final BitmapText level;


  private Hud(
      Node spatial,
      BitmapText health,
      BitmapText score,
      BitmapText coins,
      BitmapText level) {
    this.spatial = spatial;
    this.health = health;
    this.score = score;
    this.coins = coins;
    this.level = level;
  }

  public static Hud initialize() {
    var result = new Node("hud");
    var health = createText();
    result.attachChild(health);

    var coins = createText();
    result.attachChild(coins);

    var score = createText();
    result.attachChild(score);
    var level = createText();
    result.attachChild(level);

    return new Hud(result, health, score, coins, level);
  }

  private static BitmapText createText() {
    var font = manager.loadFont("Interface/Fonts/Default.fnt");
    var result = new BitmapText(font);
    result.setSize(30);

    return result;
  }

  public void update() {
    health.setText("Health: " + PLAYER.health());
    coins.setText("Coins: " + PLAYER.coins());
    score.setText("Score: " + PLAYER.points());
    level.setText("Level: " + Level.level());
    centerHud();
  }

  private void centerHud() {
    var entries = spatial.getChildren();
    var width = entries.stream()
        .mapToDouble(x -> ((BitmapText) x).getLineWidth() + OFFSET)
        .sum() - OFFSET;
    var offset = (float) (screenWidth() - width) / 2;
    for (Spatial value : entries) {
      var entry = (BitmapText) value;
      entry.setLocalTranslation(offset, screenHeight() - 30f, 0);
      offset += entry.getLineWidth() + OFFSET;
    }
  }

  public Spatial spatial() {
    return spatial;
  }
}
