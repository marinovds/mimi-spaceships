package com.dam.demo.model.behaviour.spaceship;

import com.dam.demo.enemies.Tag.ShipType;
import com.dam.demo.model.Boundary;
import com.dam.demo.model.attack.Shot;
import com.dam.demo.model.attack.SpaceshipAttack;
import com.dam.demo.model.behaviour.attack.ParallelBehaviour;
import com.dam.demo.model.behaviour.attack.RotaryBehaviour;
import com.dam.demo.model.behaviour.attack.ShotBehaviour;
import com.dam.demo.model.spaceship.Spaceship;
import com.jme3.scene.Spatial;
import java.time.Duration;
import java.util.List;

public class Boss1Behaviour implements SpaceshipBehaviour {

  private final Spaceship spaceship;
  private final RotaryBehaviour behaviour;

  private int direction;

  public Boss1Behaviour(Spaceship spaceship) {
    this.spaceship = spaceship;
    this.behaviour = attackBehaviour(spaceship);

    this.direction = 1;
  }

  private static RotaryBehaviour attackBehaviour(Spaceship spaceship) {
    var def = spaceship.attack(Boss1Attack.class);
    var fastShot = new ShotBehaviour(ShipType.BOSS, spaceship::location, def.fastShot());
    var dualRockets = ParallelBehaviour.multipleCannons(spaceship, def.dualRockets(), 2);
    var dualShot = ParallelBehaviour.multipleCannons(spaceship, def.dualShot(), 2);

    return new RotaryBehaviour(
        List.of(
            fastShot,
            dualRockets,
            dualShot
        ),
        def.attackDuration(),
        def.cooldownDuration()
    );
  }

  @Override
  public void move(float tpf) {
    spaceship.spatial().move(
        0,
        spaceship.speed() * tpf * direction,
        0);
  }

  @Override
  public void onBoundary(Boundary boundary) {
    if (boundary.top()) {
      direction = -1;
    }
    if (boundary.bottom()) {
      direction = 1;
    }
  }

  @Override
  public void onCollision(Spatial spatial, float tpf) {
    // Boss1 doesn't collide
  }

  @Override
  public void attack(float tpf) {
    behaviour.tick(tpf);
    behaviour.tryAttack(spaceship.improvements());
  }

  @Override
  public Spaceship spaceship() {
    return spaceship;
  }

  public record Boss1Attack(
      Shot fastShot,
      Shot dualRockets,
      Shot dualShot,
      Duration attackDuration,
      Duration cooldownDuration) implements SpaceshipAttack {

  }
}
