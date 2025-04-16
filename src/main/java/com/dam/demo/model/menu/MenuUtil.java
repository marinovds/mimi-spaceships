package com.dam.demo.model.menu;

import com.dam.demo.util.AssetUtil;
import com.jme3.font.BitmapText;
import com.jme3.scene.Spatial;
import java.util.function.BooleanSupplier;

public enum MenuUtil {
  ;

  public static MenuEntry centeredText(String text, int size) {

    return new CentredText(
        AssetUtil.text(text, size),
        MenuAction.NO_ON,
        () -> false,
        -1,
        -1);
  }

  public static MenuEntry selectableText(
      String text,
      MenuAction action,
      int selectedSize,
      int unselectedSize) {

    return new CentredText(
        AssetUtil.text(text, unselectedSize),
        action,
        () -> true,
        selectedSize,
        unselectedSize);
  }

  public static MenuEntry text(
      String text,
      BooleanSupplier available,
      MenuAction action,
      int selectedSize,
      int unselectedSize) {

    return new CentredText(
        AssetUtil.text(text, unselectedSize),
        action,
        available,
        selectedSize,
        unselectedSize);
  }

  public record CentredText(
      BitmapText text,
      MenuAction action,
      BooleanSupplier available,
      int selectedSize,
      int unselectedSize) implements MenuEntry {

    @Override
    public float height() {
      return selectedSize;
    }

    @Override
    public float width() {
      return text.getLineWidth();
    }

    @Override
    public boolean selectable() {
      return available.getAsBoolean();
    }

    @Override
    public void onCursor(boolean state) {
      var size = state ? selectedSize : unselectedSize;
      text.setSize(size);
    }

    @Override
    public MenuAction action() {
      return action;
    }

    @Override
    public Spatial spatial() {
      return text;
    }
  }
}
