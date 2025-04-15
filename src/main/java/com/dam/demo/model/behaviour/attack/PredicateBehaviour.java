package com.dam.demo.model.behaviour.attack;

import com.dam.demo.model.upgrade.Upgrade;
import java.util.List;
import java.util.function.BooleanSupplier;

public record PredicateBehaviour(
    BooleanSupplier predicate,
    AttackBehaviour behaviour) implements AttackBehaviour {

  @Override
  public boolean tryAttack(List<Upgrade> improvements) {
    return predicate.getAsBoolean() && behaviour.tryAttack(improvements);
  }

  @Override
  public void tick(float tpf) {
    behaviour.tick(tpf);
  }

}
