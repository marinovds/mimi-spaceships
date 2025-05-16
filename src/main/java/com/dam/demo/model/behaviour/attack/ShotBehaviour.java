package com.dam.demo.model.behaviour.attack;

import static com.dam.demo.model.upgrade.UpgradeUtil.upgradeShot;

import com.dam.demo.enemies.Tag.ProjectileType;
import com.dam.demo.enemies.Tag.ShipType;
import com.dam.demo.game.Contexts;
import com.dam.demo.game.LevelContext;
import com.dam.demo.game.Scene;
import com.dam.demo.model.Ticker;
import com.dam.demo.model.attack.Shot;
import com.dam.demo.model.spaceship.Spaceship;
import com.dam.demo.model.upgrade.Upgrade;
import com.dam.demo.util.AssetUtil;
import com.dam.demo.util.SoundUtil;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.time.Duration;
import java.util.List;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

public class ShotBehaviour implements AttackBehaviour {

  private final ShipType shooter;
  private final Supplier<Vector3f> location;
  private final Vector3f aim;
  private final Shot shot;
  private final Ticker ticker;

  public ShotBehaviour(ShipType shooter, Vector3f aim, Supplier<Vector3f> location, Shot shot) {
    this.shooter = shooter;
    this.location = location;
    this.shot = shot;
    this.aim = aim;
    this.ticker = Ticker.of(Duration.ZERO);
  }

  public ShotBehaviour(ShipType shooter, Supplier<Vector3f> location, Shot shot) {
    this(shooter, getAim(shooter), location, shot);
  }

  @Override
  public boolean tryAttack(List<Upgrade> buffs) {
    if (!ticker.isDone()) {
      return false;
    }
    var shot = upgradeShot(this.shot, buffs);
    ticker.reset(shot.cooldown());
    var proj = shoot(location.get(), aim, shot);
    var node = shooter == ShipType.PLAYER ? Scene.PLAYER_BULLETS : Scene.ENEMY_BULLETS;
    node.attachChild(proj);
    return true;
  }

  @Override
  public void tick(float tpf) {
    ticker.tick(tpf);
  }

  private Spatial shoot(Vector3f location, Vector3f aim, Shot shot) {
    var spatial = switch (shot.damage().type()) {
      case BULLET -> bullet(shooter);
      case ROCKET -> rocket(shooter);
      case COLLISION -> throw new UnsupportedOperationException();
    };

    spatial.addControl(new ShotProjectileControl(aim, shot, targets(shooter)));
    spatial.setLocalTranslation(location);

    return spatial;
  }

  private static Spatial rocket(ShipType shooter) {
    var rocket = AssetUtil.projectile("rocket", ProjectileType.ROCKET);
    if (shooter == ShipType.PLAYER) {
      return rocket;
    }

    var color = AssetUtil.getColor(rocket);
    AssetUtil.setColor(rocket, color.add(ColorRGBA.Red));
    rocket.rotate(0, 0, FastMath.PI);
    SoundUtil.play("shot");

    return rocket;
  }

  private static Spatial bullet(ShipType shooter) {
    var bullet = AssetUtil.projectile("bullet", ProjectileType.BULLET);
    if (shooter == ShipType.PLAYER) {
      SoundUtil.play("shot");

      return bullet;
    }

    AssetUtil.setColor(bullet, new ColorRGBA(256f, 0f, 0f, 0.3f));
    bullet.rotate(0, 0, FastMath.PI);
    SoundUtil.play("enemyShot");

    return bullet;
  }

  private static Supplier<List<Spatial>> targets(ShipType shooter) {
    return () -> (shooter == ShipType.PLAYER ? playerTargets() : enemyTargets()).toList();
  }

  private static Stream<Spatial> enemyTargets() {
    var level = Contexts.contextByClass(LevelContext.class);
    return Stream.concat(
        Stream.of(level.player.spatial()),
        rockets(Scene.PLAYER_BULLETS)
    );
  }

  private static Stream<Spatial> playerTargets() {
    return Stream.concat(
        Scene.ENEMIES.getChildren().stream(),
        rockets(Scene.ENEMY_BULLETS)
    );
  }

  private static Stream<Spatial> rockets(Node node) {
    return node.getChildren()
        .stream()
        .filter(x -> x.getName().equals("rocket"));
  }

  private static Vector3f getAim(ShipType shooter) {
    if (shooter == ShipType.PLAYER) {
      return new Vector3f(1, 0, 0);
    }

    return new Vector3f(-1, 0, 0);
  }

  public static Supplier<Vector3f> offset(Spaceship spaceship, int numberOfCannons) {

    return offset(spaceship, numberOfCannons, UnaryOperator.identity());
  }

  public static Supplier<Vector3f> offsetNegate(Spaceship spaceship, int numberOfCannons) {

    return offset(spaceship, numberOfCannons, Vector3f::negate);
  }

  private static Supplier<Vector3f> offset(
      Spaceship spaceship,
      int numberOfCannons,
      UnaryOperator<Vector3f> f) {
    var offset = new Vector3f(0, spaceship.dimensions().height() / numberOfCannons, 0);

    return () -> spaceship.location().add(f.apply(offset));
  }
}
