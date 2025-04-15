package com.dam.demo.enemies;

import java.time.Duration;

public record SpawnCriteria(
    Duration cooldown,
    int random,
    int maxNumber,
    int level) {

  public static SpawnCriteria none(int level) {
    return new SpawnCriteria(Duration.ZERO, 0, 1, level);
  }
}
