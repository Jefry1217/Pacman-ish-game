package gremlins;

import processing.core.PImage;
import processing.core.PApplet;

public class Wizard extends MovingCharacter {

  /**
   * the direction to move once the wizard gets to the centre of the next tile
   */
  protected boolean stillOnTeleporter = false;
  protected int nextDirection = -1;

  /**
   * Constructor for Wizard class 
   * @param sprite the image of the wizard to draw
   * @param x the horizontal location
   * @param y the vertical location
   * @param SPEED the speed of the wizard, number of pixels it moves per frame
   * @param COOLDOWN the cooldown of the wizard shooting
   */
  public Wizard(PImage sprite, int x, int y, int SPEED, float COOLDOWN) {
    super(sprite, x, y, SPEED, COOLDOWN);
  }

  /**
   * if on the centre of tile, sets possible directions by calling function,
   * and sets the current direction to the directoin saved in nextDirection
   * attribute. then calls move and updatetimer functions. 
   */
  public void tick(GameObject[][] stoneWalls, BrickWall[][] brickWalls) {
    if (this.x % 20 == 0 && this.y % 20 == 0) {
      this.setPossibleDirections(stoneWalls, brickWalls);
      this.direction = this.nextDirection;
    }
    this.move();
    this.updateTimer();
  }

  /**
   * checks how far along shooting projectile cooldown timer is in quartiles. 
   * @return an int representing how many quartiles of the cooldown bar to fill up.
   */
  public int getProgress() {
    float frames = COOLDOWN * App.FPS;
    if (this.timer < frames * 0.25) {
      return 0;
    }
    else if (this.timer >= frames * 0.25 && this.timer < frames * 0.5) {
      return 1;
    }
    else if (this.timer >= frames * 0.5 && this.timer < frames * 0.75) {
      return 2;
    }
    else {
      return 3;
    }
  }

  /**
   * draws the recharge bar for shooting another fireball
   * @param app the PApplet to draw the bar onto. 
   */
  public void updateRechargeBar(PApplet app) {
    app.fill(0, 0, 0);
    app.rect(620, 680, 80, 10);
    app.fill(204, 102, 0);
    int progress = this.getProgress();
    if (progress == 1) {
      app.rect(620, 680, 20, 10);
    } else if (progress == 2) {
      app.rect(620, 680, 40, 10);
    } else if (progress == 3) {
      app.rect(620, 680, 60, 10);
    }
  }
} 