package com.dam.demo.model.shop;

import static com.dam.demo.model.shop.ShopUtil.getNextLevel;

import com.dam.demo.game.Contexts;
import com.dam.demo.game.LevelContext;
import com.dam.demo.model.Dimensions;
import com.dam.demo.model.menu.MenuAction;
import com.dam.demo.model.menu.MenuEntry;
import com.dam.demo.util.AssetUtil;
import com.dam.demo.util.MathUtil;
import com.jme3.font.BitmapText;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.util.List;

public class ShopMenuEntry implements MenuEntry {

  public static final int WIDTH = AssetUtil.screenWidth() - 200;
  public static final int HEIGHT = 40;

  private final Spatial spatial;
  private final List<BitmapText> texts;
  private final ShopItem item;

  private ShopMenuEntry(ShopItem item, List<BitmapText> texts, Spatial spatial) {
    this.item = item;
    this.texts = texts;
    this.spatial = spatial;
    update();
  }

  public static ShopMenuEntry of(ShopItem shopItem) {
    var result = new Node(shopItem.name());
    var image = AssetUtil.bonus(shopItem.image());
    var imageDims = Dimensions.of(image);
    image.setLocalTranslation(0, -imageDims.height() / 2, 0);

    var name = AssetUtil.text(shopItem.name(), 30);
    name.setLocalTranslation(imageDims.width() + 10, 0, 0);
    name.setName("name");

    var level = AssetUtil.text(30);
    level.setLocalTranslation(name.getLocalTranslation().x + 400, 0, 0);
    level.setName("level");

    var price = AssetUtil.text(30);
    price.setName("price");
    price.setLocalTranslation(level.getLocalTranslation().x + 50, 0, 0);

    var texts = List.of(
        name,
        level,
        price
    );
    result.attachChild(image);
    texts.forEach(result::attachChild);

    return new ShopMenuEntry(shopItem, texts, result);
  }

  @Override
  public float height() {
    return HEIGHT;
  }

  @Override
  public float width() {
    return WIDTH;
  }

  @Override
  public boolean selectable() {
    var player = Contexts.contextByClass(LevelContext.class).player;
    var level = getNextLevel(item.name(), player);
    return player.coins() >= MathUtil.increase(item.baseCost(), item.costIncrease(), level)
        && level <= item.maxLevel();
  }

  @Override
  public void onCursor(boolean state) {
    var size = state ? 40 : 30;
    texts.forEach(x -> x.setSize(size));
  }

  @Override
  public MenuAction action() {
    return () -> {
      var player = Contexts.contextByClass(LevelContext.class).player;
      var level = ShopUtil.getNextLevel(item.name(), player);
      var price = MathUtil.increase(item.baseCost(), item.costIncrease(), level);
      player.addCoins(-price);
      item.bought().accept(player);
      ShopUtil.setLevel(item.name(), level, player);
    };
  }

  @Override
  public Spatial spatial() {
    return spatial;
  }

  public void update() {
    var player = Contexts.contextByClass(LevelContext.class).player;
    var levelText = get("level");
    var priceText = get("price");
    var level = ShopUtil.getNextLevel(item.name(), player);
    if (level > item.maxLevel()) {
      levelText.setText("");
      priceText.setText("Sold OUT");
      return;
    }
    levelText.setText(level + "");
    var price = MathUtil.increase(item.baseCost(), item.costIncrease(), level);
    priceText.setText(price + "");

    var color = selectable() ? ColorRGBA.White : ColorRGBA.Gray;
    texts.forEach(x -> x.setColor(color));
  }

  private BitmapText get(String name) {
    return texts.stream()
        .filter(x -> name.equals(x.getName()))
        .findFirst()
        .orElseThrow();
  }
}
