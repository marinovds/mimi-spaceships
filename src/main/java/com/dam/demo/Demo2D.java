package com.dam.demo;

import static com.dam.demo.listeners.KeyboardListener.Input.DOWN;
import static com.dam.demo.listeners.KeyboardListener.Input.PAUSE;
import static com.dam.demo.listeners.KeyboardListener.Input.SELECT;
import static com.dam.demo.listeners.KeyboardListener.Input.SHOOT;
import static com.dam.demo.listeners.KeyboardListener.Input.UP;
import static com.jme3.input.KeyInput.KEY_D;
import static com.jme3.input.KeyInput.KEY_DOWN;
import static com.jme3.input.KeyInput.KEY_ESCAPE;
import static com.jme3.input.KeyInput.KEY_RETURN;
import static com.jme3.input.KeyInput.KEY_RIGHT;
import static com.jme3.input.KeyInput.KEY_S;
import static com.jme3.input.KeyInput.KEY_SPACE;
import static com.jme3.input.KeyInput.KEY_UP;
import static com.jme3.input.KeyInput.KEY_W;

import com.dam.demo.game.context.Contexts;
import com.dam.demo.listeners.KeyboardListener;
import com.dam.demo.util.AssetUtil;
import com.dam.demo.util.SoundUtil;
import com.jme3.app.SimpleApplication;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.Vector3f;
import com.jme3.system.AppSettings;

public class Demo2D extends SimpleApplication {

  public static void main(String[] args) {
    var app = new Demo2D();
    AppSettings settings = new AppSettings(true);
    settings.setTitle("MiMi spaceships");
    settings.setWindowSize(1024, 768);
    app.setSettings(settings);
    app.start();
  }

  @Override
  public void simpleInitApp() {
    AssetUtil.initialize(assetManager, settings);
    Contexts.initialize(this);

    // setup camera for 2D games
    cam.setParallelProjection(true);
    cam.setLocation(new Vector3f(0, 0, 0.5f));
    getFlyByCamera().setEnabled(false);

    // turn off stats view (you can leave it on, if you want)
    setDisplayStatView(false);
    setDisplayFps(false);
    addInputs();

    guiNode.attachChild(SoundUtil.initialize());
    SoundUtil.music("ambient");

  }

  @Override
  public void simpleUpdate(float tpf) {
    Contexts.onTick(tpf);
  }

  private void addInputs() {
    // Remove the defaults
    inputManager.clearMappings();

    var keyboardListener = new KeyboardListener();
    inputManager.addMapping(PAUSE.key, new KeyTrigger(KEY_ESCAPE));
    inputManager.addMapping(SELECT.key, new KeyTrigger(KEY_RETURN));
    inputManager.addMapping(UP.key, new KeyTrigger(KEY_W), new KeyTrigger(KEY_UP));
    inputManager.addMapping(DOWN.key, new KeyTrigger(KEY_S), new KeyTrigger(KEY_DOWN));
    inputManager.addMapping(SHOOT.key,
        new KeyTrigger(KEY_RIGHT),
        new KeyTrigger(KEY_D),
        new KeyTrigger(KEY_SPACE));

    inputManager.addListener(keyboardListener, PAUSE.key);
    inputManager.addListener(keyboardListener, SELECT.key);
    inputManager.addListener(keyboardListener, UP.key);
    inputManager.addListener(keyboardListener, DOWN.key);
    inputManager.addListener(keyboardListener, SHOOT.key);
  }
}
