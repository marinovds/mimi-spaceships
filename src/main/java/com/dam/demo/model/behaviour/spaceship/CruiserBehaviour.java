package com.dam.demo.model.behaviour.spaceship;

import static com.dam.util.RandomUtil.formallyDistributed;

import com.dam.demo.enemies.Tag.ShipType;
import com.dam.demo.model.Boundary;
import com.dam.demo.model.attack.Shot;
import com.dam.demo.model.attack.SpaceshipAttack;
import com.dam.demo.model.behaviour.attack.ShotBehaviour;
import com.dam.demo.model.spaceship.Spaceship;
import com.dam.demo.util.JsonUtil;
import com.dam.util.RandomUtil;
import com.jme3.scene.Spatial;

public class CruiserBehaviour implements SpaceshipBehaviour {

  private final Spaceship spaceship;
  private final CruiserAttack attack;
  private final ShotBehaviour behaviour;

  private int direction;

  public CruiserBehaviour(Spaceship spaceship) {
    this.spaceship = spaceship;
    this.attack = JsonUtil.read(spaceship.attack(), CruiserAttack.class);
    this.behaviour = new ShotBehaviour(ShipType.ENEMY, spaceship::location, attack.shot());
    this.direction = formallyDistributed(1, -1);
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
    // Cruisers don't collide - maybe...
  }

  @Override
  public void attack(float tpf) {
    behaviour.tick(tpf);
    if (RandomUtil.RANDOM.nextInt(attack.random()) == 0) {
      behaviour.tryAttack(spaceship.improvements());
    }
  }

  @Override
  public Spaceship spaceship() {
    return spaceship;
  }

  public record CruiserAttack(Shot shot, int random) implements SpaceshipAttack{}
}