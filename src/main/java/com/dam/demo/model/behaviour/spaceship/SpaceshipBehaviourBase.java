package com.dam.demo.model.behaviour.spaceship;

import com.dam.demo.model.spaceship.Spaceship;
import com.dam.demo.model.upgrade.Upgrade;
import java.util.List;
import java.util.stream.Stream;

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

  public List<Upgrade> improvements() {
    return Stream.concat(spaceship.upgrades().stream(), buffs.stream()).toList();
  }

}
