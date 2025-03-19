package com.dam.demo.controls.behaviour.attack;

import com.dam.demo.controls.behaviour.ControlsUtil;
import com.dam.demo.model.Spaceship;
import com.dam.demo.model.attack.Attack;
import com.dam.demo.model.attack.Attack.Random;
import com.dam.util.RandomUtil;

public class RandomBehaviour implements AttackBehaviour {

  private final Random random;

  private final AttackBehaviour behaviour;

  public RandomBehaviour(Spaceship spaceship, Random random) {
    this.random = random;
    this.behaviour = ControlsUtil.attack(random.attack(), spaceship);
  }


  @Override
  public void onTick(float tpf) {
    if (RandomUtil.RANDOM.nextInt(random.random()) != 0) {
      return;
    }
    behaviour.onTick(tpf);
  }

  @Override
  public Attack getAttack() {
    return random;
  }

  public AttackBehaviour getBehaviour() {
    return behaviour;
  }
}
