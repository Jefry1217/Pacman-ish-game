package gremlins;

import processing.core.PApplet;
import processing.core.PImage;

public class GameObject {

  /**
   * The sprite of the object
   */
  protected PImage sprite;

  /**
   * The x (horizontal) coordinate of the object. An int from 0-720
   */
  protected int x;

  /**
   * The y (vertical) coordinate of the object. An int from 0-720
   */
  protected int y;

  /**
   * The constructor for the GameObject class
   * @param sprite the image of the GameObject
   * @param x the horizontal location
   * @param y the vertical location
   */
  public GameObject(PImage sprite, int x, int y) {
    this.sprite = sprite;
    this.x = x;
    this.y = y;
  }

  /**
   * draws the objects sprite at its location
   * @param app The PApplet app to draw onto
   */
  public void draw(PApplet app) {
    app.image(this.sprite, this.x, this.y);
  }
} 