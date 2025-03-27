package com.dam.demo.model.behaviour.spaceship;

import com.dam.demo.model.Boundary;
import com.dam.demo.model.spaceship.Spaceship;
import com.jme3.scene.Spatial;

/**
 * Interface that specifies how a spaceship behaves.
 */
public interface SpaceshipBehaviour {

  /**
   * Move the spaceship.
   *
   * @param tpf       the time per frame in seconds.
   */
  void move(float tpf);

  /**
   * Notifies when a boundary is reached.
   * <b>Implementation notice:</b> This method is called on every frame, with no boundary set in
   * case none is reached.
   *
   * @param boundary the boundaries that are reached.
   */
  void onBoundary(Boundary boundary);

  /**
   * The spaceship collided while moving.
   *
   * @param spatial the object that the spaceship collided with.
   * @param tpf the time per frame in seconds.
   */
  void onCollision(Spatial spatial, float tpf);

  /**
   * The attack of the spaceship.
   *
   * @param tpf the time per frame.
   */
  void attack(float tpf);

  /**
   * The underlying spaceship.
   *
   * @return The underlying spaceship.
   */
  Spaceship spaceship();
}
