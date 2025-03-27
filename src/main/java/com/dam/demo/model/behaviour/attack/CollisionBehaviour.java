package com.dam.demo.model.behaviour.attack;

import com.dam.demo.model.attack.Damage;
import com.dam.demo.model.upgrade.Upgrade;
import com.dam.demo.model.upgrade.UpgradeUtil;
import com.dam.demo.util.DamageUtil;
import com.dam.demo.util.MathUtil;
import com.jme3.scene.Spatial;
import java.time.Duration;
import java.util.List;

public class CollisionBehaviour {

  private final Damage damage;
  private final Duration attackCooldownDuration;
  private final Duration friendlyCollisionDuration;

  private Duration attackCooldown;
  private Duration friendlyCooldown;

  public CollisionBehaviour(int damage, Duration attackCooldown, Duration friendlyCollision) {
    this.damage = Damage.collision(damage);
    this.attackCooldownDuration = attackCooldown;
    this.friendlyCollisionDuration = friendlyCollision;

    this.attackCooldown = Duration.ZERO;
    this.friendlyCooldown = friendlyCollisionDuration;
  }

  public boolean tryAttack(Spatial target, List<Upgrade> buffs, float tpf) {
    if (attackCooldown.isPositive()) {
      return false;
    }

    attackCooldown = attackCooldownDuration;
    return DamageUtil.hit(target, UpgradeUtil.upgradeDamage(damage, buffs));
  }

  public void tick(float tpf) {
    attackCooldown = MathUtil.subtractDuration(attackCooldown, tpf);
    friendlyCooldown = MathUtil.subtractDuration(friendlyCooldown, tpf);
  }

  public boolean friendlyCollided() {
    if (friendlyCooldown.isPositive()) {
      return false;
    }
    this.friendlyCooldown = friendlyCollisionDuration;
    return true;
  }
}
