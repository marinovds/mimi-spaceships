package com.dam.demo.model.upgrade;

import static com.dam.demo.util.LangUtil.map;
import static com.dam.demo.util.MathUtil.decreaseDuration;

import com.dam.demo.controls.behaviour.attack.AttackBehaviour;
import com.dam.demo.controls.behaviour.attack.CollisionBehaviour;
import com.dam.demo.controls.behaviour.attack.ParallelBehaviour;
import com.dam.demo.controls.behaviour.attack.RandomBehaviour;
import com.dam.demo.controls.behaviour.attack.RotaryBehaviour;
import com.dam.demo.controls.behaviour.attack.RushBehaviour;
import com.dam.demo.controls.behaviour.attack.ShotBehaviour;
import com.dam.demo.game.Level;
import com.dam.demo.model.SpaceshipDefinition;
import com.dam.demo.model.attack.Attack;
import com.dam.demo.model.attack.Attack.Collision;
import com.dam.demo.model.attack.Attack.Parallel;
import com.dam.demo.model.attack.Attack.Random;
import com.dam.demo.model.attack.Attack.Rotary;
import com.dam.demo.model.attack.Attack.Rush;
import com.dam.demo.model.attack.Attack.Shot;
import com.dam.demo.model.attack.Damage;
import com.dam.demo.util.JsonUtil;
import com.dam.demo.util.MathUtil;
import com.dam.util.RandomUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.IntStream;

public enum UpgradeUtil {
  ;

  public static List<Upgrade> parse(String value) {
    return JsonUtil.read(value, new TypeReference<>() {
    });
  }

  public static String toString(List<Upgrade> upgrades) {
    return JsonUtil.write(upgrades);
  }

  public static SpaceshipDefinition upgrade(SpaceshipDefinition definition) {
    var upgrades = IntStream.range(1, Level.level())
        .filter(x -> RandomUtil.RANDOM.nextInt(2) == 0)
        .mapToObj(x -> {
              var vals = UpgradeType.values();
              var choice = RandomUtil.RANDOM.nextInt(vals.length);
              return vals[choice];
            })
        .map(x -> new Upgrade(20, x))
        .toList();

    return definition.upgrades(upgrades);
  }

  public static AttackBehaviour buffAttack(AttackBehaviour attack, Buff buff) {
    var def = attack.getAttack();
    var type = buff.upgrade().type();
    var increase = buff.upgrade().percentage();
    return switch (def) {
      case Shot s -> ((ShotBehaviour) attack).updateAttack((Shot) upgradeAttack(s, type, increase));
      case Collision c ->
          ((CollisionBehaviour) attack).updateAttack((Collision) upgradeAttack(c, type, increase));
      case Rush r -> ((RushBehaviour) attack).updateAttack((Rush) upgradeAttack(r, type, increase));
      case Random r -> buffAttack(((RandomBehaviour) attack).getBehaviour(), buff);
      case Parallel p -> {
        ((ParallelBehaviour) attack).attackBehaviours().forEach(x -> buffAttack(x, buff));
        yield attack;
      }
      case Rotary r -> {
        ((RotaryBehaviour) attack).getBehaviours().forEach(x -> buffAttack(x, buff));
        yield attack;
      }
    };
  }

  private static Attack upgradeAttack(Attack attack, Map<UpgradeType, Integer> increases) {
    var result = attack;
    for (var increase : increases.entrySet()) {
      result = upgradeAttack(result, increase.getKey(), increase.getValue());
    }
    return result;
  }

  public static Attack upgradeAttack(Attack attack, UpgradeType type, int percentage) {
    return switch (type) {
      case ATTACK_DAMAGE -> upgradeAttackDamage(attack, percentage);
      case ATTACK_SPEED -> upgradeAttackSpeed(attack, percentage);
      case SHOT_SPEED -> upgradeShotSpeed(attack, percentage);
      case MOVEMENT_SPEED -> upgradeAttackMovement(attack, percentage);
      case HEALTH -> attack;
    };
  }

  // @formatter:off
  private static Attack upgradeAttackMovement(Attack attack, int percentage) {
    return switch (attack) {
      case Shot x -> attack;
      case Collision x -> attack;
      case Rush r -> new Rush(r.damage(), MathUtil.apply(r.speed(), percentage));
      case Rotary r -> new Rotary(map(r.attacks(), x -> upgradeAttackMovement(x, percentage)), r.attackDuration(), r.attackDuration());
      case Parallel p -> new Parallel(map(p.attacks(), x -> upgradeAttackMovement(x, percentage)));
      case Random r -> new Random(upgradeAttackMovement(r.attack(), percentage), r.random());
    };
  }

  private static Attack upgradeShotSpeed(Attack attack, int percentage) {
    return switch (attack) {
      case Shot s -> new Shot(s.damage(), MathUtil.apply(s.speed(), percentage), s.cooldown());
      case Collision x -> attack;
      case Rush r -> attack;
      case Rotary r -> new Rotary(map(r.attacks(), x -> upgradeShotSpeed(x, percentage)), r.attackDuration(), r.cooldown());
      case Parallel p -> new Parallel(map(p.attacks(), x -> upgradeShotSpeed(x, percentage)));
      case Random r -> new Random(upgradeShotSpeed(r.attack(), percentage), r.random());
    };
  }

  private static Attack upgradeAttackDamage(Attack attack, int percentage) {
    BiFunction<Damage, Integer, Damage> increase = (d, p) -> new Damage(MathUtil.apply(d.damage(), p),
        d.type());
    return switch (attack) {
      case Shot s -> new Shot(increase.apply(s.damage(), percentage), s.speed(), s.cooldown());
      case Rush r -> new Rush(increase.apply(r.damage(), percentage), r.speed());
      case Rotary r -> new Rotary(map(r.attacks(), x -> upgradeAttackDamage(x, percentage)), r.attackDuration(), r.attackDuration());
      case Parallel p -> new Parallel(map(p.attacks(), x -> upgradeAttackDamage(x, percentage)));
      case Collision c -> new Collision(increase.apply(c.damage(), percentage));
      case Random r -> new Random(upgradeAttackDamage(r.attack(), percentage), r.random());
    };
  }

  private static Attack upgradeAttackSpeed(Attack attack, int percentage) {
    return switch (attack) {
      case Shot s -> new Shot(s.damage(), s.speed(), decreaseDuration(s.cooldown(), percentage));
      case Collision x -> attack;
      case Rush x -> attack;
      case Rotary r -> new Rotary(map(r.attacks(), x -> upgradeAttackSpeed(x, percentage)), r.attackDuration(), r.attackDuration());
      case Parallel p -> new Parallel(map(p.attacks(), x -> upgradeAttackSpeed(x, percentage)));
      case Random r -> new Random(upgradeAttackSpeed(r.attack(), percentage), r.random());
    };
  }
  //@formatter:on
}
