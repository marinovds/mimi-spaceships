package com.dam.demo.controls.behaviour;

import static com.dam.demo.util.MathUtil.getAimDirection;

import com.dam.demo.controls.SpaceshipControl;
import com.dam.demo.controls.behaviour.attack.AttackBehaviour;
import com.dam.demo.controls.behaviour.attack.CollisionBehaviour;
import com.dam.demo.controls.behaviour.attack.ParallelBehaviour;
import com.dam.demo.controls.behaviour.attack.RandomBehaviour;
import com.dam.demo.controls.behaviour.attack.RotaryBehaviour;
import com.dam.demo.controls.behaviour.attack.RushBehaviour;
import com.dam.demo.controls.behaviour.attack.ShotBehaviour;
import com.dam.demo.controls.behaviour.movement.MovementBehaviour;
import com.dam.demo.enemies.Tag.ShipType;
import com.dam.demo.model.Spaceship;
import com.dam.demo.model.attack.Attack;
import com.dam.demo.model.attack.Attack.Collision;
import com.dam.demo.model.attack.Attack.Parallel;
import com.dam.demo.model.attack.Attack.Random;
import com.dam.demo.model.attack.Attack.Rotary;
import com.dam.demo.model.attack.Attack.Rush;
import com.dam.demo.model.attack.Attack.Shot;
import com.jme3.math.Vector3f;
import java.util.List;

public enum ControlsUtil {
  ;

  public static SpaceshipControl spaceshipControl(
      Spaceship spaceship,
      MovementBehaviour movement) {

    return new SpaceshipControl(
        attack(spaceship.attack(), spaceship),
        movement);
  }

  public static AttackBehaviour attack(
      Attack attack,
      Spaceship spaceship) {
    return switch (attack) {
      case Shot shot -> new ShotBehaviour(spaceship, shot);
      case Collision collision -> new CollisionBehaviour(spaceship, collision);
      case Parallel parallel -> new ParallelBehaviour(spaceship, parallel);
      case Rotary rotary -> new RotaryBehaviour(spaceship, rotary);
      case Rush rush -> new RushBehaviour(spaceship, rush);
      case Random random -> new RandomBehaviour(spaceship, random);
    };
  }

  public static List<AttackBehaviour> attacks(List<Attack> attacks, Spaceship spaceship) {
    return attacks.stream()
        .map(x -> attack(x, spaceship))
        .toList();
  }

  /**
   * For internal implementation only!
   */
  public static Vector3f getAim(Spaceship spaceship) {
    if (spaceship.is(ShipType.ENEMY) || spaceship.is(ShipType.BOSS)) {
      return getAimDirection(spaceship.location()).negate();
    }
    return getAimDirection(spaceship.location());
  }
}
