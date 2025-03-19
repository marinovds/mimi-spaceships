package com.dam.demo.controls;

import static java.lang.Math.min;

import com.dam.demo.util.AssetUtil;
import com.dam.demo.util.MathUtil;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class ParticleControl extends AbstractControl {

  private final Duration lifespan;
  private final Instant spawned;
  private final ColorRGBA color;
  private Vector3f velocity;

  public ParticleControl(Vector3f velocity, ColorRGBA color) {
    this.velocity = velocity;
    this.lifespan = Duration.ofMillis(1_500);
    this.spawned = Instant.now();
    this.color = color;
  }

  @Override

  protected void controlUpdate(float tpf) {

    // movement
    spatial.move(velocity.mult(tpf * 3f));
    velocity.multLocal(1 - 3f * tpf);
    if (Math.abs(velocity.x) + Math.abs(velocity.y) < 0.001f) {
      velocity = Vector3f.ZERO;
    }
    // rotation
    if (velocity != Vector3f.ZERO) {
      spatial.rotateUpTo(velocity.normalize());
      spatial.rotate(0, 0, FastMath.PI / 2f);

    }

    // scaling and alpha
    float speed = velocity.length();
    var alpha = calculateAlpha(speed);
    AssetUtil.setColor(spatial, color.setAlpha(alpha));
    spatial.setLocalScale(0.3f + min(min(1.5f, 0.02f * speed + 0.1f), alpha));
    spatial.scale(0.65f);

    // is particle expired?
    if (!MathUtil.inCooldown(spawned, lifespan)) {
      spatial.removeFromParent();
    }
  }

  private float calculateAlpha(float speed) {
    long difTime = ChronoUnit.MILLIS.between(spawned, Instant.now());

    float percentLife = 1 - (float) difTime / lifespan.toMillis();
    float alpha = min(1.5f, min(percentLife * 2, speed));
    return alpha * alpha;
  }

  @Override
  protected void controlRender(RenderManager rm, ViewPort vp) {
  }

}
