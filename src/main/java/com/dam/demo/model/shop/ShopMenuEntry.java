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

  private static final int OFFSET = 200;
  public static final int WIDTH = AssetUtil.screenWidth() - OFFSET;
  public static final int HEIGHT = 40;

  private final Node spatial;
  private final List<BitmapText> texts;
  private final ShopItem item;

  private ShopMenuEntry(ShopItem item, List<BitmapText> texts, Node spatial) {
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
    level.setName("level");

    var price = AssetUtil.text(30);
    price.setName("price");

    var texts = List.of(
        name,
        level,
        price
    );
    result.attachChild(image);
    texts.forEach(result::attachChild);
    center(price, level);

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
    return !(player.coins() < MathUtil.increase(item.baseCost(), item.costIncrease(), level)
        || level > item.maxLevel());
  }

  @Override
  public void onCursor(boolean state) {
    var scale = state ? 1.3f : 1f;
    spatial.getChildren().forEach(x -> x.setLocalScale(scale));
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
    var nextLevel = ShopUtil.getNextLevel(item.name(), player);

    var level = nextLevel > item.maxLevel()
        ? ""
        : String.valueOf(nextLevel);
    var price = nextLevel > item.maxLevel()
        ? "Sold OUT"
        : String.valueOf(MathUtil.increase(item.baseCost(), item.costIncrease(), nextLevel));

    levelText.setText(level);
    priceText.setText(price);
    center(priceText, levelText);

    var color = selectable() ? ColorRGBA.White : ColorRGBA.Gray;
    texts.forEach(x -> x.setColor(color));
  }

  private static void center(BitmapText price, BitmapText level) {
    var x = AssetUtil.screenWidth() - OFFSET - price.getLineWidth();
    price.setLocalTranslation(x, 0, 0);
    level.setLocalTranslation(x - 30 - level.getLineWidth(), 0, 0);
  }

  private BitmapText get(String name) {
    return texts.stream()
        .filter(x -> name.equals(x.getName()))
        .findFirst()
        .orElseThrow();
  }
}
