package com.dam.demo.model;

import java.time.Duration;

public final class Ticker {

  private final Duration baseDuration;

  private Duration duration;

  private Ticker(Duration base, Duration current) {
    this.baseDuration = base;
    this.duration = current;
  }

  public static Ticker of(Duration base, Duration start) {
    return new Ticker(base, start);
  }

  public static Ticker of(Duration duration) {
    return new Ticker(duration, duration);
  }

  public static Duration tick(Duration duration, float tpf) {
    var nanos = (int) (tpf * 1_000_000_000f);
    var result = duration.minusNanos(nanos);
    return result.isNegative() ? Duration.ZERO : result;
  }

  public Ticker tick(float tpf) {
    duration = tick(duration, tpf);

    return this;
  }

  public boolean isDone() {
    return duration.isZero();
  }

  public Ticker reset() {
    return reset(baseDuration);
  }

  public Ticker reset(Duration duration) {
    this.duration = duration;
    return this;
  }

  public Duration baseDuration() {
    return baseDuration;
  }

  public Duration currentDuration() {
    return duration;
  }
}
