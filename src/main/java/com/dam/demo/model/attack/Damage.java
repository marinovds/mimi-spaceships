package com.dam.demo.model.attack;

public record Damage(int amount, DamageType type) {

  public static Damage bullet(int amount) {
    return new Damage(amount, DamageType.BULLET);
  }

  public static Damage rocket(int amount) {
    return new Damage(amount, DamageType.ROCKET);
  }

  public static Damage collision(int amount) {
    return new Damage(amount, DamageType.COLLISION);
  }

  public enum DamageType {

      BULLET, ROCKET, COLLISION
  }
}
