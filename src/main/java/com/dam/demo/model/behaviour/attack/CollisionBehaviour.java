package com.dam.demo.model.behaviour.attack;

import com.dam.demo.model.Ticker;
import com.dam.demo.model.attack.Damage;
import com.dam.demo.model.upgrade.Upgrade;
import com.dam.demo.model.upgrade.UpgradeUtil;
import com.dam.demo.util.DamageUtil;
import com.jme3.scene.Spatial;
import java.time.Duration;
import java.util.List;

public class CollisionBehaviour {

  private final Damage damage;

  private final Ticker attackCooldown;
  private final Ticker friendlyCooldown;

  public CollisionBehaviour(int damage, Duration attackCooldown, Duration friendlyCollision) {
    this.damage = Damage.collision(damage);
    this.attackCooldown = Ticker.of(attackCooldown, Duration.ZERO);
    this.friendlyCooldown = Ticker.of(friendlyCollision);
  }

  public boolean tryAttack(Spatial target, List<Upgrade> buffs) {
    if (!attackCooldown.isDone()) {
      return false;
    }

    attackCooldown.reset();
    return DamageUtil.hit(target, UpgradeUtil.upgradeDamage(damage, buffs));
  }

  public void tick(float tpf) {
    attackCooldown.tick(tpf);
    friendlyCooldown.tick(tpf);
  }

  public boolean friendlyCollided() {
    if (!friendlyCooldown.isDone()) {
      return false;
    }

    friendlyCooldown.reset();
    return true;
  }
}
