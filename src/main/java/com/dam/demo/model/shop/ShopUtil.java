package com.dam.demo.model.shop;

import static com.dam.demo.model.UserConstants.BASE_HEALTH;
import static com.dam.demo.model.UserConstants.MAX_HEALTH;

import com.dam.demo.model.spaceship.Spaceship;
import com.dam.demo.model.upgrade.UpgradeType;
import com.dam.demo.util.MathUtil;
import java.util.List;

public enum ShopUtil {
  ;

  public static final String DAMAGE = "Damage";
  public static final String SHOT_SPEED = "Shot Speed";
  public static final String MOVEMENT_SPEED = "Movement Speed";
  public static final String ATTACK_SPEED = "Attack Speed";
  public static final String ROCKETS = "Extra Rockets";
  public static final String CANNONS = "Extra Cannons";
  public static final String HEALTH = "Max Health";

  public static final List<ShopItem> SHOP_ITEMS = List.of(
      ShopItem.endlessUpgrade("buffDamage", DAMAGE, UpgradeType.ATTACK_DAMAGE),
      ShopItem.endlessUpgrade("buffShot", SHOT_SPEED, UpgradeType.SHOT_SPEED),
      ShopItem.endlessUpgrade("buffSpeed", MOVEMENT_SPEED, UpgradeType.MOVEMENT_SPEED),
      ShopItem.endlessUpgrade("buffAttack", ATTACK_SPEED, UpgradeType.ATTACK_SPEED),
      ShopItem.cannon("rocket", ROCKETS, 800),
      ShopItem.cannon("bullet", CANNONS, 533),
      ShopItem.endless("buffHeart", HEALTH, x -> {
        var spatial = x.spatial();
        int base = spatial.getUserData(BASE_HEALTH);
        var level = getNextLevel(HEALTH, x);
        var maxHealth = MathUtil.increase(base, 20, level);
        spatial.setUserData(MAX_HEALTH, maxHealth);
      })
  );

  public static void setLevel(String name, int level, Spaceship spaceship) {
    spaceship.spatial().setUserData(key(name), level);
  }

  public static int getNextLevel(String name, Spaceship spaceship) {
    return getLevel(name, spaceship) + 1;
  }

  public static int getNextLevel(UpgradeType upgrade, Spaceship spaceship) {
    return getLevel(upgradeName(upgrade), spaceship) + 1;
  }

  public static int getLevel(String name, Spaceship spaceship) {
    var currentLevel = spaceship.spatial().<Integer>getUserData(key(name));
    return currentLevel == null ? 0 : currentLevel;
  }

  public static int getMaxLevel(String name) {
    return SHOP_ITEMS.stream()
        .filter(x -> x.name().equals(name))
        .mapToInt(ShopItem::maxLevel)
        .findFirst()
        .orElseThrow();
  }

  private static String upgradeName(UpgradeType upgrade) {
    return switch (upgrade) {
      case SHOT_SPEED -> SHOT_SPEED;
      case ATTACK_SPEED -> ATTACK_SPEED;
      case ATTACK_DAMAGE -> DAMAGE;
      case MOVEMENT_SPEED -> MOVEMENT_SPEED;
    };
  }

  private static String key(String name) {
    return name + " Level";
  }
}
