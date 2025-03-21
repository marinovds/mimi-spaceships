package com.dam.demo.game.context;

import com.dam.demo.listeners.KeyboardListener.Action;
import com.dam.demo.listeners.KeyboardListener.Input;
import com.jme3.app.SimpleApplication;

public class Contexts {

  private static MenuContext menu;
  private static LevelContext level;
  private static ShopContext shop;
  private static HighScoreContext highScore;

  private static GameContext currentContext;

  public static void initialize(SimpleApplication app) {
    Contexts.menu = new MenuContext(app);
    Contexts.level = new LevelContext(app);
    Contexts.shop = new ShopContext(app);
    Contexts.highScore = new HighScoreContext(app);

    currentContext = menu;
    currentContext.enable();
  }

  public static void onInput(Input input, Action action) {
    if (input == Input.PAUSE && action == Action.PRESS) {
      if (currentContext == menu) {
        // Already in menu
        return;
      }
      switchContext(MenuContext.class);
      return;
    }
    currentContext.onInput(input, action);
  }

  public static void switchContext(Class<? extends GameContext> context) {
    var actual = contextByClass(context);
    currentContext.disable();
    currentContext = actual;
    currentContext.enable();
  }

  public static <T extends  GameContext> T contextByClass(Class<T> clazz) {
    if (clazz == MenuContext.class) {
      return (T) menu;
    }
    if (clazz == LevelContext.class) {
      return (T) level;
    }
    if (clazz == HighScoreContext.class) {
      return (T) highScore;
    }
    if (clazz == ShopContext.class) {
      return (T) shop;
    }
    throw new IllegalArgumentException("Unknown Game context provided " + clazz.getSimpleName());
  }

  public static void onTick(float tpf) {
    currentContext.onTick(tpf);
  }
}
