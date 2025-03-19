package com.dam.demo.controls.behaviour.attack;

import static com.dam.demo.controls.behaviour.ControlsUtil.attacks;
import static com.dam.demo.util.MathUtil.inCooldown;

import com.dam.demo.model.Spaceship;
import com.dam.demo.model.attack.Attack;
import com.dam.demo.model.attack.Attack.Rotary;
import java.time.Instant;
import java.util.List;

public class RotaryBehaviour implements AttackBehaviour {

  private final Rotary attack;

  private final List<AttackBehaviour> behaviours;
  private RotaryStatus status;
  private int attackIndex;
  private Instant timestamp;

  public RotaryBehaviour(Spaceship spaceship, Rotary rotary) {
    this.attack = rotary;
    this.behaviours = attacks(rotary.attacks(), spaceship);

    this.status = RotaryStatus.ATTACKING;
    this.timestamp = Instant.now();
    this.attackIndex = 0;
  }

  @Override
  public void onTick(float tpf) {
    switch (status) {
      case ATTACKING -> attack(tpf);
      case COOLDOWN -> cooldown();
    }
  }

  @Override
  public Attack getAttack() {
    return attack;
  }

  public List<AttackBehaviour> getBehaviours() {
    return behaviours;
  }

  private void cooldown() {
    if (!inCooldown(timestamp, attack.cooldown())) {
      status = RotaryStatus.ATTACKING;
      timestamp = Instant.now();
    }
  }

  private void attack(float tpf) {
    behaviours.get(attackIndex).onTick(tpf);
    if (!inCooldown(timestamp, attack.attackDuration())) {
      status = RotaryStatus.COOLDOWN;
      timestamp = Instant.now();
      attackIndex = nextIndex(behaviours.size(), attackIndex);
    }
  }

  private static int nextIndex(int size, int index) {
    return size - 1 == index ? 0 : index + 1;
  }

  private enum RotaryStatus {
    ATTACKING, COOLDOWN;
  }
}
