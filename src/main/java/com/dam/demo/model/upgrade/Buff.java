package com.dam.demo.model.upgrade;

import com.jme3.math.ColorRGBA;
import java.time.Duration;

public record Buff(Upgrade upgrade, Duration duration, ColorRGBA color) {

  public Buff invert() {

    return new Buff(
        new Upgrade(-upgrade.percentage(), upgrade.type()),
        duration,
        color.mult(-1f)
    );
  }

}
