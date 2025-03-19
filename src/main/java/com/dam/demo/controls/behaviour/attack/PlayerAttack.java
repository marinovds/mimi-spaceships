package com.dam.demo.controls.behaviour.attack;

import com.dam.demo.controls.Input;
import com.dam.demo.listeners.KeyboardListener;
import com.dam.demo.model.Spaceship;
import com.dam.demo.model.attack.Attack.Parallel;

public final class PlayerAttack extends ParallelBehaviour {

  public PlayerAttack(Spaceship spaceship, Parallel attack) {
    super(spaceship, attack);
  }

  @Override
  public void onTick(float tpf) {
    if (KeyboardListener.INPUTS.get(Input.SHOOT)) {
      super.onTick(tpf);
    }
  }
}
