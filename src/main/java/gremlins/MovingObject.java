package gremlins;

import processing.core.PImage;
import java.util.List;

public abstract class MovingObject extends GameObject {

  /**
   * The amount of pixels the object will move each frame when moving. 
   */
  protected int SPEED;

  /**
   * Direction of object expressed as an integer
   * 0 = left, 1 = up, 2 = right, 3 = down
   */
  protected int direction = 2;

  /**
   * Constructor for MovingObject class
   * @param sprite the image of the MovingObject to draw
   * @param x the horizontal location
   * @param y the vertical location
   * @param SPEED the speed of the MovingObject, number of pixels it moves per frame
   * @param direction the direction of the MovingObject
   */
  public MovingObject(PImage sprite, int x, int y, int SPEED, int direction) {
    super(sprite, x, y);
    this.SPEED = SPEED;
    this.direction = direction;
  }

  /**
   * checks whether an object has hit a slime. 
   * @param slimes list of Slime objects from game. 
   * @return Projectile that the object hit if object has hit a slime, null if it hasn't. 
   */
  public Projectile onASlime(List<Projectile> slimes) {
    for (Projectile s : slimes) {
      if (s.x - (s.x % 20) == this.x - (this.x % 20) && s.y - (s.y % 20) == this.y - (this.y % 20)) {
        return s;
      }
    }
    return null;
  }

  /**
   * checks whether an object has hit a gremlin
   * @param gremlins list of Gremlin objects from game.
   * @return Gremlin that the object hit if object has hit a gremlin, null if it hasn't.
   */
  public Gremlin onAGremlin(List<Gremlin> gremlins) {
    for (Gremlin g : gremlins) {
      if (g.x - (g.x % 20) == this.x - (this.x % 20) && g.y - (g.y % 20) == this.y - (this.y % 20)) {
        return g;
      }
    }
    return null;
  }
} 