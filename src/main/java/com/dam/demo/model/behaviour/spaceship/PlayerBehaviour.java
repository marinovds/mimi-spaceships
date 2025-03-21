package com.dam.demo.model.behaviour.spaceship;

import static com.dam.demo.controls.Input.DOWN;
import static com.dam.demo.controls.Input.SHOOT;
import static com.dam.demo.controls.Input.UP;

import com.dam.demo.listeners.KeyboardListener;
import com.dam.demo.model.Boundary;
import com.dam.demo.model.Spaceship;
import com.dam.demo.model.attack.Shot;
import com.dam.demo.model.attack.SpaceshipAttack;
import com.dam.demo.model.behaviour.attack.ShotBehaviour;
import com.dam.demo.util.JsonUtil;
import com.jme3.scene.Spatial;
import java.util.function.Consumer;

public class PlayerBehaviour extends SpaceshipBehaviourBase {

  private final ShotBehaviour behaviour;

  private boolean topReached;
  private boolean bottomReached;

  public PlayerBehaviour(Spaceship spaceship) {
    super(spaceship);
    var attack = JsonUtil.read(spaceship.attack(), PlayerAttack.class);
    this.behaviour = new ShotBehaviour(spaceship, attack.shot());
    this.topReached = false;
    this.bottomReached = false;
  }

  @Override
  public void move(float tpf) {
    if (KeyboardListener.INPUTS.get(UP)) {
      up(tpf);
    }
    if (KeyboardListener.INPUTS.get(DOWN)) {
      down(tpf);
    }
  }

  private void down(float tpf) {
    if (bottomReached) {
      return;
    }
    spaceship.spatial().move(0, -tpf * spaceship.speed(), 0);
  }

  private void up(float tpf) {
    if (topReached) {
      return;
    }
    spaceship.spatial().move(0, tpf * spaceship.speed(), 0);
  }

  @Override
  public void onBoundary(Boundary boundary) {
    bottomReached = boundary.bottom();
    topReached = boundary.top();
  }

  @Override
  public void onCollision(Spatial spatial) {
    // No collision amount
  }

  @Override
  public void attack(float tpf) {
    Consumer<ShotBehaviour> f = KeyboardListener.INPUTS.get(SHOOT)
        ? x -> x.tryAttack(buffs, tpf)
        : x -> x.tick(tpf);

    f.accept(behaviour);
  }

  public record PlayerAttack(Shot shot) implements SpaceshipAttack{}
}
