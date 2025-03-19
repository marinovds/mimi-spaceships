package com.dam.demo.controls;

import static com.dam.demo.util.AssetUtil.checkBoundaries;
import static com.dam.demo.util.MathUtil.apply;

import com.dam.demo.controls.behaviour.attack.AttackBehaviour;
import com.dam.demo.controls.behaviour.movement.MovementBehaviour;
import com.dam.demo.model.Dimensions;
import com.dam.demo.model.upgrade.Buff;
import com.dam.demo.model.upgrade.Upgrade;
import com.dam.demo.model.upgrade.UpgradeType;
import com.dam.demo.model.upgrade.UpgradeUtil;
import com.dam.demo.util.AssetUtil;
import com.dam.demo.util.MathUtil;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public final class SpaceshipControl extends AbstractControl {


  private final MovementBehaviour movement;
  private final Map<Buff, Instant> buffs;

  private AttackBehaviour attack;

  public SpaceshipControl(
      AttackBehaviour attack,
      MovementBehaviour movement) {
    this.attack = attack;
    this.movement = movement;
    buffs = new HashMap<>();
  }

  @Override
  protected void controlUpdate(float tpf) {
    expireBuffs();
    var movementIncrease = movementIncrease();
    movement.onTick(apply(tpf, movementIncrease));
    var boundary = checkBoundaries(spatial.getLocalTranslation(), Dimensions.of(spatial));
    movement.onBoundary(boundary);
    attack.onTick(tpf);
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

  private void expireBuffs() {
    var expired = buffs.entrySet()
        .stream()
        .filter(x -> !MathUtil.inCooldown(x.getValue(), x.getKey().duration()))
        .map(Entry::getKey)
        .toList();

    for (var buff : expired) {
      buffs.remove(buff);
      applyBuff(buff.invert(), true);
    }
  }


  @Override
  protected void controlRender(RenderManager rm, ViewPort vp) {
    // Not used for 2D graphics
  }

  public MovementBehaviour movement() {
    return movement;
  }

  public void addBuff(Buff buff) {
    var prev = buffs.put(buff, Instant.now());
    applyBuff(buff, prev == null);
  }

  private void applyBuff(Buff buff, boolean applyColor) {
    if (applyColor) {
      var color = AssetUtil.getColor(spatial);
      AssetUtil.setColor(spatial, color.add(buff.color().mult(6f)));
    }
    attack = UpgradeUtil.buffAttack(attack, buff);
  }
}
