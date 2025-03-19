package com.dam.demo.model;

import com.jme3.scene.Spatial;

public record Dimensions(
    float radius,
    float width,
    float height) {

  public static Dimensions of(Spatial spatial) {
    return new Dimensions(
        spatial.getUserData("radius"),
        spatial.getUserData("width"),
        spatial.getUserData("height")
    );
  }

  public void applyTo(Spatial spatial) {
    spatial.setUserData("radius", radius);
    spatial.setUserData("width", width);
    spatial.setUserData("height", height);
  }
}
