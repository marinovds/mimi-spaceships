package com.dam.demo.model.behaviour.attack;

import com.dam.demo.model.upgrade.Upgrade;
import java.util.List;

public interface AttackBehaviour {

  /**
   * Attacks with the underlying implementation of the attack behaviour. Doesn't do anything in case
   * the attack is in cooldown.
   *
   * @param buffs the currently active buffs
   * @param tpf the time per frame in seconds
   * @return true if attacked, false if in cooldown
   */
  boolean tryAttack(List<Upgrade> buffs, float tpf);

  /**
   * Tick the duration of the attack behaviour despite not attacking.
   *
   * @param tpf the time per frame in seconds
   */
  void tick(float tpf);
}