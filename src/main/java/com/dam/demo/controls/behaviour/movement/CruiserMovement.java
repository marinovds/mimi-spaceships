package com.dam.demo.controls.behaviour.movement;

import com.dam.demo.model.Boundary;
import com.dam.demo.model.Spaceship;

public class CruiserMovement extends MovementBase {

  private int direction;

  public CruiserMovement(Spaceship spaceship) {
    super(spaceship);
    direction = 1;
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
}
