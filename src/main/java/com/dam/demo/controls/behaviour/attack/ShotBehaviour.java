package com.dam.demo.controls.behaviour.attack;

import static com.dam.demo.controls.behaviour.ControlsUtil.getAim;
import static com.dam.demo.game.Scene.ENEMY_BULLETS;
import static com.dam.demo.game.Scene.PLAYER_BULLETS;

import com.dam.demo.enemies.Tag.ShipType;
import com.dam.demo.game.Scene;
import com.dam.demo.game.SoundUtil;
import com.dam.demo.model.Spaceship;
import com.dam.demo.model.attack.Attack;
import com.dam.demo.model.attack.Attack.Shot;
import com.dam.demo.util.AssetUtil;
import com.dam.demo.util.MathUtil;
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

  private Attack.Shot attack;
  private Duration cooldown;

  public ShotBehaviour(Spaceship spaceship, Attack.Shot shot) {
    this.spaceship = spaceship;
    this.attack = shot;
    this.aim = getAim(spaceship);
    this.cooldown = Duration.ZERO;
  }

  @Override
  public void onTick(float tpf) {
    if (cooldown.isPositive()) {
      cooldown = MathUtil.subtractDuration(cooldown, tpf);
      return;
    }
    cooldown = attack.cooldown();
    var shot = shoot(spaceship, aim, attack);
    var node = spaceship.is(ShipType.PLAYER) ? PLAYER_BULLETS : ENEMY_BULLETS;
    node.attachChild(shot);
  }

  private static Spatial shoot(Spaceship spaceship, Vector3f aim, Attack.Shot shot) {
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
    return Stream.concat(
        Stream.of(Scene.PLAYER.spatial()),
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

  @Override
  public Shot getAttack() {
    return attack;
  }

  public ShotBehaviour updateAttack(Shot attack) {
    this.attack = attack;

    return this;
  }
}
