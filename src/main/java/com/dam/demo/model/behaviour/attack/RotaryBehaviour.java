package com.dam.demo.model.behaviour.attack;

import com.dam.demo.model.Ticker;
import com.dam.demo.model.upgrade.Upgrade;
import java.time.Duration;
import java.util.List;

public class RotaryBehaviour implements AttackBehaviour {

  private final Ticker ticker;
  private final Duration attackDuration;
  private final Duration cooldownDuration;
  private final List<AttackBehaviour> attacks;

  private RotaryStatus status;
  private int attackIndex;

  public RotaryBehaviour(
      List<AttackBehaviour> attacks,
      Duration attackDuration,
      Duration cooldownDuration) {
    this.attacks = attacks;
    this.ticker = Ticker.of(attackDuration);
    this.attackDuration = attackDuration;
    this.cooldownDuration = cooldownDuration;

    this.status = RotaryStatus.COOLDOWN;
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
    ticker.tick(tpf);
  }

  private boolean cooldown() {
    if (ticker.isDone()) {
      status = RotaryStatus.ATTACKING;
      ticker.reset(attackDuration);
    }
    return false;
  }

  private boolean attack(List<Upgrade> buffs) {
    var result = attacks.get(attackIndex).tryAttack(buffs);
    if (ticker.isDone()) {
      status = RotaryStatus.COOLDOWN;
      ticker.reset(cooldownDuration);
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
