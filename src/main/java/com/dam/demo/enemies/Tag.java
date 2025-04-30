package com.dam.demo.enemies;

import static com.dam.demo.model.UserConstants.TAGS;

import com.jme3.scene.Spatial;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;

public sealed interface Tag {

  static Set<Tag> parse(Object[] data) {
    return Stream.of(data)
        .map(Object::toString)
        .flatMap(x -> of(x).stream())
        .collect(HashSet::new, Set::add, Set::addAll);
  }

  static String[] tags(Set<Tag> tags, Tag other) {
    return Stream.concat(
            tags.stream(),
            Stream.of(other)
        )
        .map(Object::toString)
        .toArray(String[]::new);
  }

  static Optional<Tag> of(String input) {
    return Stream.of(
            as(SpatialType::values, input),
            as(ArmorType::values, input),
            as(ShipType::values, input),
            as(EnemyType::values, input),
            as(ProjectileType::values, input)
        )
        .filter(Optional::isPresent)
        .map(x -> (Tag) x.get())
        .findFirst();
  }

  static <T extends Enum<T> & Tag> Optional<T> as(Supplier<T[]> f, String value) {
    return Arrays.stream(f.get())
        .filter(x -> x.name().equals(value))
        .findFirst();
  }

  default boolean is(Spatial spatial) {
    var tags = (Object[]) spatial.getUserData(TAGS);
    if (tags == null) {
      return false;
    }

    return parse(tags).contains(this);
  }

  enum SpatialType implements Tag {
    SPACESHIP, PROJECTILE, BONUS
  }

  enum ArmorType implements Tag {
    HEAVY, LIGHT;
  }

  enum ShipType implements Tag {
    PLAYER, ENEMY, BOSS
  }

  enum EnemyType implements Tag {
    CRUISER, BOMBER, CHASER
  }

  enum ProjectileType implements Tag {
    BULLET, ROCKET, PARTICLE
  }
}
