package com.dam.demo.game;

import static com.dam.demo.util.AssetUtil.screenHeight;
import static com.dam.demo.util.AssetUtil.screenWidth;

import com.dam.demo.listeners.KeyboardListener.Action;
import com.dam.demo.listeners.KeyboardListener.Input;
import com.dam.demo.util.AssetUtil;
import com.dam.demo.util.SoundUtil;
import com.jme3.app.SimpleApplication;
import com.jme3.scene.Node;

public final class HighScoreContext implements GameContext {

  private final Node guiNode;
  private final Node spatial;

  HighScoreContext(SimpleApplication app) {
    this.guiNode = app.getGuiNode();
    this.spatial = new Node("highScore");
  }

  @Override
  public void enable() {
    SoundUtil.music("lose");

    var text = AssetUtil.text(60);
    text.setText("Defeat");
    var x = (screenWidth() - text.getLineWidth()) / 2f;
    var y = screenHeight() / 2f;
    text.setLocalTranslation(x, y, 0);
    spatial.attachChild(text);

    guiNode.attachChild(spatial);
  }

  @Override
  public void onTick(float tpf) {

  }

  @Override
  public void disable() {
    spatial.detachAllChildren();
    spatial.removeFromParent();
  }

  @Override
  public void onInput(Input input, boolean isPressed) {
    if (Input.SELECT == input && isPressed) {
      Contexts.switchContext(MenuContext.class);
    }
    // TODO;
  }
}
