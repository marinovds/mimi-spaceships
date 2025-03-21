package com.dam.demo.listeners;

import com.dam.demo.game.context.Contexts;
import com.jme3.input.controls.ActionListener;
import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Stream;

public class KeyboardListener implements ActionListener {

  private static final Map<Input, Boolean> INPUTS = new EnumMap<>(Map.of(
      Input.UP, false,
      Input.DOWN, false,
      Input.PAUSE, false,
      Input.SHOOT, false,
      Input.SELECT, false
  ));

  @Override
  public void onAction(String key, boolean isPressed, float tpf) {
    var input = Input.fromKey(key);
    var action = action(INPUTS.get(input), isPressed);

    INPUTS.put(input, isPressed);
    Contexts.onInput(input, action);
  }

  private static Action action(boolean currentState, boolean newState) {
    return currentState == newState
        ? Action.HOLD
        : newState
            ? Action.PRESS
            : Action.RELEASE;
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

  public enum Action {
    PRESS(true), HOLD(true), RELEASE(false);

    final boolean pressed;

    Action(boolean pressed) {
      this.pressed = pressed;
    }

    public boolean isPressed() {
      return pressed;
    }
  }
}
