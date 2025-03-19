package com.dam.demo.controls.behaviour.movement;

import static com.dam.demo.controls.Input.DOWN;
import static com.dam.demo.controls.Input.UP;

import com.dam.demo.listeners.KeyboardListener;
import com.dam.demo.model.Boundary;
import com.dam.demo.model.Spaceship;

public class PlayerMovement extends MovementBase {

  private boolean topReached;
  private boolean bottomReached;

  public PlayerMovement(Spaceship spaceship) {
    super(spaceship);
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
}
