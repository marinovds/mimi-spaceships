package com.dam.demo.util;

import static com.dam.demo.util.AssetUtil.manager;

import com.jme3.export.binary.BinaryExporter;
import com.jme3.scene.Node;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

public enum SaveUtil {
  ;

  private static final Logger LOGGER = Logger.getLogger(SaveUtil.class.getName());

  public static void save(String name, Node node) {
    var exporter = BinaryExporter.getInstance();
    try {
      exporter.save(node, getPath(name).toFile());
    } catch (IOException ex) {
      LOGGER.log(Level.SEVERE, "Error: Failed to save " + name + "!", ex);
    }
  }

  public static Node load(String name) {
    var path = "/Documents/My Games/MiMi Spaceships/" + name + ".j3o";

    return (Node) manager.loadModel(path);
  }


  public static boolean exits(String name) {
    return Files.exists(getPath(name));
  }

  public static void delete(String name) throws IOException {
    Files.delete(getPath(name));
  }

  private static Path getPath(String name) {
    return Path.of(
        System.getProperty("user.home"),
        "Documents",
        "My Games",
        "MiMi Spaceships",
        name + ".j3o");
  }
}
