package com.dam.demo.controls.behaviour.movement;

import com.dam.demo.model.Boundary;
import com.dam.demo.model.Spaceship;
import com.dam.util.RandomUtil;
import com.jme3.math.FastMath;
import com.jme3.scene.Spatial;

public class BomberMovement extends MovementBase {

  private float widthDirection;
  private float heightDirection;
  private float rotation;

  public BomberMovement(Spaceship spaceship) {
    super(spaceship);
    widthDirection = randomDirection();
    heightDirection = randomDirection();
    rotation = 4f + RandomUtil.RANDOM.nextFloat();
  }

  @Override
  public void move(float tpf) {
    spaceship.spatial().rotate(0, 0, FastMath.INV_PI * rotation * tpf);
    spaceship.spatial().move(
        tpf * spaceship.speed() * widthDirection,
        tpf * spaceship.speed() * heightDirection,
        0);
  }

  @Override
  public void onBoundary(Boundary boundary) {
    var rand = 4f + RandomUtil.RANDOM.nextFloat();
    if (boundary.top()) {
      rotation = rand;
      heightDirection = -randomDirection();
    }
    if (boundary.bottom()) {
      rotation = rand;
      heightDirection = randomDirection();
    }
    if (boundary.left()) {
      rotation = rand;
      widthDirection = randomDirection();
    }
    if (boundary.right()) {
      rotation = rand;
      widthDirection = -randomDirection();
    }
  }

  @Override
  public void onCollision(Spatial spaceship) {
    widthDirection = -widthDirection;
    heightDirection = -heightDirection;
  }

  private static float randomDirection() {
    return 0.2f + RandomUtil.RANDOM.nextFloat(0.8f);
  }
}
