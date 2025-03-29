package com.dam.demo.model.behaviour.attack;

import com.dam.demo.model.upgrade.Upgrade;
import java.util.List;

public record ParallelBehaviour(List<AttackBehaviour> attackBehaviours) implements AttackBehaviour {

  @Override
  public boolean tryAttack(List<Upgrade> improvements) {
    var result = false;
    for (var behaviour : attackBehaviours) {
      result = result || behaviour.tryAttack(improvements);
    }
    return result;
  }

  @Override
  public void tick(float tpf) {
    for (var behaviour : attackBehaviours) {
      behaviour.tick(tpf);
    }
  }
}
