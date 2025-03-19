package com.dam.demo.controls.behaviour.attack;

import com.dam.demo.enemies.Tag.ShipType;
import com.dam.demo.game.Scene;
import com.dam.demo.model.Spaceship;
import com.dam.demo.model.attack.Attack.Collision;
import com.dam.demo.util.DamageUtil;
import com.dam.demo.util.MathUtil;
import com.jme3.scene.Spatial;
import java.util.stream.Stream;

public class CollisionBehaviour implements AttackBehaviour {

  private final Spaceship spaceship;

  private Collision attack;

  public CollisionBehaviour(Spaceship spaceship, Collision attack) {
    this.spaceship = spaceship;
    this.attack = attack;
  }


  @Override
  public void onTick(float tpf) {
    var collided = targets()
        .filter(x -> MathUtil.collided(spaceship.spatial(), x))
        .findFirst();
    if (collided.isPresent()) {
      var target = collided.get();
      if (DamageUtil.hit(target, attack.damage())) {
        spaceship.control().movement().onCollision(target);
      }
    }
  }

  @Override
  public Collision getAttack() {
    return attack;
  }

  public CollisionBehaviour updateAttack(Collision collision) {
      this.attack = collision;

      return this;
  }

  private Stream<Spatial> targets() {
    return spaceship.tags().contains(ShipType.PLAYER)
        ? Scene.ENEMIES.getChildren().stream()
        : Stream.of(Scene.PLAYER.spatial());
  }
}
