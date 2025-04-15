package com.dam.demo.game;

import static com.dam.demo.model.shop.ShopUtil.SHOP_ITEMS;

import com.dam.demo.listeners.KeyboardListener.Input;
import com.dam.demo.model.menu.Menu;
import com.dam.demo.model.menu.MenuAction;
import com.dam.demo.model.menu.MenuConfig;
import com.dam.demo.model.menu.MenuEntry;
import com.dam.demo.model.menu.MenuUtil;
import com.dam.demo.model.shop.ShopMenuEntry;
import com.dam.demo.util.AssetUtil;
import com.dam.demo.util.SoundUtil;
import com.jme3.app.SimpleApplication;
import com.jme3.font.BitmapText;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.util.ArrayList;
import java.util.List;

public final class ShopContext implements GameContext {

  private final Node guiNode;
  private final PlayerCoins coins;
  private final List<ShopMenuEntry> shopItems;
  private final Menu menu;

  ShopContext(SimpleApplication app) {
    this.guiNode = app.getGuiNode();
    this.coins = PlayerCoins.of();
    this.shopItems = SHOP_ITEMS.stream()
        .map(ShopMenuEntry::of)
        .toList();

    var config = new MenuConfig(
        "shop",
        true,
        true,
        "menuMove",
        30);
    this.menu = Menu.of(options(coins, shopItems), config);
  }

  private List<MenuEntry> options(PlayerCoins coins, List<ShopMenuEntry> shopItems) {
    var result = new ArrayList<MenuEntry>();
    result.add(coins);
    result.addAll(shopItems);
    result.add(nextLevel());

    return result;
  }

  private static MenuEntry nextLevel() {
    return MenuUtil.selectableText("Next Level",
        () -> {
          Contexts.contextByClass(LevelContext.class).nextLevel();
          Contexts.switchContext(LevelContext.class);
        },
        40,
        30);
  }

  @Override
  public void enable() {
    SoundUtil.music("victory");
    menu.enable();
    guiNode.attachChild(menu.spatial());
  }

  @Override
  public void onTick(float tpf) {
    coins.update();
    shopItems.forEach(ShopMenuEntry::update);
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

  private record PlayerCoins(
      Spatial spatial,
      BitmapText amount
  ) implements MenuEntry {

    public static PlayerCoins of() {
      var result = new Node("playerCoins");
      var coins = AssetUtil.text("Coins:", 40);
      var amount = AssetUtil.text( 40);
      amount.setLocalTranslation(512 - amount.getLineWidth(), 0, 0);

      result.attachChild(coins);
      result.attachChild(amount);

      return new PlayerCoins(result, amount);
    }

    public void update() {
      var player = Contexts.contextByClass(LevelContext.class).player;
      amount.setText(player.coins() + "");
    }

    @Override
    public float height() {
      return ShopMenuEntry.HEIGHT;
    }

    @Override
    public float width() {
      return ShopMenuEntry.WIDTH;
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
      return spatial;
    }
  }
}
