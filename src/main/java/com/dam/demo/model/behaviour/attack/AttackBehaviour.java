package com.dam.demo.model.behaviour.attack;

import com.dam.demo.model.upgrade.Upgrade;
import java.util.List;

/**
 * Interface that represents the behaviour of the attack. Common usage consists of:
 * <pre>
 * {@code
 *   @Override
 *   public void attack(float tpf) {
 *     behaviour.tick(tpf);
 *     behaviour.attack(spaceship.improvements());
 *   }
 * }
 * </pre>
 */
public interface AttackBehaviour {

  /**
   * Attacks with the underlying implementation of the attack behaviour. Doesn't do anything in case
   * the attack is in cooldown.
   *
   * @param improvements the currently active improvements
   * @return true if attacked, false if in cooldown
   */
  boolean tryAttack(List<Upgrade> improvements);

  /**
   * Tick the duration of the attack behaviour.
   *
   * @param tpf the time per frame in seconds
   */
  void tick(float tpf);
}