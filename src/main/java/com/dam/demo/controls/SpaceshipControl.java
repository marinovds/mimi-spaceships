package com.dam.demo.controls;

import static com.dam.demo.util.AssetUtil.checkBoundaries;
import static com.dam.demo.util.MathUtil.apply;
import static com.dam.demo.util.MathUtil.collided;

import com.dam.demo.game.context.Contexts;
import com.dam.demo.game.context.LevelContext;
import com.dam.demo.model.Dimensions;
import com.dam.demo.model.behaviour.spaceship.SpaceshipBehaviour;
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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

public final class SpaceshipControl extends AbstractControl {


  private final SpaceshipBehaviour behaviour;

  private final Map<Buff, Duration> buffs;

  public SpaceshipControl(
      SpaceshipBehaviour behaviour) {
    this.behaviour = behaviour;
    buffs = new HashMap<>();
  }

  @Override
  protected void controlUpdate(float tpf) {
    var activeBuffs = getActiveUpgrades(tpf);
    var movementIncrease = movementIncrease(activeBuffs);
    behaviour.move(apply(tpf, movementIncrease));
    var boundary = checkBoundaries(spatial.getLocalTranslation(), Dimensions.of(spatial));
    behaviour.onBoundary(boundary);
    var collisions = Stream.concat(
            Contexts.contextByClass(LevelContext.class).enemies.getChildren().stream(),
            Stream.of(Contexts.contextByClass(LevelContext.class).player.spatial())
        )
        .filter(x -> spatial != x) // Ignore yourself
        .filter(x -> collided(spatial, x))
        .toList();
    for (var collision : collisions) {
      behaviour.onCollision(collision, tpf);
    }

    behaviour.currentlyActiveBuffs(activeBuffs);
    behaviour.attack(tpf);
  }

  /**
   * Expires the non-active buffs and returns the active upgrades.
   *
   * @param tpf the time per frame in seconds
   * @return the list of currently active upgrades.
   */
  private List<Upgrade> getActiveUpgrades(float tpf) {
    var expired = getExpiredBuffs(tpf);
    for (var buff : expired) {
      buffs.remove(buff);
      var color = AssetUtil.getColor(spatial);
      AssetUtil.setColor(spatial, color.add(buff.color().mult(-1f)));
    }

    return buffs.keySet()
        .stream()
        .map(Buff::upgrade)
        .toList();
  }

  private static int movementIncrease(List<Upgrade> activeBuffs) {
    return activeBuffs.stream()
        .filter(x -> x.type() == UpgradeType.MOVEMENT_SPEED)
        .mapToInt(Upgrade::percentage)
        .findFirst()
        .orElse(0);
  }

  private List<Buff> getExpiredBuffs(float tpf) {
    buffs.replaceAll((b, d) -> MathUtil.subtractDuration(d, tpf));
    return buffs.entrySet()
        .stream()
        .filter(x -> x.getValue().isZero())
        .map(Entry::getKey)
        .toList();
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

  public SpaceshipBehaviour getBehaviour() {
    return behaviour;
  }
}
