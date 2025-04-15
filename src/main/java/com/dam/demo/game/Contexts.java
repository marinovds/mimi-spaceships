package com.dam.demo.game;

import com.dam.demo.listeners.KeyboardListener.Input;
import com.jme3.app.SimpleApplication;

public class Contexts {

  private static MenuContext menu;
  private static LevelContext level;
  private static ShopContext shop;
  private static HighScoreContext highScore;

  private static GameContext currentContext;
  private static GameContext lastContext;

  public static void initialize(SimpleApplication app) {
    Contexts.menu = new MenuContext(app);
    Contexts.level = new LevelContext(app);
    Contexts.shop = new ShopContext(app);
    Contexts.highScore = new HighScoreContext(app);

    currentContext = menu;
    lastContext = null;
    currentContext.enable();
  }

  public static void onInput(Input input, boolean isPressed) {
    if (input == Input.PAUSE && isPressed) {
      if (currentContext == menu) {
        if (lastContext == null) {
          return;
        }

        switchContext(lastContext);
        return;
      }
      switchContext(menu);
      return;
    }

    currentContext.onInput(input, isPressed);
  }

  public static void switchContext(Class<? extends GameContext> context) {
    var actual = contextByClass(context);
    switchContext(actual);
  }

  private static void switchContext(GameContext context) {
    currentContext.disable();
    lastContext = currentContext;
    currentContext = context;
    currentContext.enable();
  }

  public static void switchLastContext() {
    switchContext(lastContext);
  }

  public static <T extends GameContext> T contextByClass(Class<T> clazz) {
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
