package com.dam.demo.game;

import static com.dam.demo.util.AssetUtil.screenHeight;
import static com.dam.demo.util.AssetUtil.screenWidth;
import static com.dam.demo.util.AssetUtil.text;

import com.dam.demo.game.context.LevelContext;
import com.dam.demo.model.spaceship.Spaceship;
import com.jme3.font.BitmapText;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.util.List;
import java.util.function.Function;

public class Hud {

  private static final float OFFSET = 100;

  private final Node spatial;
  private final List<HudEntry> entries;


  private Hud(
      Node spatial,
      List<HudEntry> entries) {
    this.spatial = spatial;
    this.entries = entries;
  }

  public static Hud initialize() {
    var result = new Node("hud");
    var entries = List.of(
        createText("Health", Spaceship::health),
        createText("Coins", Spaceship::coins),
        createText("Score", Spaceship::points),
        createText("Level", (x) -> LevelContext.level)
    );

    entries.forEach(x -> result.attachChild(x.spatial()));

    return new Hud(result, entries);
  }

  private static HudEntry createText(String type, Function<Spaceship, Integer> value) {

    return new HudEntry(text(30), type, value);
  }

  public void update(Spaceship player) {
    entries.forEach(x -> x.visualize(player));
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

  private record HudEntry(BitmapText spatial, String type, Function<Spaceship, Integer> value) {

    void visualize(Spaceship spaceship) {
      spatial.setText(type + ": " + value.apply(spaceship));
    }
  }
}
