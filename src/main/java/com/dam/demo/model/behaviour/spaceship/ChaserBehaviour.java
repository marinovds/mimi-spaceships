package com.dam.demo.model.behaviour.spaceship;

import static com.dam.demo.game.Scene.PLAYER;
import static com.dam.demo.util.MathUtil.angleFromVector;

import com.dam.demo.enemies.Tag.ShipType;
import com.dam.demo.game.ParticleManager;
import com.dam.demo.model.Boundary;
import com.dam.demo.model.Spaceship;
import com.dam.demo.model.attack.SpaceshipAttack;
import com.dam.demo.model.behaviour.attack.CollisionBehaviour;
import com.dam.demo.util.JsonUtil;
import com.dam.demo.util.LangUtil;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import java.time.Duration;
import java.util.logging.Logger;

public class ChaserBehaviour extends SpaceshipBehaviourBase {

  private static final Logger log = Logger.getLogger("test");

  private final int baseSpeed;
  private final float speedMult;
  private final CollisionBehaviour collision;

  private int speed;
  private float rotation;

  public ChaserBehaviour(Spaceship spaceship) {
    super(spaceship);
    var attack = JsonUtil.read(spaceship.attack(), ChaserAttack.class);
    this.baseSpeed = spaceship.speed();
    this.speedMult = attack.speedMultiplier();
    this.collision = new CollisionBehaviour(
        attack.collision(),
        Duration.ZERO,
        Duration.ofMillis(100)
    );
  }

  @Override
  public void move(float tpf) {
    var aim = getAim(PLAYER.location(), spaceship.location());
    float actualRotation = angleFromVector(aim.negate());
    if (actualRotation != rotation) {
      spaceship.spatial().rotate(0, 0, actualRotation - rotation);
      rotation = actualRotation;
    }
    speed = calculateNewSpeed(1);
    spaceship.spatial().move(aim.mult(tpf * speed));
  }

  private Vector3f getAim(Vector3f player, Vector3f location) {
    var dif = new Vector3f(location.x - player.x, location.y - player.y, 0).negate();

    return dif.normalizeLocal();
  }

  @Override
  public void onBoundary(Boundary boundary) {
    if (boundary.left()) {
      spaceship.spatial().removeFromParent();
    }
  }

  @Override
  public void onCollision(Spatial spatial, float tpf) {
    if (ShipType.PLAYER.is(spatial)) {
      collision.tryAttack(spatial, buffs, tpf);
      spaceship.spatial().removeFromParent();
      ParticleManager.explosion(spaceship.location(), 10);
      return;
    }
    if (collision.ignoreFriendlyCollision()) {
      return;
    }
    // Collided with an ally
    speed = calculateNewSpeed(-2);
  }

  private int calculateNewSpeed(int mult) {
    var result = (int) (speed * speedMult * mult);
    return LangUtil.clamp(result, baseSpeed / 2, 2 * baseSpeed);
  }

  @Override
  public void attack(float tpf) {
    collision.tick(tpf);
  }

  public record ChaserAttack(int collision, float speedMultiplier) implements SpaceshipAttack {

  }
}
