package com.dam.demo.controls.behaviour.spaceship;

import static com.dam.util.RandomUtil.RANDOM;

import com.dam.demo.enemies.Tag.ShipType;
import com.dam.demo.model.Boundary;
import com.dam.demo.model.Spaceship;
import com.dam.demo.model.attack.Damage;
import com.dam.demo.model.attack.SpaceshipAttack;
import com.dam.demo.util.DamageUtil;
import com.dam.demo.util.JsonUtil;
import com.jme3.math.FastMath;
import com.jme3.scene.Spatial;

public class BomberBehaviour extends SpaceshipBehaviourBase {

  private final Damage damage;

  private float widthDirection;
  private float heightDirection;
  private float rotation;

  public BomberBehaviour(Spaceship spaceship) {
    super(spaceship);
    var attack = JsonUtil.read(spaceship.attack(), BomberAttack.class);
    this.damage = Damage.collision(attack.collision());

    widthDirection = randomDirection();
    heightDirection = randomDirection();
    rotation = 4f + RANDOM.nextFloat();
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
    var rand = 4f + RANDOM.nextFloat();
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
  public void onCollision(Spatial spatial) {
    if (ShipType.PLAYER.is(spatial)) {
      DamageUtil.hit(spatial, buffDamage(damage));
    }
    widthDirection = -widthDirection;
    heightDirection = -heightDirection;
    rotation = 4f + RANDOM.nextFloat();
  }

  @Override
  public void attack(float tpf) {
    // The Bomber collides - no attack
  }

  private static float randomDirection() {
    return 0.4f + RANDOM.nextFloat(0.6f);
  }

  public record BomberAttack(int collision) implements SpaceshipAttack {

  }
}
