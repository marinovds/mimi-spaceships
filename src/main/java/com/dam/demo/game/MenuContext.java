package com.dam.demo.game;

import com.dam.demo.listeners.KeyboardListener.Input;
import com.dam.demo.model.menu.Menu;
import com.dam.demo.model.menu.MenuAction;
import com.dam.demo.model.menu.MenuConfig;
import com.dam.demo.model.menu.MenuEntry;
import com.dam.demo.model.menu.MenuUtil;
import com.jme3.app.SimpleApplication;
import com.jme3.scene.Node;
import java.util.List;
import java.util.function.BooleanSupplier;

public final class MenuContext implements GameContext {

  private static final MenuAction NO_OP = () -> {
  };
  private static final int OFFSET = 60;

  private final Node guiNode;
  private final Menu menu;

  public MenuContext(SimpleApplication app) {
    this.guiNode = app.getGuiNode();

    var options = List.of(
        createOption(
            "Continue",
            () -> Contexts.contextByClass(LevelContext.class).inGame(),
            Contexts::switchLastContext
        ),
        createOption(
            "New Game",
            () -> true,
            () -> {
              Contexts.contextByClass(LevelContext.class).reset();
              Contexts.switchContext(LevelContext.class);
            }),
        createOption("Save", () -> false, NO_OP),
        createOption("Load", () -> false, NO_OP),
        createOption("High Score", () -> false, NO_OP),
        MenuUtil.selectableText("Exit", app::stop, 60, 40)
    );
    var config = new MenuConfig("menu", false, true, "menuMove", OFFSET);
    this.menu = Menu.of(options, config);
  }

  @Override
  public void enable() {
    menu.enable();
    guiNode.attachChild(menu.spatial());
  }

  @Override
  public void onTick(float tpf) {
    menu.onTick();
  }

  @Override
  public void disable() {
    menu.spatial().removeFromParent();
  }

  @Override
  public void onInput(Input input, boolean isPressed) {
    menu.onInput(input, isPressed);
  }

  private static MenuEntry createOption(String message, BooleanSupplier enabled,
      MenuAction action) {
    return MenuUtil.text(
        message,
        enabled,
        action,
        60,
        40);
  }

}
