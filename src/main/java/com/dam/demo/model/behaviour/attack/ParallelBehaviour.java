package com.dam.demo.model.behaviour.attack;

import static com.dam.demo.model.behaviour.attack.ShotBehaviour.offset;
import static com.dam.demo.model.behaviour.attack.ShotBehaviour.offsetNegate;

import com.dam.demo.enemies.Tag.ShipType;
import com.dam.demo.model.attack.Shot;
import com.dam.demo.model.shop.ShopUtil;
import com.dam.demo.model.spaceship.Spaceship;
import com.dam.demo.model.upgrade.Upgrade;
import java.util.List;
import java.util.stream.Stream;

public record ParallelBehaviour(List<AttackBehaviour> attackBehaviours) implements AttackBehaviour {

  @Override
  public boolean tryAttack(List<Upgrade> improvements) {
    var result = false;
    for (var behaviour : attackBehaviours) {
      result = result || behaviour.tryAttack(improvements);
    }
    return result;
  }

  @Override
  public void tick(float tpf) {
    for (var behaviour : attackBehaviours) {
      behaviour.tick(tpf);
    }
  }

  public static ParallelBehaviour multipleCannons(Spaceship spaceship, Shot shot, int cannons) {
    var shipType = getShipType(spaceship);

    var mainCannon = cannons % 2 == 0
        ? Stream.<AttackBehaviour>empty()
        : Stream.of(new ShotBehaviour(shipType, spaceship::location, shot));
    var sideCannons = Stream.<ShotBehaviour>builder();
    for (int i = 2; i <= cannons; i += 2) {
      sideCannons.add(new ShotBehaviour(shipType, offset(spaceship, i), shot));
      sideCannons.add(new ShotBehaviour(shipType, offsetNegate(spaceship, i), shot));
    }
    var allCannons = Stream.concat(mainCannon, sideCannons.build()).toList();

    return new ParallelBehaviour(allCannons);
  }

  public static ParallelBehaviour levelCannons(Spaceship spaceship, String name, Shot shot) {
    var maxCannons = ShopUtil.getMaxLevel(name);
    var shipType = getShipType(spaceship);

    var mainCannon = Stream.of(new PredicateBehaviour(
            () -> ShopUtil.getLevel(name, spaceship) % 2 == 1,
            new ShotBehaviour(shipType, spaceship::location, shot)
        )
    );
    var sideCannons = Stream.<AttackBehaviour>builder();

    for (int i = 2; i <= maxCannons; i += 2) {
      var level = i;
      sideCannons.add(new PredicateBehaviour(
          () -> ShopUtil.getLevel(name, spaceship) >= level,
          new ShotBehaviour(shipType, offset(spaceship, i), shot)
      ));
      sideCannons.add(new PredicateBehaviour(
          () -> ShopUtil.getLevel(name, spaceship) >= level,
          new ShotBehaviour(shipType, offsetNegate(spaceship, i), shot)));
    }
    var allCannons = Stream.concat(mainCannon, sideCannons.build()).toList();

    return new ParallelBehaviour(allCannons);
  }

  private static ShipType getShipType(Spaceship spaceship) {
    if (spaceship.is(ShipType.PLAYER)) {
      return ShipType.PLAYER;
    }
    return spaceship.is(ShipType.BOSS) ? ShipType.BOSS : ShipType.ENEMY;
  }
}
