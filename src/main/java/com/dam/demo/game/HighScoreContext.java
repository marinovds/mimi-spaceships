package com.dam.demo.game;

import static com.dam.demo.util.AssetUtil.screenHeight;
import static com.dam.demo.util.AssetUtil.screenWidth;
import static com.dam.util.RandomUtil.RANDOM;

import com.dam.demo.controls.ParticleManager;
import com.dam.demo.listeners.KeyboardListener.Input;
import com.dam.demo.model.menu.Menu;
import com.dam.demo.model.menu.MenuAction;
import com.dam.demo.model.menu.MenuConfig;
import com.dam.demo.model.menu.MenuEntry;
import com.dam.demo.util.AssetUtil;
import com.dam.demo.util.SaveUtil;
import com.dam.demo.util.SoundUtil;
import com.jme3.app.SimpleApplication;
import com.jme3.font.BitmapText;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.util.List;

public final class HighScoreContext implements GameContext {

  private final Node guiNode;
  private final Menu menu;

  private boolean fireworks;

  HighScoreContext(SimpleApplication app) {
    this.guiNode = app.getGuiNode();
    this.menu = Menu.of(
        List.of(
            HighscoreEntry.empty(ColorRGBA.Orange),
            HighscoreEntry.value(String.valueOf(load())),
            HighscoreEntry.empty(ColorRGBA.White)
        ),
        new MenuConfig(
            "highscore",
            true,
            false,
            "menuMove",
            40
        )
    );
    fireworks = false;
  }

  @Override
  public void enable() {
    menu.enable();
    guiNode.attachChild(menu.spatial());
    guiNode.attachChild(Scene.PARTICLES);
  }

  @Override
  public void onTick(float tpf) {
    if (fireworks && RANDOM.nextInt(100) == 0) {
      SoundUtil.play("explode");
      ParticleManager.explosion(randomPosition(), 100);
    }
    menu.onTick();
  }

  @Override
  public void disable() {
    fireworks = false;
    setText(0, "");
    menu.disable();
    Scene.PARTICLES.removeFromParent();
  }

  @Override
  public void onInput(Input input, boolean isPressed) {
    if (Input.SELECT == input && isPressed) {
      Contexts.switchContext(MenuContext.class);
    }
    // TODO;
  }

  public void save(int score) {
    int past = load();
    if (score > past) {
      fireworks = true;
      var node = new Node("highscore");
      node.setUserData("score", score);
      SaveUtil.save("highscore", node);
      setText(0, "HIGH SCORE!!!");
      setText(1, String.valueOf(score));
    }
  }

  public static boolean available() {
    return SaveUtil.exits("highscore");
  }

  private static int load() {
    if (!available()) {
      return 0;
    }

    return SaveUtil.load("highscore").getUserData("score");
  }

  private void setText(int index, String text) {
    HighscoreEntry entry = menu.getEntry(index);
    entry.text().setText(text);
  }

  private Vector3f randomPosition() {
    return new Vector3f(
        RANDOM.nextInt(screenWidth()),
        RANDOM.nextInt(screenHeight()),
        0
    );
  }

  private record HighscoreEntry(BitmapText text) implements MenuEntry {

    public static HighscoreEntry value(String val) {
      return new HighscoreEntry(AssetUtil.text(val, 60));
    }

    public static HighscoreEntry empty(ColorRGBA color) {
      var text = AssetUtil.text(60);
      text.setColor(color);

      return new HighscoreEntry(text);
    }

    @Override
    public float height() {
      return text.getHeight();
    }

    @Override
    public float width() {
      return text.getLineWidth();
    }

    @Override
    public boolean selectable() {
      return false;
    }

    @Override
    public void onCursor(boolean state) {

    }

    @Override
    public MenuAction action() {
      return MenuAction.NO_ON;
    }

    @Override
    public Spatial spatial() {
      return text;
    }
  }
}
