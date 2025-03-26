package com.dam.demo.model.behaviour.spaceship;

import static com.dam.demo.listeners.KeyboardListener.Input.DOWN;
import static com.dam.demo.listeners.KeyboardListener.Input.SHOOT;
import static com.dam.demo.listeners.KeyboardListener.Input.UP;

import com.dam.demo.listeners.KeyboardListener.Input;
import com.dam.demo.model.Boundary;
import com.dam.demo.model.attack.Shot;
import com.dam.demo.model.attack.SpaceshipAttack;
import com.dam.demo.model.behaviour.attack.ShotBehaviour;
import com.dam.demo.model.spaceship.Spaceship;
import com.dam.demo.util.JsonUtil;
import com.jme3.scene.Spatial;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Consumer;

public class PlayerBehaviour extends SpaceshipBehaviourBase {

  private final ShotBehaviour behaviour;
  private final Map<Input, Boolean> inputs = new EnumMap<>(Map.of(
      UP, false,
      DOWN, false,
      SHOOT, false
  ));

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
    Consumer<ShotBehaviour> f = inputs.get(SHOOT)
        ? x -> x.tryAttack(improvements(), tpf)
        : x -> x.tick(tpf);

    f.accept(behaviour);
  }

  public void onInput(Input input, boolean isPressed) {
    inputs.put(input, isPressed);
  }

  public record PlayerAttack(Shot shot) implements SpaceshipAttack {

  }
}
