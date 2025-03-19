package com.dam.demo.util;

import static java.lang.Math.abs;

import com.dam.demo.model.Dimensions;
import com.dam.demo.model.Spaceship;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import java.time.Duration;
import java.time.Instant;

public enum MathUtil {
  ;

  public static float angleFromVector(Vector3f vec) {

    return new Vector2f(vec.x, vec.y).getAngle();
  }

  public static Vector3f getVectorFromAngle(float angle) {

    return new Vector3f(FastMath.cos(angle), FastMath.sin(angle), 0);
  }

  public static boolean collided(Spatial a, Spatial b) {
    if (b == null) {
      // Concurrency maybe?
      return false;
    }

    var dimA = Dimensions.of(a);
    var dimB = Dimensions.of(b);
    var heightDiff = dimA.height() / 2 + dimB.height() / 2;
    var widthDiff = dimA.width() / 2 + dimB.width() / 2;

    return abs(a.getLocalTranslation().x - b.getLocalTranslation().x) < widthDiff
        && abs(a.getLocalTranslation().y - b.getLocalTranslation().y) < heightDiff;
  }

  public static boolean inCooldown(Instant last, Duration cooldown) {
    if (last == null) {
      // no previous value. Not in cooldown
      return false;
    }

    return Instant.now().isBefore(last.plus(cooldown));
  }

  public static Vector3f getAimDirection(Vector3f location) {
    return new Vector3f(location.x, 0, 0).normalizeLocal();
  }

  public static boolean isDead(Spaceship spaceship) {
    return spaceship.health() <= 0;
  }

  /**
   * Increases the base value with the given percentages. If the percentages are negative, reverts
   * the value to the old base
   *
   * @param base       the base value to work with
   * @param percentage the percentage increase
   * @return the increased value or the reverted base
   */
  public static int apply(int base, int percentage) {
    var mult = 1f + Math.abs(percentage) / 100f;
    if (percentage < 0) {
      // In this case, revert to the last value

      return (int) (base / mult);
    }

    return (int) (base * mult);
  }

  /**
   * Increases the base value with the given percentages. If the percentages are negative, reverts
   * the value to the old base
   *
   * @param base       the base value to work with
   * @param percentage the percentage increase
   * @return the increased value or the reverted base
   */
  public static float apply(float base, int percentage) {
    var mult = 1f + Math.abs(percentage) / 100f;
    if (percentage < 0) {
      // In this case, revert to the last value
      return base / mult;
    }

    return base * mult;
  }

  public static Duration decreaseDuration(Duration base, int percentage) {
    var mult = 1f - Math.abs(percentage) / 100f;
    var result = percentage < 0
        ? (long) Math.floor(base.toMillis() / mult)
        : (long) Math.ceil(base.toMillis() * mult);

    return Duration.ofMillis(result);
  }
}

