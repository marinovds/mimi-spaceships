package com.dam.demo.model.behaviour.attack;

import static com.dam.demo.model.upgrade.UpgradeUtil.upgradeShot;
import static com.dam.demo.util.MathUtil.getAimDirection;

import com.dam.demo.enemies.Tag.ShipType;
import com.dam.demo.game.context.Contexts;
import com.dam.demo.game.context.LevelContext;
import com.dam.demo.model.attack.Shot;
import com.dam.demo.model.spaceship.Spaceship;
import com.dam.demo.model.upgrade.Upgrade;
import com.dam.demo.util.AssetUtil;
import com.dam.demo.util.MathUtil;
import com.dam.demo.util.SoundUtil;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.time.Duration;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class ShotBehaviour implements AttackBehaviour {

  private final Spaceship spaceship;
  private final Vector3f aim;

  private final Shot shot;
  private Duration cooldown;

  public ShotBehaviour(Spaceship spaceship, Shot shot) {
    this.spaceship = spaceship;
    this.shot = shot;
    this.aim = getAim(spaceship);
    this.cooldown = Duration.ZERO;
  }

  @Override
  public boolean tryAttack(List<Upgrade> buffs, float tpf) {
    if (cooldown.isPositive()) {
      tick(tpf);
      return false;
    }
    var shot = upgradeShot(this.shot, buffs);
    cooldown = shot.cooldown();
    var proj = shoot(spaceship, aim, shot);
    var level = Contexts.contextByClass(LevelContext.class);
    var node = spaceship.is(ShipType.PLAYER) ? level.playerBullets : level.enemyBullets;
    node.attachChild(proj);
    return true;
  }

  @Override
  public void tick(float tpf) {
    cooldown = MathUtil.subtractDuration(cooldown, tpf);
  }

  private static Spatial shoot(Spaceship spaceship, Vector3f aim, Shot shot) {
    var spatial = switch (shot.damage().type()) {
      case BULLET -> bullet(spaceship);
      case ROCKET -> rocket();
      case COLLISION -> throw new UnsupportedOperationException();
    };

    spatial.addControl(new ShotProjectileControl(aim, shot, targets(spaceship)));
    spatial.setLocalTranslation(translation(spaceship));

    return spatial;
  }

  private static Vector3f translation(Spaceship spaceship) {
    var offset = new Vector3f(spaceship.dimensions().height() / 2, 0, 0);
    if (spaceship.is(ShipType.PLAYER)) {
      return spaceship.location().add(offset);
    }
    return spaceship.location().add(offset.negate());
  }

  private static Spatial rocket() {
    var rocket = AssetUtil.projectile("rocket");
    SoundUtil.play("shot");
    return rocket;
  }

  private static Spatial bullet(Spaceship spaceship) {
    var bullet = AssetUtil.projectile("bullet");
    if (spaceship.is(ShipType.PLAYER)) {
      SoundUtil.play("shot");

      return bullet;
    }

    AssetUtil.setColor(bullet, new ColorRGBA(256f, 0f, 0f, 0.3f));
    bullet.rotate(0, 0, FastMath.PI);
    SoundUtil.play("enemyShot");

    return bullet;
  }

  private static Supplier<List<Spatial>> targets(Spaceship spaceship) {
    return () -> (spaceship.is(ShipType.PLAYER) ? playerTargets() : enemyTargets()).toList();
  }

  private static Stream<Spatial> enemyTargets() {
    var level = Contexts.contextByClass(LevelContext.class);
    return Stream.concat(
        Stream.of(level.player.spatial()),
        rockets(level.playerBullets)
    );
  }

  private static Stream<Spatial> playerTargets() {
    var level = Contexts.contextByClass(LevelContext.class);
    return Stream.concat(
        level.enemies.getChildren().stream(),
        rockets(level.enemyBullets)
    );
  }

  private static Stream<Spatial> rockets(Node node) {
    return node.getChildren()
        .stream()
        .filter(x -> x.getName().equals("rocket"));
  }

  private static Vector3f getAim(Spaceship spaceship) {
    if (spaceship.is(ShipType.ENEMY) || spaceship.is(ShipType.BOSS)) {
      return getAimDirection(spaceship.location()).negate();
    }
    return getAimDirection(spaceship.location());
  }
}
