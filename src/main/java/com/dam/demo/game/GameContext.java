package com.dam.demo.game.context;

import com.dam.demo.listeners.KeyboardListener.Action;
import com.dam.demo.listeners.KeyboardListener.Input;

public sealed interface GameContext permits MenuContext, LevelContext, ShopContext, HighScoreContext {

  void enable();

  void onTick(float tpf);

  void disable();

  void onInput(Input input, Action action);
}
