package com.dam.demo.controls;

import static com.dam.demo.game.Scene.PLAYER;
import static com.dam.demo.util.AssetUtil.checkBoundaries;

import com.dam.demo.game.SoundUtil;
import com.dam.demo.model.Dimensions;
import com.dam.demo.model.Spaceship;
import com.dam.demo.util.MathUtil;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import java.util.function.Consumer;

public class BonusControl extends AbstractControl {

  private final int speed;
  private final Vector3f aim;
  private final Consumer<Spaceship> bonus;

  public BonusControl(Vector3f aim, Consumer<Spaceship> bonus) {
    this.speed = 200;
    this.aim = aim;
    this.bonus = bonus;
  }

  @Override
  protected void controlUpdate(float tpf) {
    var dimensions = Dimensions.of(spatial);
    spatial.move(aim.mult(tpf * speed));

    if (MathUtil.collided(PLAYER.spatial(), spatial)) {
      bonus.accept(PLAYER);
      SoundUtil.play("coin");
      spatial.removeFromParent();
    }

    if (checkBoundaries(spatial.getLocalTranslation(), dimensions).left()) {
      spatial.removeFromParent();
    }
  }

  @Override
  protected void controlRender(RenderManager rm, ViewPort vp) {

  }
}
