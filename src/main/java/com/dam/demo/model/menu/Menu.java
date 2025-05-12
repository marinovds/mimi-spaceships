package com.dam.demo.model.menu;

import static com.dam.demo.util.AssetUtil.screenHeight;
import static com.dam.demo.util.AssetUtil.screenWidth;

import com.dam.demo.listeners.KeyboardListener.Input;
import com.dam.demo.util.LangUtil;
import com.dam.demo.util.SoundUtil;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.util.List;

public final class Menu {

  private final List<MenuEntry> entries;
  private final MenuConfig config;
  private final Node spatial;

  private int index;

  private Menu(List<MenuEntry> entries, MenuConfig config) {
    this.entries = entries;
    this.config = config;
    this.spatial = new Node(config.name());
  }

  public Menu enable() {
    var entries = entries();
    entries.forEach(x -> spatial.attachChild(x.spatial()));
    index = 0;

    return this;
  }

  public Menu disable() {
    spatial.detachAllChildren();
    spatial.removeFromParent();

    return this;
  }

  public void onTick() {
    var available = availableEntries();
    for (int i = 0; i < available.size(); i++) {
      available.get(i).onCursor(i == index);
    }
    centerMenu();
  }

  public static Menu of(List<? extends MenuEntry> entries, MenuConfig config) {
    return new Menu((List<MenuEntry>) entries, config);
  }

  public void onInput(Input input, boolean isPressed) {
    if (!isPressed || input == Input.SHOOT || input == Input.PAUSE) {
      return;
    }
    var entries = availableEntries();
    if (input == Input.SELECT) {
      var entry = entries.get(index);
      entry.action().select();
      if (!entry.selectable()) {
        entry.onCursor(false);
        index = 0;
      }
      return;
    }
    index = input == Input.UP
        ? moveIndex(-1)
        : moveIndex(1);
  }

  public <T extends MenuEntry> T getEntry(int index) {
    return (T) entries.get(index);
  }

  private int moveIndex(int delta) {
    SoundUtil.play(config.movementSound());
    var lastIndex = availableEntries().size() - 1;
    if (!config.rotaryIndex()) {
      return LangUtil.clamp(index + delta, 0, lastIndex);
    }
    if (index == 0 && delta < 0) {
      return lastIndex;
    }
    if (index == lastIndex && delta > 0) {
      return 0;
    }
    return index + delta;
  }

  public Spatial spatial() {
    return spatial;
  }

  private List<MenuEntry> availableEntries() {
    return entries.stream()
        .filter(MenuEntry::selectable)
        .toList();
  }

  private void centerMenu() {
    var baseOffset = config.offset();
    var height = entries().stream()
        .mapToDouble(x -> x.height() + baseOffset)
        .sum() - baseOffset;
    var offset = (float) (screenHeight() - height) / 2f;
    for (var entry : entries()) {
      var spatial = entry.spatial();
      var x = (screenWidth() - entry.width()) / 2f;
      spatial.setLocalTranslation(x, screenHeight() - offset, 0);
      offset += entry.height() + baseOffset;
    }
  }

  private List<MenuEntry> entries() {
    return entries.stream()
        .filter(x -> config.showNonSelectable() || x.selectable())
        .toList();
  }
}
