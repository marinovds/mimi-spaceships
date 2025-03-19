package com.dam.demo.enemies;

import java.time.Duration;

public record SpawnCriteria(Duration cooldown, int random, int maxNumber) {

  public static final SpawnCriteria NONE = new SpawnCriteria(Duration.ZERO, 0, 1);
}
