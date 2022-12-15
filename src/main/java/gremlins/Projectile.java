package gremlins;

import processing.core.PImage;

public class Projectile extends MovingObject{

  /**
   * 
   * @param sprite the image of the Projectile
   * @param x the horizontal location 
   * @param y the vertical location
   * @param DIRECTION the direction the projectile will move in
   */
  public Projectile(PImage sprite, int x, int y, int DIRECTION) {
    super(sprite, x, y, 4, DIRECTION);
  }

  /**
   * updates the position of the projectile depending on its direction
   */
  public void tick() {
    if (this.direction == 0) {
      this.x -= SPEED;
    } else if (this.direction == 1) {
      this.y -= SPEED;
    } else if (this.direction == 2) {
      this.x += SPEED;
    } else if (this.direction == 3) {
      this.y += SPEED;
    }
  }
} 