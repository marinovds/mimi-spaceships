package com.dam.demo.util;

import static com.dam.demo.enemies.Tag.SpatialType.BONUS;
import static com.dam.demo.enemies.Tag.SpatialType.PROJECTILE;
import static com.dam.demo.model.UserConstants.TAGS;

import com.dam.demo.model.Boundary;
import com.dam.demo.model.Dimensions;
import com.dam.demo.model.Spaceship;
import com.dam.demo.model.SpaceshipDefinition;
import com.jme3.asset.AssetManager;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.system.AppSettings;
import com.jme3.texture.Texture2D;
import com.jme3.ui.Picture;
import java.util.List;

public enum AssetUtil {
  ;

  public static AssetManager manager;
  private static AppSettings settings;

  public static void initialize(
      AssetManager assetManager,
      AppSettings appSettings) {
    manager = assetManager;
    settings = appSettings;
  }

  public static Spaceship spaceship(SpaceshipDefinition definition) {
    var spatial = image(definition.name());
    definition.applyTo(spatial);

    return Spaceship.of(spatial);
  }

  public static Spatial projectile(String name) {
    var result = image(name);
    result.setUserData("width", 0f);
    result.setUserData("height", 0f);
    result.setUserData(TAGS, new Object[]{PROJECTILE.name()});

    return result;
  }

  public static Spatial bonus(String name) {
    var result = images(List.of("buffBase", name));
    result.setUserData(TAGS, new Object[]{BONUS.name()});
    return result;
  }

  private static Spatial images(List<String> names) {
    var result = new Node();
    var pics = names.stream()
        .map(x -> {
          var pic = new Picture(x);
          var tex = (Texture2D) manager.loadTexture("textures/" + x + ".png");
          pic.setTexture(manager, tex, true);
          // adjust picture

          var width = tex.getImage().getWidth();
          var height = tex.getImage().getHeight();
          pic.setWidth(width);
          pic.setHeight(height);
          pic.move(-width / 2f, -height / 2f, 0);

          return pic;
        })
        .toList();
    var first = pics.get(0);
    var dimensions = new Dimensions(
        first.getWidth() / 2f,
        first.getWidth(),
        first.getHeight()
    );
    dimensions.applyTo(result);
    pics.forEach(result::attachChild);
    result.setName(first.getName());

    return result;
  }

  private static Spatial image(String name) {
    return images(List.of(name));
  }

  public static int screenHeight() {
    return settings.getHeight();
  }

  public static int screenWidth() {
    return settings.getWidth();
  }

  public static void pause(Spatial spatial, boolean enabled) {
    for (var i = 0; i < spatial.getNumControls(); i++) {
      var control = (AbstractControl) spatial.getControl(i);
      control.setEnabled(enabled);
    }
  }

  public static void pause(Node node, boolean enabled) {
    node.getChildren().forEach(x -> pause(x, enabled));
  }

  public static Boundary checkBoundaries(Spaceship spaceship) {
    return checkBoundaries(spaceship.location(), spaceship.dimensions());
  }

  public static Boundary checkBoundaries(Vector3f location, Dimensions dimensions) {
    return new Boundary(
        location.x <= dimensions.width() / 2,
        location.x >= screenWidth() - dimensions.width() / 2,
        location.y >= screenHeight() - dimensions.height() / 2,
        location.y <= dimensions.height() / 2
    );
  }

  public static Spatial setColor(Spatial spatial, ColorRGBA color) {
    if (!(spatial instanceof Node)) {
      throw new IllegalStateException(
          "Incorrect spatial passed. Node expected, but got " + spatial.getClass().getSimpleName());
    }
    var image = (Picture) ((Node) spatial).getChild(0);
    image.getMaterial().setColor("Color", color);

    return spatial;
  }

  public static ColorRGBA getColor(Spatial spatial) {
    if (!(spatial instanceof Node)) {
      throw new IllegalStateException(
          "Incorrect spatial passed. Node expected, but got " + spatial.getClass().getSimpleName());
    }
    var image = (Picture) ((Node) spatial).getChild(0);
    ;
    return (ColorRGBA) image.getMaterial().getParam("Color").getValue();
  }
}
