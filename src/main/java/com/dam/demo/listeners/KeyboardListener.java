package com.dam.demo.listeners;

import com.dam.demo.game.Contexts;
import com.jme3.input.controls.ActionListener;
import java.util.stream.Stream;

public class KeyboardListener implements ActionListener {

  @Override
  public void onAction(String key, boolean isPressed, float tpf) {
    var input = Input.fromKey(key);

    Contexts.onInput(input, isPressed);
  }

  public enum Input {
    PAUSE("escape"), UP("up"), DOWN("down"), SHOOT("shoot"), SELECT("select");

    public final String key;

    Input(String key) {
      this.key = key;
    }

    public static Input fromKey(String key) {
      return Stream.of(values())
          .filter(x -> x.key.equals(key))
          .findFirst()
          .orElseThrow(() -> new IllegalStateException("Unknown key binding for " + key));
    }
  }
}
