package com.dam.demo.controls.behaviour.attack;

import static com.dam.demo.controls.behaviour.ControlsUtil.attacks;

import com.dam.demo.model.Spaceship;
import com.dam.demo.model.attack.Attack;
import com.dam.demo.model.attack.Attack.Parallel;
import java.util.List;

public class ParallelBehaviour implements AttackBehaviour {

  private final Parallel attack;
  private final List<AttackBehaviour> behaviours;

  public ParallelBehaviour(Spaceship spaceship, Parallel parallel) {
    this.behaviours = attacks(parallel.attacks(), spaceship);
    this.attack = parallel;
  }

  @Override
  public void onTick(float tpf) {
    for (var attack : behaviours) {
      attack.onTick(tpf);
    }
  }

  @Override
  public Attack getAttack() {
    return attack;
  }

  public List<AttackBehaviour> attackBehaviours() {
    return behaviours;
  }
}
