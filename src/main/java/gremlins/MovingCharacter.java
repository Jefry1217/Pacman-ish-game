package gremlins;

import processing.core.PImage;

public abstract class MovingCharacter extends MovingObject {

  /**
   * the cooldown of shooting a projectile for the character, set by the config 
   * file for each level, doesn't change once set. 
   */
  protected final float COOLDOWN;

  /**
   * boolean array corresponding to whether the character can move in
   * each direction. (aka there isn't a wall in that direction).
   * 0th, 1st, 2nd, and 3rd index representing left, up, right, and 
   * down directions respectively. 
   */
  protected boolean[] possibleDirections = new boolean[4];

  /**
   * keeps track of how long it has been since a projectile has been fired.
   */
  protected float timer = 0;

  /**
   * boolean giving whether the character can shoot a projectile or not. 
   */
  protected boolean canShoot = true;

  /**
   * Constructor for MovingCharacter class 
   * @param sprite the image of the MovingCharacter to draw
   * @param x the horizontal location
   * @param y the vertical location
   * @param SPEED the speed of the MovingCharacter, number of pixels it moves per frame
   * @param COOLDOWN the cooldown of the MovingCharacter shooting
   */
  public MovingCharacter(PImage sprite, int x, int y, int SPEED, float COOLDOWN) {
    super(sprite, x, y, SPEED, 2);
    this.COOLDOWN = COOLDOWN;
  }

  /**
   * sets boolean values for possibleDirections attribute
   * @param stoneWalls list of stonewalls from the game
   * @param brickWalls list of brickwalls from the game
   */
  public void setPossibleDirections(GameObject[][] stoneWalls, BrickWall[][] brickWalls) {
    for (int i = 0; i < this.possibleDirections.length; i++) {
      this.possibleDirections[i] = false;
    }
    if (stoneWalls[(this.y - 20) / 20][(this.x / 20)] == null && brickWalls[(this.y - 20) / 20][this.x / 20] == null) {
      this.possibleDirections[1] = true;
    }
    if (stoneWalls[(this.y + 20) / 20][this.x / 20] == null && brickWalls[(this.y + 20) / 20][this.x / 20] == null) {
      this.possibleDirections[3] = true;
    }
    if (stoneWalls[this.y / 20][(this.x - 20) / 20] == null && brickWalls[this.y / 20][(this.x - 20) / 20] == null) {
      this.possibleDirections[0] = true;
    }
    if (stoneWalls[this.y / 20][(this.x + 20) / 20] == null && brickWalls[this.y / 20][(this.x + 20) / 20] == null) {
      this.possibleDirections[2] = true;
    }
  }

  /**
   * every moving character must have some sort of tick function, as 
   * because it is a movingcharacter, it should be updating every frame. 
   * @param stoneWalls list of stonewalls from the game
   * @param brickWalls list of brickwalls from the game
   */
  public abstract void tick(GameObject[][] stoneWalls, BrickWall[][] brickWalls);

  /**
   * moves the characters location in desired direction by the desired speed 
   * if it is possible to go in that direction
   */
  public void move() {
    if (this.direction == 0 && this.possibleDirections[0]) {
      this.x -= SPEED;
    } else if (this.direction == 1 && this.possibleDirections[1]) {
      this.y -= SPEED;
    } else if (this.direction == 2 && this.possibleDirections[2]) {
      this.x += SPEED;
    } else if (this.direction == 3 && this.possibleDirections[3]) {
      this.y += SPEED;
    }
  }

  /**
   * increments timer attribute and sets canShoot to true if enough time has elapsed since last shot. 
   */
  public void updateTimer() {
    this.timer++;
    if (this.timer > COOLDOWN * App.FPS) {
      this.canShoot = true;
    }
  }

  /**
   * resets timer and sets canShoot to false. 
   */
  public void setCannotShoot() {
    this.canShoot = false;
    this.timer = 0;
  }
} 