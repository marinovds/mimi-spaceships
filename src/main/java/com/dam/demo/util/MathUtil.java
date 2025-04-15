package com.dam.demo.util;

import static java.lang.Math.abs;

import com.dam.demo.model.Dimensions;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import java.time.Duration;

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
    var heightDiff = (dimA.height() + dimB.height()) / 2;
    var widthDiff = (dimA.width() + dimB.width()) / 2;

    return abs(a.getLocalTranslation().x - b.getLocalTranslation().x) < widthDiff
        && abs(a.getLocalTranslation().y - b.getLocalTranslation().y) < heightDiff;
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

  public static int increase(int base, int percentage, int times) {
    var mult = 1f + percentage / 100f;
    float result = base;
    for (int i = 1; i < times; i++) {
      result *= mult;
    }
    return Math.round(result);
  }
}

