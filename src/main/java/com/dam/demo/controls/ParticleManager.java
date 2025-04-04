package com.dam.demo.controls;

import static com.dam.util.RandomUtil.RANDOM;

import com.dam.demo.enemies.Tag.ProjectileType;
import com.dam.demo.game.Scene;
import com.dam.demo.util.AssetUtil;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

public enum ParticleManager {
  ;

  private static final Spatial PARTICLE = AssetUtil.projectile("particle", ProjectileType.PARTICLE);

  public static void explosion(Vector3f position, int numberOfParticles) {
    var color = ColorRGBA.Yellow.clone();
    color.interpolateLocal(ColorRGBA.randomColor(), RANDOM.nextFloat());

    // create particles
    for (int i = 0; i < numberOfParticles; i++) {
      var velocity = getRandomVelocity(250);
      var particle = PARTICLE.clone();
      particle.setLocalTranslation(position);

      particle.addControl(new ParticleControl(velocity, color));
      Scene.PARTICLES.attachChild(particle);
    }
  }

  private static Vector3f getRandomVelocity(float max) {

    // generate Vector3f with random direction
    var velocity = new Vector3f(
        RANDOM.nextFloat() - 0.5f,
        RANDOM.nextFloat() - 0.5f,
        0).normalizeLocal();

    // apply semi-random particle speed
    var random = (40 + RANDOM.nextInt(51)) / 100f;
    var particleSpeed = max * random;
    velocity.multLocal(particleSpeed);

    return velocity;
  }


}
