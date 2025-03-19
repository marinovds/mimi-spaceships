package com.dam.demo.controls.behaviour.movement;

import com.dam.demo.model.Boundary;
import com.jme3.scene.Spatial;

public interface MovementBehaviour {

  default void onTick(float tpf) {
    if(!enabled()) {
      return;
    }
    move(tpf);
  }

  void move(float tpf);

  void onBoundary(Boundary boundary);

  void onCollision(Spatial spaceship);

  boolean enabled();

  boolean enable(boolean state);

}
