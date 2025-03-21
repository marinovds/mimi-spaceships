package com.dam.demo.model.behaviour.spaceship;

import com.dam.demo.model.Spaceship;
import com.dam.demo.model.attack.Damage;
import com.dam.demo.model.upgrade.Upgrade;
import com.dam.demo.model.upgrade.UpgradeUtil;
import java.util.List;

public abstract class SpaceshipBehaviourBase implements SpaceshipBehaviour {

  protected final Spaceship spaceship;
  protected List<Upgrade> buffs;

  protected SpaceshipBehaviourBase(Spaceship spaceship) {
    this.spaceship = spaceship;
    this.buffs = List.of();
  }

  @Override
  public void currentlyActiveBuffs(List<Upgrade> buffs) {
    this.buffs = buffs;
  }

  protected Damage buffDamage(Damage damage) {
    return UpgradeUtil.upgradeDamage(damage, buffs);
  }

}
