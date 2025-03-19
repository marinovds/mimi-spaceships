package com.dam.demo.game;

import com.dam.demo.util.AssetUtil;
import com.jme3.audio.AudioData.DataType;
import com.jme3.audio.AudioNode;
import com.jme3.audio.AudioSource.Status;
import com.jme3.scene.Node;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SoundUtil {

  private static Map<String, AudioNode> AUDIO = new HashMap<>();

  public static Node initialize() {
    var result = new Node("audio");
    getSounds("sounds").forEach(x -> {
      var audio = createSound(x);
      result.attachChild(audio);
    });

    getSounds("sounds/music").forEach(x -> {
      var audio = createAmbient(x);
      result.attachChild(audio);
    });

    return result;
  }

  public static void play(String tune) {
    AUDIO.get(tune).playInstance();
  }

  public static void music(String tune) {
    var audio = AUDIO.get(tune);
    if (audio.getStatus() == Status.Playing) {
      // music already playing. Don't do anything
      return;
    }
    // Stop the currently playing music and run the new tunes.
    AUDIO.values().stream()
        .filter(AudioNode::isLooping)
        .filter(x -> x.getStatus() == Status.Playing)
        .forEach(AudioNode::stop);
    audio.play();
  }

  private static List<String> getSounds(String folder) {
    try (var files = Files.list(Path.of("src/main/resources/" + folder))) {
      return files.filter(x -> !Files.isDirectory(x))
          .map(x -> x.getName(x.getNameCount() - 1))
          .map(Path::toString)
          .toList();
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  private static AudioNode createSound(String name) {
    var filename = "sounds/" + name;
    var audio = new AudioNode(AssetUtil.manager, filename, DataType.Buffer);
    audio.setPositional(false);
    audio.setLooping(false);
    audio.setVolume(1);
    AUDIO.put(name.substring(0, name.indexOf('.')), audio);

    return audio;
  }

  private static AudioNode createAmbient(String name) {
    var filename = "sounds/music/" + name;
    var audio = new AudioNode(AssetUtil.manager, filename, DataType.Stream);
    audio.setPositional(false);
    audio.setLooping(true);
    audio.setVolume(0.8f);
    AUDIO.put(name.substring(0, name.indexOf('.')), audio);

    return audio;
  }

  private static String filename(String name) {
    return "sounds/" + name;
  }
}
