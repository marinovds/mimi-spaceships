package com.dam.demo.util;

import static com.dam.demo.game.Scene.BUFFS;
import static com.dam.demo.util.MathUtil.getAimDirection;
import static com.dam.util.RandomUtil.Option.option;
import static com.dam.util.RandomUtil.RANDOM;
import static com.dam.util.RandomUtil.weighted;

import com.dam.demo.controls.BonusControl;
import com.dam.demo.model.Spaceship;
import com.dam.demo.model.upgrade.Buff;
import com.dam.demo.model.upgrade.Upgrade;
import com.dam.demo.model.upgrade.UpgradeType;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import java.time.Duration;
import java.util.function.Consumer;
import java.util.function.Supplier;

public enum BonusUtil {
  ;

  private static final Buff DAMAGE_BUFF = new Buff(
      new Upgrade(100, UpgradeType.ATTACK_DAMAGE),
      Duration.ofSeconds(10),
      ColorRGBA.Red
  );

  private static final Buff SPEED_BUFF = new Buff(
      new Upgrade(50, UpgradeType.MOVEMENT_SPEED),
      Duration.ofSeconds(10),
      ColorRGBA.Blue
  );

  private static final Buff ATTACK_BUFF = new Buff(
      new Upgrade(30, UpgradeType.ATTACK_SPEED),
      Duration.ofSeconds(10),
      ColorRGBA.Green
  );

  private static final Buff SHOT_BUFF = new Buff(
      new Upgrade(100, UpgradeType.SHOT_SPEED),
      Duration.ofSeconds(10),
      ColorRGBA.Yellow
  );

  public static void spawnBonus(Vector3f location) {
    if (RANDOM.nextInt(4) != 0) {
      return;
    }
    var buff = weighted(
        option(1, bonus("coin", location, s -> s.addCoins(15))),
        option(1, bonus("heart", location, s -> s.addHealth(20))),
        option(1, bonus("star", location, s -> s.addPoints(300))),
        option(1, bonus("damage", location, s -> s.addBuff(DAMAGE_BUFF))),
        option(1, bonus("speed", location, s -> s.addBuff(SPEED_BUFF))),
        option(1, bonus("attack", location, s -> s.addBuff(ATTACK_BUFF))),
        option(1, bonus("shot", location, s -> s.addBuff(SHOT_BUFF)))
    );

    BUFFS.attachChild(buff);
  }

  private static Supplier<Spatial> bonus(String name, Vector3f location, Consumer<Spaceship> f) {
    return () -> {
      var result = AssetUtil.buff(name);
      result.setLocalTranslation(location);
      result.addControl(new BonusControl(getAimDirection(location).negate(), f));

      return result;
    };
  }
}
