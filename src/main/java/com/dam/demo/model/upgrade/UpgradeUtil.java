package com.dam.demo.model.upgrade;

import static com.dam.demo.util.MathUtil.apply;
import static com.dam.demo.util.MathUtil.decreaseDuration;

import com.dam.demo.model.attack.Damage;
import com.dam.demo.model.attack.Shot;
import com.dam.demo.util.JsonUtil;
import com.dam.demo.util.MathUtil;
import java.util.List;

public enum UpgradeUtil {
  ;

  public static Shot upgradeShot(Shot shot, List<Upgrade> upgrades) {
    if (upgrades.isEmpty()) {
      return shot;
    }
    var result = shot;
    for (var upgrade : upgrades) {
      result = upgradeShot(shot, upgrade);
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
