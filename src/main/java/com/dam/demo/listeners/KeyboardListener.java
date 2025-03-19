package com.dam.demo.listeners;

import com.dam.demo.controls.Input;
import com.dam.demo.game.Level;
import com.jme3.input.controls.ActionListener;
import java.util.EnumMap;
import java.util.Map;

public class KeyboardListener implements ActionListener {

  public static final Map<Input, Boolean> INPUTS = new EnumMap<>(Map.of(
     Input.UP, false,
     Input.DOWN, false,
     Input.PAUSE, false,
     Input.SHOOT, false
  ));

  @Override
  public void onAction(String key, boolean isPressed, float tpf) {
    var input = Input.fromKey(key);
    if (input == Input.PAUSE && !isPressed) {
      Level.pause();
      return;
    }
    INPUTS.put(input, isPressed);
  }

}
