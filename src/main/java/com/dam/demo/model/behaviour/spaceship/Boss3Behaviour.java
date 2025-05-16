package com.dam.demo.model.behaviour.spaceship;

import static com.dam.util.RandomUtil.inBounds;

import com.dam.demo.model.Boundary;
import com.dam.demo.model.Ticker;
import com.dam.demo.model.attack.Shot;
import com.dam.demo.model.attack.SpaceshipAttack;
import com.dam.demo.model.behaviour.attack.CollisionBehaviour;
import com.dam.demo.model.behaviour.attack.ParallelBehaviour;
import com.dam.demo.model.behaviour.attack.RotaryBehaviour;
import com.dam.demo.model.spaceship.Spaceship;
import com.jme3.scene.Spatial;
import java.time.Duration;
import java.util.List;

public class Boss3Behaviour implements SpaceshipBehaviour {

  private final Spaceship spaceship;
  private final RotaryBehaviour behaviour;
  private final CollisionBehaviour collision;
  private final Ticker rushTicker;

  private boolean rushing;
  private float yDirection;
  private int xDirection;

  public Boss3Behaviour(Spaceship spaceship) {
    this.spaceship = spaceship;
    this.behaviour = attackBehaviour(spaceship);
    var def = spaceship.attack(Boss3Attack.class);
    this.collision = new CollisionBehaviour(
        def.collision(),
        Duration.ofSeconds(1),
        Duration.ZERO
    );
    this.rushTicker = Ticker.of(def.rushCooldown());

    this.yDirection = inBounds(0.6f, 1.2f);
    this.xDirection = -2;
    this.rushing = false;
  }

  private static RotaryBehaviour attackBehaviour(Spaceship spaceship) {
    var def = spaceship.attack(Boss3Attack.class);
    var doubleRocket = ParallelBehaviour.multipleCannons(spaceship, def.doubleRocket(), 2);
    var tripleShot = ParallelBehaviour.multipleCannons(spaceship, def.tripleShot(), 3);
    return new RotaryBehaviour(
        List.of(
            doubleRocket,
            tripleShot
        ),
        def.attackDuration(),
        def.cooldownDuration()
    );
  }

  @Override
  public void move(float tpf) {
    var spatial = spaceship.spatial();
    var unused = rushing
        ? spatial.move(spaceship.speed() * tpf * xDirection, 0, 0)
        : spatial.move(0, spaceship.speed() * tpf * yDirection, 0);
  }

  @Override
  public void onBoundary(Boundary boundary) {
    if (boundary.top()) {
      yDirection = -inBounds(0.6f, 1.2f);
    }
    if (boundary.bottom()) {
      yDirection = inBounds(0.6f, 1.2f);
    }
    if (boundary.left()) {
      xDirection = 2;
    }
    if (boundary.right() && rushing) {
      rushing = false;
      rushTicker.reset();
      xDirection = -2;
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
    if (!rushing) {
      if (rushTicker.isDone()) {
        rushing = true;
        xDirection = -2;
        return;
      }
      behaviour.tryAttack(spaceship.improvements());
      rushTicker.tick(tpf);
    }
  }

  @Override
  public Spaceship spaceship() {
    return spaceship;
  }

  public record Boss3Attack(
      Shot doubleRocket,
      Shot tripleShot,
      int collision,
      Duration attackDuration,
      Duration cooldownDuration,
      Duration rushCooldown) implements SpaceshipAttack {

  }
}
