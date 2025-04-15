package com.dam.demo.model.menu;

import com.jme3.scene.Spatial;

public interface MenuEntry {

  float height();

  float width();

  boolean selectable();

  void onCursor(boolean state);

  MenuAction action();

  Spatial spatial();

}
