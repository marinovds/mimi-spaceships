package com.dam.demo.game;

import com.dam.demo.listeners.KeyboardListener.Input;

public sealed interface GameContext permits MenuContext, LevelContext, ShopContext, HighScoreContext {

  void enable();

  void onTick(float tpf);

  void disable();

  void onInput(Input input, boolean isPressed);
}
