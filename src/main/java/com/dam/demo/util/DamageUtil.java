package com.dam.demo.util;

import static com.dam.demo.model.UserConstants.HEALTH;
import static com.dam.demo.model.UserConstants.LAST_HIT;
import static com.dam.demo.util.MathUtil.isDead;
import static java.lang.Math.max;

import com.dam.demo.controls.ParticleManager;
import com.dam.demo.enemies.Tag.ArmorType;
import com.dam.demo.enemies.Tag.ProjectileType;
import com.dam.demo.enemies.Tag.ShipType;
import com.dam.demo.game.Contexts;
import com.dam.demo.game.LevelContext;
import com.dam.demo.model.attack.Damage;
import com.dam.demo.model.spaceship.Spaceship;
import com.dam.demo.model.upgrade.UpgradeUtil;
import com.jme3.scene.Spatial;
import java.time.Instant;

public enum DamageUtil {
  ;

  public static boolean hit(Spatial spatial, Damage damage) {
    if (ProjectileType.ROCKET.is(spatial)) {
      // This is a rocket. It should explode on impact
      spatial.removeFromParent();
      return true;
    }

    return hit(Spaceship.of(spatial), damage);
  }

  public static boolean hit(Spaceship spaceship, Damage damage) {
    if (spaceship.is(ShipType.PLAYER)) {
      return hitPlayer(spaceship, damage);
    }

    return spaceship.is(ShipType.BOSS)
        ? hitBoss(spaceship, damage)
        : hitEnemy(spaceship, damage);
  }

  private static boolean hitBoss(Spaceship boss, Damage damage) {
    var health = calculateHealth(boss, damage);
    boss.spatial().setUserData(HEALTH, health);
    if (isDead(boss)) {
      SoundUtil.play("explode");
      Contexts.contextByClass(LevelContext.class).player.addCoins(boss.coins())
          .addPoints(boss.points());
      ParticleManager.explosion(boss.location(), 200);
      Contexts.contextByClass(LevelContext.class).bossKilled();
      boss.spatial().removeFromParent();
      return true;
    }

    var audio = boss.is(ArmorType.HEAVY) ? "heavyHit" : "lightHit";
    SoundUtil.play(audio);
    return true;
  }

  private static boolean hitEnemy(Spaceship spaceship, Damage damage) {
    var health = calculateHealth(spaceship, damage);
    spaceship.spatial().setUserData(HEALTH, health);
    if (isDead(spaceship)) {
      SoundUtil.play("explode");
      Contexts.contextByClass(LevelContext.class).player.addCoins(spaceship.coins())
          .addPoints(spaceship.points());
      spaceship.spatial().removeFromParent();
      ParticleManager.explosion(spaceship.location(), 20);
      UpgradeUtil.spawnBonus(spaceship.location());
      return true;
    }
    var audio = spaceship.is(ArmorType.HEAVY) ? "heavyHit" : "lightHit";
    SoundUtil.play(audio);

    return true;
  }

  private static boolean hitPlayer(Spaceship player, Damage damage) {
    // TODO: Add player invincibility when hit
    var health = calculateHealth(player, damage);
    player.spatial().setUserData(HEALTH, health);
    player.spatial().setUserData(LAST_HIT, Instant.now().toString());
    if (isDead(player)) {
      ParticleManager.explosion(player.location(), 300);
      Contexts.contextByClass(LevelContext.class).playerKilled();
      return true;
    }

    SoundUtil.play("hit");
    return true;
  }

  private static Instant getLastHit(Spaceship spaceship) {
    var hit = (String) spaceship.spatial().getUserData(LAST_HIT);

    return hit == null ? null : Instant.parse(hit);
  }

  private static int calculateHealth(Spaceship target, Damage attack) {

    return max(0, target.health() - attack.amount());
  }
}
