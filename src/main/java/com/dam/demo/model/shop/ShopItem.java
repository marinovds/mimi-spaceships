package com.dam.demo.model.shop;

import static com.dam.demo.model.shop.ShopUtil.getNextLevel;

import com.dam.demo.model.spaceship.Spaceship;
import com.dam.demo.model.upgrade.Upgrade;
import com.dam.demo.model.upgrade.UpgradeType;
import java.util.function.Consumer;

public record ShopItem(
    String image,
    String name,
    int maxLevel,
    int baseCost,
    int costIncrease,
    Consumer<Spaceship> bought) {

  public static ShopItem endlessUpgrade(String image, String name, UpgradeType upgrade) {
    return new ShopItem(
        image,
        name,
        Integer.MAX_VALUE,
        200,
        25,
        x -> x.addUpgrade(new Upgrade(20 * getNextLevel(upgrade, x), upgrade))
    );
  }

  public static ShopItem endless(String image, String name, Consumer<Spaceship> f) {
    return new ShopItem(
        image,
        name,
        Integer.MAX_VALUE,
        200,
        25,
        f
    );
  }

  public static ShopItem cannon(String image, String name, int baseCost) {
    return new ShopItem(
        image,
        name,
        3,
        baseCost,
        50,
        x -> {
          // Cannons are based on level properties
        }
    );
  }
}
