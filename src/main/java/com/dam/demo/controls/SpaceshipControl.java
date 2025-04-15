package com.dam.demo.controls;

import static com.dam.demo.util.AssetUtil.checkBoundaries;
import static com.dam.demo.util.MathUtil.apply;
import static com.dam.demo.util.MathUtil.collided;

import com.dam.demo.game.Contexts;
import com.dam.demo.game.LevelContext;
import com.dam.demo.game.Scene;
import com.dam.demo.model.Dimensions;
import com.dam.demo.model.Ticker;
import com.dam.demo.model.behaviour.spaceship.SpaceshipBehaviour;
import com.dam.demo.model.upgrade.Buff;
import com.dam.demo.model.upgrade.Upgrade;
import com.dam.demo.model.upgrade.UpgradeType;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import java.util.List;
import java.util.stream.Stream;

public final class SpaceshipControl extends AbstractControl {


  private final SpaceshipBehaviour behaviour;

  public SpaceshipControl(SpaceshipBehaviour behaviour) {
    this.behaviour = behaviour;
  }

  @Override
  protected void controlUpdate(float tpf) {
    var activeBuffs = getActiveBuffs(tpf);
    var movementIncrease = movementIncrease(activeBuffs);
    behaviour.move(apply(tpf, movementIncrease));
    var boundary = checkBoundaries(spatial.getLocalTranslation(), Dimensions.of(spatial));
    behaviour.onBoundary(boundary);
    var collisions = Stream.concat(
            Scene.ENEMIES.getChildren().stream(),
            Stream.of(Contexts.contextByClass(LevelContext.class).player.spatial())
        )
        .filter(x -> spatial != x) // Ignore yourself
        .filter(x -> collided(spatial, x))
        .toList();
    for (var collision : collisions) {
      behaviour.onCollision(collision, tpf);
    }

    behaviour.spaceship().setBuffs(activeBuffs);
    behaviour.attack(tpf);
  }

  /**
   * Get the buffs that are currently active.
   *
   * @param tpf the time per frame in seconds
   * @return the list of currently active buffs.
   */
  private List<Buff> getActiveBuffs(float tpf) {
    return behaviour.spaceship()
        .buffs()
        .stream()
        .map(x -> new Buff(x.upgrade(), Ticker.tick(x.duration(), tpf)))
        .filter(x -> x.duration().isPositive())
        .toList();
  }

  private static int movementIncrease(List<Buff> activeBuffs) {
    return activeBuffs.stream()
        .map(Buff::upgrade)
        .filter(x -> x.type() == UpgradeType.MOVEMENT_SPEED)
        .mapToInt(Upgrade::percentage)
        .findFirst()
        .orElse(0);
  }

  @Override
  protected void controlRender(RenderManager rm, ViewPort vp) {
    // Not used for 2D graphics
  }

  public SpaceshipBehaviour getBehaviour() {
    return behaviour;
  }
}
