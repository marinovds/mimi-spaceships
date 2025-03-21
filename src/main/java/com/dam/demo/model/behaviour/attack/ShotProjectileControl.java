package com.dam.demo.model.behaviour.attack;

import static com.dam.demo.util.AssetUtil.checkBoundaries;

import com.dam.demo.model.Dimensions;
import com.dam.demo.model.attack.Shot;
import com.dam.demo.util.DamageUtil;
import com.dam.demo.util.MathUtil;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import java.util.List;
import java.util.function.Supplier;

public class ShotProjectileControl extends AbstractControl {

  private final Vector3f direction;
  private final Shot shot;
  private final Supplier<List<Spatial>> targets;

  public ShotProjectileControl(
      Vector3f direction,
      Shot shot,
      Supplier<List<Spatial>> targets) {
    this.shot = shot;
    this.direction = direction;
    this.targets = targets;
  }

  @Override
  protected void controlUpdate(float tpf) {
    // movement
    spatial.move(direction.mult(shot.speed() * tpf));
    var collided = targets.get()
        .stream()
        .filter(x -> MathUtil.collided(spatial, x))
        .findFirst();
    if (collided.isPresent()) {
      spatial.removeFromParent();
      enabled = false;
      var spatial = collided.get();
      DamageUtil.hit(spatial, shot.damage());
      return;
    }
    // check boundaries
    var boundary = checkBoundaries(spatial.getLocalTranslation(), Dimensions.of(spatial));
    if (boundary.top() || boundary.left() || boundary.bottom() || boundary.right()) {
      spatial.removeFromParent();
    }
  }

  @Override
  protected void controlRender(RenderManager rm, ViewPort vp) {

  }

}
