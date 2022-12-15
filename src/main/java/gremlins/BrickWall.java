package gremlins;

import processing.core.PImage;

public class BrickWall extends GameObject {
  /**
   * The phase of the BrickWall, only changes once it has been hit by a fireball. 
   * 0 = regular brick wall. 1, 2, 3, 4 = first, second, third, and fourth stage of 
   * being destroyed respectively. 
   */
  protected int phase = 0;

  /**
   * The constructor for the BrickWall class
   * @param sprite the image of the BrickWall
   * @param x the horizontal location
   * @param y the vertical location
   */
  public BrickWall(PImage sprite, int x, int y) {
    super(sprite, x, y);
  }
} 