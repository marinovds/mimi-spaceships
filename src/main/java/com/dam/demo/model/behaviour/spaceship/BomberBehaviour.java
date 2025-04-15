package com.dam.demo.model.behaviour.spaceship;

import static com.dam.util.RandomUtil.RANDOM;
import static java.lang.Math.signum;

import com.dam.demo.enemies.Tag.ShipType;
import com.dam.demo.model.Boundary;
import com.dam.demo.model.attack.SpaceshipAttack;
import com.dam.demo.model.behaviour.attack.CollisionBehaviour;
import com.dam.demo.model.spaceship.Spaceship;
import com.jme3.math.FastMath;
import com.jme3.scene.Spatial;
import java.time.Duration;

public class BomberBehaviour implements SpaceshipBehaviour {

  private final Spaceship spaceship;
  private final CollisionBehaviour collision;

  private float widthDirection;
  private float heightDirection;
  private float rotation;

  public BomberBehaviour(Spaceship spaceship) {
    this.spaceship = spaceship;
    var attack = spaceship.attack(BomberAttack.class);
    this.collision = new CollisionBehaviour(
        attack.collision(),
        Duration.ofMillis(500),
        Duration.ofSeconds(1)
    );

    widthDirection = randomDirection();
    heightDirection = randomDirection();
    rotation = 4f + RANDOM.nextFloat();
  }

  @Override
  public void move(float tpf) {
    collision.tick(tpf);
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
  public void onCollision(Spatial spatial, float tpf) {
    if (ShipType.PLAYER.is(spatial)
        && collision.tryAttack(spatial, spaceship.improvements())) {
        revertDirection();
        return;
    }

    if (collision.friendlyCollided()) {
      revertDirection();
    }
  }

  private void revertDirection() {
    widthDirection = -signum(widthDirection) * randomDirection();
    heightDirection = -signum(heightDirection) * randomDirection();
    rotation = 4f + RANDOM.nextFloat();
  }

  @Override
  public void attack(float tpf) {
    // Bombers do not attack
  }

  @Override
  public Spaceship spaceship() {
    return spaceship;
  }

  private static float randomDirection() {
    return 0.4f + RANDOM.nextFloat(0.6f);
  }

  public record BomberAttack(int collision) implements SpaceshipAttack {

  }
}
