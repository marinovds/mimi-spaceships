package com.dam.demo.game;

import static com.dam.demo.util.AssetUtil.manager;

import com.dam.demo.listeners.KeyboardListener.Input;
import com.dam.demo.model.menu.Menu;
import com.dam.demo.model.menu.MenuAction;
import com.dam.demo.model.menu.MenuConfig;
import com.dam.demo.model.menu.MenuEntry;
import com.dam.demo.model.menu.MenuUtil;
import com.jme3.app.SimpleApplication;
import com.jme3.export.binary.BinaryExporter;
import com.jme3.scene.Node;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class MenuContext implements GameContext {

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
        createOption("High Score", () -> false, () -> {
        }),
        MenuUtil.selectableText(
            "Exit",
            () -> {
              save();
              app.stop();
            },
            60,
            40)
    );
    var config = new MenuConfig("menu", false, true, "menuMove", OFFSET);
    this.menu = Menu.of(options, config);
  }

  @Override
  public void enable() {
    load();
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

  private static Path savefilePath() {
    return Path.of(
        System.getProperty("user.home"),
        "Documents",
        "My Games",
        "MiMi Spaceships",
        "save.j3o");
  }

  private static void save() {
    var exporter = BinaryExporter.getInstance();
    try {
      var levelContext = Contexts.contextByClass(LevelContext.class);
      var saveNode = levelContext.saveData();
      exporter.save(saveNode, savefilePath().toFile());
    } catch (IOException ex) {
      Logger.getLogger("save").log(Level.SEVERE, "Error: Failed to save game!", ex);
    }
  }

  private static void load() {
    var saveFile = savefilePath();
    if (!Files.exists(saveFile)) {
      return;
    }

    var savedGame = (Node) manager.loadModel("/Documents/My Games/MiMi Spaceships/save.j3o");
    try {
      Contexts.contextByClass(LevelContext.class).loadGame(savedGame);
      Files.delete(saveFile);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
