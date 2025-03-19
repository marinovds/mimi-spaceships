package com.dam.demo.model.attack;

import com.dam.demo.model.attack.Attack.Collision;
import com.dam.demo.model.attack.Attack.Parallel;
import com.dam.demo.model.attack.Attack.Random;
import com.dam.demo.model.attack.Attack.Rotary;
import com.dam.demo.model.attack.Attack.Rush;
import com.dam.demo.model.attack.Attack.Shot;
import com.dam.demo.util.JsonUtil;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.time.Duration;
import java.util.List;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type")
@JsonSubTypes({
    @Type(value = Shot.class, name = "shot"),
    @Type(value = Rush.class, name = "rush"),
    @Type(value = Rotary.class, name = "rotary"),
    @Type(value = Parallel.class, name = "parallel"),
    @Type(value = Collision.class, name = "collision"),
    @Type(value = Random.class, name = "random"),
})
public sealed interface Attack {

  static Attack parse(String value) {
    return JsonUtil.read(value, Attack.class);
  }

  static String toString(Attack attack) {
    return JsonUtil.write(attack);
  }

  record Shot(Damage damage, int speed, Duration cooldown) implements Attack {

  }

  record Rush(Damage damage, int speed) implements Attack {

  }

  record Rotary(List<Attack> attacks, Duration attackDuration, Duration cooldown) implements
      Attack {

  }

  record Parallel(List<Attack> attacks) implements Attack {

  }

  record Collision(Damage damage) implements Attack {

  }

  record Random(Attack attack, int random) implements Attack {

  }
}
