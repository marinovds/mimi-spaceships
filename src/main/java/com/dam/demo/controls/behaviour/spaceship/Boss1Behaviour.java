package com.dam.demo.controls.behaviour.spaceship;

import com.dam.demo.controls.behaviour.attack.RotaryBehaviour;
import com.dam.demo.controls.behaviour.attack.ShotBehaviour;
import com.dam.demo.model.Boundary;
import com.dam.demo.model.Spaceship;
import com.dam.demo.model.attack.Shot;
import com.dam.demo.model.attack.SpaceshipAttack;
import com.dam.demo.model.upgrade.UpgradeUtil;
import com.dam.demo.util.JsonUtil;
import com.jme3.scene.Spatial;
import java.time.Duration;
import java.util.List;

public class Boss1Behaviour extends SpaceshipBehaviourBase {

  private final Boss1Attack attack;
  private final RotaryBehaviour behaviour;

  private int direction;

  public Boss1Behaviour(Spaceship spaceship) {
    super(spaceship);
    this.attack = JsonUtil.read(spaceship.attack(), Boss1Attack.class);
    this.behaviour = new RotaryBehaviour(attack.attackDuration(), attack.cooldownDuration());

    this.direction = 1;
  }

  @Override
  public void move(float tpf) {
    spaceship.spatial().move(
        0,
        spaceship.speed() * tpf * direction,
        0);
  }

  @Override
  public void onBoundary(Boundary boundary) {
    if (boundary.top()) {
      direction = -1;
    }
    if (boundary.bottom()) {
      direction = 1;
    }
  }

  @Override
  public void onCollision(Spatial spatial) {
    // Boss1 doesn't collide
  }

  @Override
  public void attack(float tpf) {
    var attacks = attack.shots()
        .stream()
        .map(x -> UpgradeUtil.upgradeShot(x, buffs))
        .map(x -> new ShotBehaviour(spaceship, x))
        .toList();
    behaviour.setAttacks(attacks).tryAttack(tpf);
  }

  public record Boss1Attack(
      List<Shot> shots,
      Duration attackDuration,
      Duration cooldownDuration) implements
      SpaceshipAttack {

  }
}
