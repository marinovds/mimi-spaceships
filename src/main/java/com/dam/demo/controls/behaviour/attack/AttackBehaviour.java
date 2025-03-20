package com.dam.demo.controls.behaviour.attack;

public interface AttackBehaviour {

  /**
   * Attacks with the underlying implementation of the attack behaviour. Doesn't do anything in case
   * the attack is in cooldown.
   *
   * @param tpf the time per frame in seconds
   */
  void tryAttack(float tpf);

  /**
   * Tick the duration of the attack behaviour despite not attacking.
   *
   * @param tpf the time per frame in seconds
   */
  void tick(float tpf);
}