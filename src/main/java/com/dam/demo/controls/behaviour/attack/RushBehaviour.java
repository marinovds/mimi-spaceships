package com.dam.demo.controls.behaviour.attack;

import static com.dam.demo.enemies.Tag.ShipType.ENEMY;
import static com.dam.demo.util.AssetUtil.checkBoundaries;
import static com.dam.demo.util.MathUtil.collided;

import com.dam.demo.controls.behaviour.ControlsUtil;
import com.dam.demo.enemies.Tag.ShipType;
import com.dam.demo.game.Scene;
import com.dam.demo.model.Boundary;
import com.dam.demo.model.Spaceship;
import com.dam.demo.model.attack.Attack.Rush;
import com.dam.demo.util.DamageUtil;
import com.jme3.math.Vector3f;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class RushBehaviour implements AttackBehaviour {

  private final Spaceship spaceship;

  private Rush attack;
  private Vector3f direction;
  private Predicate<Vector3f> rushed;

  public RushBehaviour(Spaceship spaceship, Rush rush) {
    this.attack = rush;
    this.spaceship = spaceship;
    direction = ControlsUtil.getAim(spaceship);

  }

  public void activate() {
    spaceship.control().movement().enable(false);
    var startingPoint = spaceship.location().x;
    rushed = spaceship.is(ShipType.ENEMY)
        ? x -> x.x >= startingPoint
        : x -> x.x <= startingPoint;
  }

  @Override
  public void onTick(float tpf) {
    var movement = direction.mult(tpf * attack.speed());
    spaceship.spatial().move(movement);
    boundaryReached(checkBoundaries(spaceship));
    var targets = spaceship.is(ENEMY)
        ? Stream.of(Scene.PLAYER.spatial())
        : Scene.ENEMIES.getChildren().stream();
    var collided = targets.filter(x -> collided(spaceship.spatial(), x))
        .findFirst();
    if (collided.isPresent()) {
      var hit = collided.get();
      DamageUtil.hit(hit, attack.damage());
    }

    if (rushed.test(spaceship.location())) {
      // we are back to where we started
    }
  }

  @Override
  public Rush getAttack() {
    return attack;
  }

  public RushBehaviour updateAttack(Rush attack) {
    this.attack = attack;

    return this;
  }

  private void boundaryReached(Boundary boundary) {
    if (boundary.bottom() || boundary.top() || boundary.left() || boundary.right()) {
      direction = direction.negate();
    }
  }
}
