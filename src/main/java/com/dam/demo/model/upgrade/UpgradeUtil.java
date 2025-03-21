package com.dam.demo.model.upgrade;

import static com.dam.demo.game.Scene.BUFFS;
import static com.dam.demo.util.MathUtil.apply;
import static com.dam.demo.util.MathUtil.decreaseDuration;
import static com.dam.demo.util.MathUtil.getAimDirection;
import static com.dam.util.RandomUtil.Option.option;
import static com.dam.util.RandomUtil.RANDOM;
import static com.dam.util.RandomUtil.weighted;

import com.dam.demo.controls.BonusControl;
import com.dam.demo.model.Spaceship;
import com.dam.demo.model.attack.Damage;
import com.dam.demo.model.attack.Shot;
import com.dam.demo.util.AssetUtil;
import com.dam.demo.util.JsonUtil;
import com.dam.demo.util.MathUtil;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import java.time.Duration;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public enum UpgradeUtil {
  ;

  public static final ColorRGBA COLOR_SPEED = ColorRGBA.Blue;
  public static final ColorRGBA COLOR_ATTACK_DAMAGE = new ColorRGBA(10, 0, 0, 1);
  public static final ColorRGBA COLOR_ATTACK_SPEED = ColorRGBA.Green;
  public static final ColorRGBA COLOR_SHOT_SPEED = ColorRGBA.LightGray;

  public static void spawnBonus(Vector3f location) {
    if (RANDOM.nextInt(4) != 0) {
      return;
    }
    var buff = weighted(
        option(1, bonus("buffCoin", location, s -> s.addCoins(15))),
        option(1, bonus("buffHeart", location, s -> s.addHealth(20))),
        option(1, bonus("buffStar", location, s -> s.addPoints(300))),

        option(1, bonus("buffDamage", location, buff(new Upgrade(100, UpgradeType.ATTACK_DAMAGE)))),
        option(1, bonus("buffSpeed", location, buff(new Upgrade(50, UpgradeType.MOVEMENT_SPEED)))),
        option(1, bonus("buffAttack", location, buff(new Upgrade(30, UpgradeType.ATTACK_SPEED)))),
        option(1, bonus("buffShot", location, buff(new Upgrade(100, UpgradeType.SHOT_SPEED))))
    );

    BUFFS.attachChild(buff);
  }

  private static Supplier<Spatial> bonus(String name, Vector3f location, Consumer<Spaceship> f) {
    return () -> {
      var result = AssetUtil.bonus(name);
      result.setLocalTranslation(location);
      result.addControl(new BonusControl(getAimDirection(location).negate(), f));

      return result;
    };
  }

  private static Consumer<Spaceship> buff(Upgrade upgrade) {
    var color = switch (upgrade.type()) {
      case HEALTH -> ColorRGBA.White; // Should not happen
      case ATTACK_DAMAGE -> COLOR_ATTACK_DAMAGE;
      case ATTACK_SPEED -> COLOR_ATTACK_SPEED;
      case SHOT_SPEED -> COLOR_SHOT_SPEED;
      case MOVEMENT_SPEED -> COLOR_SPEED;
    };

    return x -> x.addBuff(new Buff(upgrade, Duration.ofSeconds(10), color));
  }

  public static Shot upgradeShot(Shot shot, List<Upgrade> upgrades) {
    if (upgrades.isEmpty()) {
      return shot;
    }
    var result = shot;
    for (var upgrade : upgrades) {
      result = upgradeShot(result, upgrade);
    }
    return result;
  }

  public static Damage upgradeDamage(Damage damage, List<Upgrade> upgrades) {
    return upgrades.stream()
        .filter(x -> x.type() == UpgradeType.ATTACK_DAMAGE)
        .findFirst()
        .map(x -> MathUtil.apply(damage.amount(), x.percentage()))
        .map(x -> new Damage(x, damage.type()))
        .orElse(damage);
  }

  public static String toString(List<Upgrade> upgrades) {
    return JsonUtil.write(upgrades);
  }

  public static Shot upgradeShot(Shot shot, Upgrade upgrade) {
    var percentage = upgrade.percentage();
    return switch (upgrade.type()) {
      case ATTACK_DAMAGE -> new Shot(
          new Damage(apply(shot.damage().amount(), percentage), shot.damage().type()),
          shot.speed(),
          shot.cooldown()
      );
      case ATTACK_SPEED -> new Shot(
          shot.damage(),
          shot.speed(),
          decreaseDuration(shot.cooldown(), percentage)
      );
      case SHOT_SPEED -> new Shot(
          shot.damage(),
          apply(shot.speed(), percentage),
          shot.cooldown()
      );
      case MOVEMENT_SPEED, HEALTH -> shot;
    };
  }
}
