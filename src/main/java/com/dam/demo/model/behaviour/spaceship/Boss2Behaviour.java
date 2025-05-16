package com.dam.demo.model.behaviour.spaceship;

import static com.dam.demo.model.behaviour.attack.ShotBehaviour.offset;
import static com.dam.demo.model.behaviour.attack.ShotBehaviour.offsetNegate;
import static com.dam.util.RandomUtil.inBounds;
import static java.lang.Math.abs;
import static java.lang.Math.pow;
import static java.lang.Math.signum;
import static java.lang.Math.sqrt;

import com.dam.demo.enemies.Tag.ShipType;
import com.dam.demo.model.Boundary;
import com.dam.demo.model.attack.Shot;
import com.dam.demo.model.attack.SpaceshipAttack;
import com.dam.demo.model.behaviour.attack.CollisionBehaviour;
import com.dam.demo.model.behaviour.attack.ParallelBehaviour;
import com.dam.demo.model.behaviour.attack.RotaryBehaviour;
import com.dam.demo.model.behaviour.attack.ShotBehaviour;
import com.dam.demo.model.spaceship.Spaceship;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import java.time.Duration;
import java.util.List;

public class Boss2Behaviour implements SpaceshipBehaviour {

  private final Spaceship spaceship;
  private final RotaryBehaviour behaviour;
  private final CollisionBehaviour collision;

  private float yDirection;
  private float xDirection;

  public Boss2Behaviour(Spaceship spaceship) {
    this.spaceship = spaceship;
    this.behaviour = attackBehaviour(spaceship);
    this.collision = new CollisionBehaviour(
        spaceship.attack(Boss2Attack.class).collision(),
        Duration.ofSeconds(1),
        Duration.ZERO
    );

    this.yDirection = inBounds(0.3f, 1f);
    this.xDirection = inBounds(0.3f, 1f);
  }

  private static RotaryBehaviour attackBehaviour(Spaceship spaceship) {
    var up = new Vector3f(-1, 1, 0);
    var down = new Vector3f(-1, -1, 0);

    var def = spaceship.attack(Boss2Attack.class);
    var single = new ShotBehaviour(ShipType.BOSS, spaceship::location, def.singleShot());
    var cross = new ParallelBehaviour(List.of(
        new ShotBehaviour(ShipType.BOSS, down, spaceship::location, def.crossShot()),
        new ShotBehaviour(ShipType.BOSS, spaceship::location, def.crossShot()),
        new ShotBehaviour(ShipType.BOSS, up, spaceship::location, def.crossShot())
    ));
    var wide = new ParallelBehaviour(List.of(
        new ShotBehaviour(ShipType.BOSS, up, offset(spaceship, 2), def.wideShot()),
        new ShotBehaviour(ShipType.BOSS, spaceship::location, def.wideShot()),
        new ShotBehaviour(ShipType.BOSS, down, offsetNegate(spaceship, 2), def.wideShot())
    ));
    return new RotaryBehaviour(
        List.of(
            single,
            cross,
            wide
        ),
        def.attackDuration(),
        def.cooldownDuration()
    );
  }

  @Override
  public void move(float tpf) {
    spaceship.spatial().move(
        spaceship.speed() * tpf * xDirection,
        spaceship.speed() * tpf * yDirection,
        0);
  }

  @Override
  public void onBoundary(Boundary boundary) {
    if (boundary.top()) {
      yDirection = -inBounds(0.3f, 1f);
      xDirection = (float) (signum(xDirection) * sqrt(1 - abs(pow(yDirection, 2))));
    }
    if (boundary.bottom()) {
      yDirection = inBounds(0.3f, 1f);
      xDirection = (float) (signum(xDirection) * sqrt(1 - abs(pow(yDirection, 2))));
    }
    if (boundary.right()) {
      xDirection = -inBounds(0.3f, 1f);
      yDirection = (float) (signum(yDirection) * sqrt(1 - abs(pow(xDirection, 2))));
    }
    if (boundary.left()) {
      xDirection = inBounds(0.3f, 1f);
      yDirection = (float) (signum(yDirection) * sqrt(1 - abs(pow(xDirection, 2))));
    }
  }

  @Override
  public void onCollision(Spatial spatial, float tpf) {
    collision.tryAttack(spatial, spaceship.improvements());
  }

  @Override
  public void attack(float tpf) {
    behaviour.tick(tpf);
    collision.tick(tpf);
    behaviour.tryAttack(spaceship.improvements());
  }

  @Override
  public Spaceship spaceship() {
    return spaceship;
  }

  public record Boss2Attack(
      Shot singleShot,
      Shot wideShot,
      Shot crossShot,
      int collision,
      Duration attackDuration,
      Duration cooldownDuration) implements SpaceshipAttack {

  }
}
