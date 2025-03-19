package com.dam.deal;

import java.util.List;

public enum Rules {
  ;

  public static final List<Double> AMOUNTS = List.of(
      0.01D,
      0.10D,
      0.50D,
      1D,
      2D,
      5D,
      10D,
      50D,
      100D,
      250D,
      500D,
      750D,
      1_000D,
      1_500D,
      2_500D,
      5_000D,
      7_500D,
      10_000D,
      12_500D,
      15_000D,
      20_000D,
      25_000D,
      50_000D,
      100_000D
  );

  public static final List<Integer> ROUNDS = List.of(6, 4, 3, 3, 3, 2, 1);
}
