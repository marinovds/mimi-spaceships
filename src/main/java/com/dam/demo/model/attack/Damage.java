package com.dam.demo.model.attack;

import com.dam.demo.model.DamageType;

public record Damage(int damage, DamageType type) {

  public static Damage bullet(int damage) {
    return new Damage(damage, DamageType.BULLET);
  }

  public static Damage rocket(int damage) {
    return new Damage(damage, DamageType.ROCKET);
  }

  public static Damage collision(int damage) {
    return new Damage(damage, DamageType.COLLISION);
  }
}
