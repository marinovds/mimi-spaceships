package com.dam.demo.model.upgrade;

import com.jme3.math.ColorRGBA;
import java.time.Duration;

public record Buff(Upgrade upgrade, Duration duration, ColorRGBA color) {

}
