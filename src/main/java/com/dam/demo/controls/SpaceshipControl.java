package com.dam.demo.controls;

import static com.dam.demo.util.AssetUtil.checkBoundaries;
import static com.dam.demo.util.MathUtil.apply;
import static com.dam.demo.util.MathUtil.collided;

import com.dam.demo.controls.behaviour.spaceship.SpaceshipBehaviour;
import com.dam.demo.game.Scene;
import com.dam.demo.model.Dimensions;
import com.dam.demo.model.Spaceship;
import com.dam.demo.model.upgrade.Buff;
import com.dam.demo.model.upgrade.Upgrade;
import com.dam.demo.model.upgrade.UpgradeType;
import com.dam.demo.util.AssetUtil;
import com.dam.demo.util.MathUtil;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

public final class SpaceshipControl extends AbstractControl {

  private final Spaceship spaceship;
  private final SpaceshipBehaviour behaviour;

  private final Map<Buff, Duration> buffs;

  public SpaceshipControl(
      Spaceship spaceship,
      SpaceshipBehaviour behaviour) {
    this.spaceship = spaceship;
    this.behaviour = behaviour;
    buffs = new HashMap<>();
  }

  @Override
  protected void controlUpdate(float tpf) {
    expireBuffs(tpf);
    var movementIncrease = movementIncrease();
    behaviour.move(apply(tpf, movementIncrease));
    var boundary = checkBoundaries(spatial.getLocalTranslation(), Dimensions.of(spatial));
    behaviour.onBoundary(boundary);
    Stream.concat(
            Scene.ENEMIES.getChildren().stream(),
            Stream.of(Scene.PLAYER.spatial())
        )
        .filter(x -> spatial != x) // Ignore yourself
        .filter(x -> collided(spatial, x))
        .forEach(behaviour::onCollision);

    var activeBuffs = buffs.keySet()
        .stream()
        .map(Buff::upgrade)
        .toList();

    behaviour.currentlyActiveBuffs(activeBuffs);
    behaviour.attack(tpf);
  }

  private int movementIncrease() {
    return buffs.keySet()
        .stream()
        .map(Buff::upgrade)
        .filter(x -> x.type() == UpgradeType.MOVEMENT_SPEED)
        .mapToInt(Upgrade::percentage)
        .findFirst()
        .orElse(0);
  }

  private void expireBuffs(float tpf) {
    buffs.replaceAll((b, d) -> MathUtil.subtractDuration(d, tpf));
    var expired = buffs.entrySet()
        .stream()
        .filter(x -> x.getValue().isZero())
        .map(Entry::getKey)
        .toList();

    for (var buff : expired) {
      buffs.remove(buff);
      var color = AssetUtil.getColor(spatial);
      AssetUtil.setColor(spatial, color.add(buff.color().mult(-1f)));
    }
  }


  @Override
  protected void controlRender(RenderManager rm, ViewPort vp) {
    // Not used for 2D graphics
  }

  public void addBuff(Buff buff) {
    var prev = buffs.put(buff, buff.duration());
    if (prev == null) {
      var color = AssetUtil.getColor(spatial);
      AssetUtil.setColor(spatial, color.add(buff.color()));
    }
  }

}
