package com.dam.demo.model.behaviour.spaceship;

import static com.dam.demo.listeners.KeyboardListener.Input.DOWN;
import static com.dam.demo.listeners.KeyboardListener.Input.SHOOT;
import static com.dam.demo.listeners.KeyboardListener.Input.UP;

import com.dam.demo.listeners.KeyboardListener.Input;
import com.dam.demo.model.Boundary;
import com.dam.demo.model.attack.Shot;
import com.dam.demo.model.attack.SpaceshipAttack;
import com.dam.demo.model.behaviour.attack.AttackBehaviour;
import com.dam.demo.model.behaviour.attack.ParallelBehaviour;
import com.dam.demo.model.shop.ShopUtil;
import com.dam.demo.model.spaceship.Spaceship;
import com.jme3.scene.Spatial;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class PlayerBehaviour implements SpaceshipBehaviour {

  private final Spaceship spaceship;
  private final AttackBehaviour behaviour;
  private final Map<Input, Boolean> inputs = new EnumMap<>(Map.of(
      UP, false,
      DOWN, false,
      SHOOT, false
  ));

  private boolean topReached;
  private boolean bottomReached;

  public PlayerBehaviour(Spaceship spaceship) {
    this.spaceship = spaceship;
    this.behaviour = attackBehaviour(spaceship);
    this.topReached = false;
    this.bottomReached = false;
  }

  private AttackBehaviour attackBehaviour(Spaceship spaceship) {
    var attack = spaceship.attack(PlayerAttack.class);
    var bullets = ParallelBehaviour.levelCannons(spaceship, ShopUtil.CANNONS, attack.bullet());
    var rockets = ParallelBehaviour.levelCannons(spaceship, ShopUtil.ROCKETS, attack.rocket());

    return new ParallelBehaviour(List.of(bullets, rockets));
  }

  @Override
  public void move(float tpf) {
    if (inputs.get(UP) == inputs.get(DOWN)) {
      // Either both pressed, or none. Don't move either way.
      return;
    }

    tryMove(inputs.get(UP) ? tpf : -tpf);
  }

  private void tryMove(float tpf) {
    if (tpf > 0 ? topReached : bottomReached) {
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
  public void onCollision(Spatial spatial, float tpf) {
    // No collision amount
  }

  @Override
  public void attack(float tpf) {
    behaviour.tick(tpf);
    if (inputs.get(SHOOT)) {
      behaviour.tryAttack(spaceship.improvements());
    }
  }

  @Override
  public Spaceship spaceship() {
    return spaceship;
  }

  public void onInput(Input input, boolean isPressed) {
    inputs.put(input, isPressed);
  }

  public record PlayerAttack(
      Shot bullet,
      Shot rocket
  ) implements SpaceshipAttack {

  }
}
