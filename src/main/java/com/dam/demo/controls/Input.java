package com.dam.demo.controls;

import java.util.stream.Stream;

public enum Input {
  PAUSE("escape"), UP("up"), DOWN("down"), SHOOT("shoot");

  public final String key;

  Input(String key) {
    this.key = key;
  }

  public static Input fromKey(String key) {
    return Stream.of(values())
        .filter(x -> x.key.equals(key))
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("Unknown key binding for " + key));
  }
}
