package com.dam.demo.model.behaviour.attack;

import com.dam.demo.model.upgrade.Upgrade;
import com.dam.demo.util.MathUtil;
import java.time.Duration;
import java.util.List;

public class RotaryBehaviour implements AttackBehaviour {

  private final Duration attackDuration;
  private final Duration cooldownDuration;


  private List<ShotBehaviour> attacks;
  private RotaryStatus status;
  private int attackIndex;
  private Duration duration;

  public RotaryBehaviour(
      Duration attackDuration,
      Duration cooldownDuration) {
    this.attacks = List.of();
    this.attackDuration = attackDuration;
    this.cooldownDuration = cooldownDuration;

    this.status = RotaryStatus.ATTACKING;
    this.duration = attackDuration;
    this.attackIndex = 0;
  }

  @Override
  public void tryAttack(List<Upgrade> buffs, float tpf) {
    switch (status) {
      case ATTACKING -> attack(buffs, tpf);
      case COOLDOWN -> cooldown(tpf);
    }
  }

  @Override
  public void tick(float tpf) {
    duration = MathUtil.subtractDuration(duration, tpf);
  }

  private void cooldown(float tpf) {
    if (duration.isZero()) {
      status = RotaryStatus.ATTACKING;
      duration = attackDuration;
    }
    tick(tpf);
  }

  private void attack(List<Upgrade> buffs, float tpf) {
    attacks.get(attackIndex).tryAttack(buffs, tpf);
    tick(tpf);
    if (duration.isZero()) {
      status = RotaryStatus.COOLDOWN;
      duration = cooldownDuration;
      attackIndex = nextIndex(attacks.size(), attackIndex);
    }
  }

  private static int nextIndex(int size, int index) {
    return size - 1 == index ? 0 : index + 1;
  }

  private enum RotaryStatus {
    ATTACKING, COOLDOWN;
  }
}
