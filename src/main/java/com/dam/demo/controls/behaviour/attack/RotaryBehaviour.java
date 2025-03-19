package com.dam.demo.controls.behaviour.attack;

import static com.dam.demo.controls.behaviour.ControlsUtil.attacks;

import com.dam.demo.model.Spaceship;
import com.dam.demo.model.attack.Attack;
import com.dam.demo.model.attack.Attack.Rotary;
import com.dam.demo.util.MathUtil;
import java.time.Duration;
import java.util.List;

public class RotaryBehaviour implements AttackBehaviour {

  private final Rotary attack;

  private final List<AttackBehaviour> behaviours;
  private RotaryStatus status;
  private int attackIndex;
  private Duration duration;

  public RotaryBehaviour(Spaceship spaceship, Rotary rotary) {
    this.attack = rotary;
    this.behaviours = attacks(rotary.attacks(), spaceship);

    this.status = RotaryStatus.ATTACKING;
    this.duration = rotary.attackDuration();
    this.attackIndex = 0;
  }

  @Override
  public void onTick(float tpf) {
    switch (status) {
      case ATTACKING -> attack(tpf);
      case COOLDOWN -> cooldown(tpf);
    }
  }

  @Override
  public Attack getAttack() {
    return attack;
  }

  public List<AttackBehaviour> getBehaviours() {
    return behaviours;
  }

  private void cooldown(float tpf) {
    if (duration.isZero()) {
      status = RotaryStatus.ATTACKING;
      duration = attack.attackDuration();
    }
    duration = MathUtil.subtractDuration(duration, tpf);
  }

  private void attack(float tpf) {
    behaviours.get(attackIndex).onTick(tpf);
    duration = MathUtil.subtractDuration(duration, tpf);
    if (duration.isZero()) {
      status = RotaryStatus.COOLDOWN;
      duration = attack.cooldown();
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
