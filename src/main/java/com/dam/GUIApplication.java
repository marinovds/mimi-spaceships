package com.dam;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.system.AppSettings;

public class GUIApplication extends SimpleApplication {

  public static void main(String[] args) {

    GUIApplication app = new GUIApplication();
    AppSettings settings = new AppSettings(true);
    settings.setTitle("Deal or Not");
    settings.setFullscreen(true);
    app.setSettings(settings);

    app.start();
  }

  @Override
  public void simpleInitApp() {
    var head = new Geometry("head", new Box(0.5f, 0.5f, 0.5f));
    var headMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    headMat.setColor("Color", ColorRGBA.fromRGBA255(237, 28, 36, 255));
    head.setLocalTranslation(0.125f, 2.5f, 0);
    head.setMaterial(headMat);

    var torso = new Geometry("torso", new Box(1f, 2f, 0.5f));
    var torsoMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    torsoMat.setColor("Color", ColorRGBA.fromRGBA255(255, 242, 0, 255));
    torso.setMaterial(torsoMat);

    var leftHand = createHand("left hand", new Vector3f(1.25f, -0.1f, -0.25f));
    var rightHand = createHand("right hand", new Vector3f(-1.25f, -0.1f, -0.25f));

    var leftLeg = createHand("left leg", new Vector3f(0.75f, -4.1f, -0.25f));
    var rightLeg = createHand("right leg", new Vector3f(-0.75f, -4.1f, -0.25f));

    var person = new Node();
    person.setUserData("health", 100);
    rootNode.attachChild(person);
    person.attachChild(head);
    person.attachChild(leftHand);
    person.attachChild(torso);
    person.attachChild(rightHand);
    person.attachChild(leftLeg);
    person.attachChild(rightLeg);


    /** create a blue box at coordinates (1,-1,1) */
//    Box box1 = new Box(1,1,1);
//    Geometry blue = new Geometry("Box", box1);
//    blue.setLocalTranslation(new Vector3f(1,-1,1));
//    Material mat1 = new Material(assetManager,
//        "Common/MatDefs/Misc/Unshaded.j3md");
//    mat1.setColor("Color", ColorRGBA.Blue);
//    blue.setMaterial(mat1);
//
//    /** create a red box straight above the blue one at (1,3,1) */
//    Box box2 = new Box(1,1,1);
//    Geometry red = new Geometry("Box", box2);
//    red.setLocalTranslation(new Vector3f(1,3,1));
//    Material mat2 = new Material(assetManager,
//        "Common/MatDefs/Misc/Unshaded.j3md");
//    mat2.setColor("Color", ColorRGBA.Red);
//    red.setMaterial(mat2);
//
//    /** Create a pivot node at (0,0,0) and attach it to the root node */
//    Node pivot = new Node("pivot");
//    rootNode.attachChild(pivot); // put this node in the scene
//
//    /** Attach the two boxes to the *pivot* node. (And transitively to the root node.) */
//    pivot.attachChild(blue);
//    pivot.attachChild(red);
    /** Rotate the pivot node: Note that both boxes have rotated! */
//    pivot.rotate(.4f,.4f,0f);
  }

  private Geometry createHand(String name, Vector3f position) {
    var hand = new Geometry(name, new Box(0.25f, 2.1f, 0.25f));
    var handMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    handMat.setColor("Color", ColorRGBA.fromRGBA255(127, 130, 187, 255));
    hand.setLocalTranslation(position);
    hand.setMaterial(handMat);

    return hand;
  }

  @Override
  public void simpleUpdate(float tpf) {
  }
}
