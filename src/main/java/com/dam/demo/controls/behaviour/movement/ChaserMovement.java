package com.dam.demo.controls.behaviour.movement;

import static com.dam.demo.game.Scene.PLAYER;
import static com.dam.demo.util.MathUtil.angleFromVector;

import com.dam.demo.enemies.Tag.ShipType;
import com.dam.demo.game.ParticleManager;
import com.dam.demo.game.Scene;
import com.dam.demo.model.Boundary;
import com.dam.demo.model.Spaceship;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

public class ChaserMovement extends MovementBase {

  private int speed;
  private float rotation;

  public ChaserMovement(Spaceship spaceship) {
    super(spaceship);
  }

  @Override
  public void move(float tpf) {
    var aim = getAim(PLAYER.location(), spaceship.location());
    float actualRotation = angleFromVector(aim.negate());
    if (actualRotation != rotation) {
      spaceship.spatial().rotate(0, 0, actualRotation - rotation);
      rotation = actualRotation;
    }
    speed += 650 * tpf;
    spaceship.spatial().move(aim.mult(tpf * speed));
  }

  private Vector3f getAim(Vector3f player, Vector3f location) {
    var dif = new Vector3f(location.x - player.x, location.y - player.y, 0).negate();

    return dif.normalizeLocal();
  }

  @Override
  public void onBoundary(Boundary boundary) {
    if (boundary.left()) {
      Scene.kill(spaceship);
    }
  }

  @Override
  public void onCollision(Spatial spatial) {
    if (ShipType.PLAYER.is(spatial)) {
      spaceship.spatial().removeFromParent();
      ParticleManager.explosion(spaceship.location(), 10);
    }
  }
}
