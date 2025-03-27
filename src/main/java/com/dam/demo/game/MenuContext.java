package com.dam.demo.game;

import static com.dam.demo.util.AssetUtil.screenHeight;
import static com.dam.demo.util.AssetUtil.screenWidth;
import static com.dam.demo.util.AssetUtil.text;

import com.dam.demo.listeners.KeyboardListener.Input;
import com.dam.demo.util.LangUtil;
import com.dam.demo.util.SoundUtil;
import com.jme3.app.SimpleApplication;
import com.jme3.font.BitmapText;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.util.List;
import java.util.function.BooleanSupplier;

public final class MenuContext implements GameContext {

  private static final int OFFSET = 100;

  private final Node guiNode;
  private final Node spatial;
  private final List<MenuOption> options;

  private int index;

  public MenuContext(SimpleApplication app) {
    this.guiNode = app.getGuiNode();
    this.spatial = new Node("menu");
    MenuAction noOp = () -> {
    };
    this.options = List.of(
        createOption("Continue",
            () -> Contexts.contextByClass(LevelContext.class).inGame(),
            () -> Contexts.switchContext(LevelContext.class)
        ),
        createOption("New Game", () -> true, () -> {
          Contexts.contextByClass(LevelContext.class).reset();
          Contexts.switchContext(LevelContext.class);
        }),
        createOption("Save", () -> false, noOp),
        createOption("Load", () -> false, noOp),
        createOption("High Score", () -> false, noOp),
        createOption("Exit", () -> true, app::stop)
    );
  }

  @Override
  public void enable() {
    availableOptions()
        .forEach(x -> spatial.attachChild(x.spatial()));

    guiNode.attachChild(spatial);
    index = 0;
  }

  @Override
  public void onTick(float tpf) {
    var available = availableOptions();
    for (int i = 0; i < available.size(); i++) {
      var size = i == index ? 60 : 40;
      available.get(i).spatial().setSize(size);
    }
    centerMenu();
  }

  private void centerMenu() {
    var entries = spatial.getChildren();

    var height = entries.stream()
        .map(x -> (BitmapText) x)
        .mapToDouble(x -> x.getHeight() + OFFSET)
        .sum() - OFFSET;
    var offset = (float) (screenHeight() - height) / 2f;
    for (Spatial value : entries) {
      var entry = (BitmapText) value;
      var x = (screenWidth() - entry.getLineWidth()) / 2f;
      entry.setLocalTranslation(x, screenHeight() - offset, 0);
      offset += entry.getHeight() + OFFSET;
    }
  }

  @Override
  public void disable() {
    spatial.detachAllChildren();
    spatial.removeFromParent();
  }

  @Override
  public void onInput(Input input, boolean isPressed) {
    if (!isPressed || input == Input.SHOOT || input == Input.PAUSE) {
      return;
    }
    if (input == Input.SELECT) {
      availableOptions().get(index).action().select();
      return;
    }
    index = input == Input.UP
        ? moveIndex(-1)
        : moveIndex(1);
  }

  private int moveIndex(int amount) {
    SoundUtil.play("menuMove");
    var options = availableOptions();
    return LangUtil.clamp(index + amount, 0, options.size() - 1);
  }

  private static MenuOption createOption(String message, BooleanSupplier enabled,
      MenuAction action) {
    var text = text(40);
    text.setText(message);

    return new MenuOption(text, message, enabled, action);
  }

  private List<MenuOption> availableOptions() {
    return options.stream()
        .filter(x -> x.enabled().getAsBoolean())
        .toList();
  }

  private record MenuOption(BitmapText spatial, String message, BooleanSupplier enabled,
                            MenuAction action) {

  }

  @FunctionalInterface
  private interface MenuAction {

    void select();

  }
}
