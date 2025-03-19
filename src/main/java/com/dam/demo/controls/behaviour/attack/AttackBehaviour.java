package com.dam.demo.controls.behaviour.attack;

import com.dam.demo.model.attack.Attack;

public interface AttackBehaviour {

    void onTick(float tpf);

    Attack getAttack();

}