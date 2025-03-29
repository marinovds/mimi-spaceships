package com.dam.demo.model.behaviour.attack;

import com.dam.demo.model.upgrade.Upgrade;
import com.dam.demo.util.MathUtil;
import java.time.Duration;
import java.util.List;

public class RotaryBehaviour implements AttackBehaviour {

  private final Duration attackDuration;
  private final Duration cooldownDuration;
  private final List<AttackBehaviour> attacks;

  private RotaryStatus status;
  private int attackIndex;
  private Duration duration;

  public RotaryBehaviour(
      List<AttackBehaviour> attacks,
      Duration attackDuration,
      Duration cooldownDuration) {
    this.attacks = attacks;
    this.attackDuration = attackDuration;
    this.cooldownDuration = cooldownDuration;

    this.status = RotaryStatus.ATTACKING;
    this.duration = attackDuration;
    this.attackIndex = 0;
  }

  @Override
  public boolean tryAttack(List<Upgrade> buffs) {
    return switch (status) {
      case ATTACKING -> attack(buffs);
      case COOLDOWN -> cooldown();
    };
  }

  @Override
  public void tick(float tpf) {
    attacks.get(attackIndex).tick(tpf);
    duration = MathUtil.subtractDuration(duration, tpf);
  }

  private boolean cooldown() {
    if (duration.isZero()) {
      status = RotaryStatus.ATTACKING;
      duration = attackDuration;
    }
    return false;
  }

  private boolean attack(List<Upgrade> buffs) {
    var result = attacks.get(attackIndex).tryAttack(buffs);
    if (duration.isZero()) {
      status = RotaryStatus.COOLDOWN;
      duration = cooldownDuration;
      attackIndex = nextIndex(attacks.size(), attackIndex);
    }
    return result;
  }

  private static int nextIndex(int size, int index) {
    return size - 1 == index ? 0 : index + 1;
  }

  private enum RotaryStatus {
    ATTACKING, COOLDOWN;
  }
}
