package com.dam.demo.model.menu;

public record MenuConfig(
    String name,
    boolean showNonSelectable,
    boolean rotaryIndex,
    String movementSound,
    int offset
) {

}
