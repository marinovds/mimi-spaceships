package com.dam.demo.controls.behaviour.movement;

import com.dam.demo.model.Spaceship;
import com.jme3.scene.Spatial;

public abstract class MovementBase implements MovementBehaviour {

  protected final Spaceship spaceship;
  protected boolean enabled;

  public MovementBase(Spaceship spaceship) {
    this.spaceship = spaceship;
    this.enabled = true;
  }

  @Override
  public boolean enabled() {
    return enabled;
  }

  @Override
  public boolean enable(boolean state) {
    var result = enabled;
    this.enabled = state;

    return result;
  }

  @Override
  public void onCollision(Spatial spaceship) {

  }
}
