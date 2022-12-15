package gremlins;

import processing.core.PImage;
import java.util.List;

public class Gremlin extends MovingCharacter {

  /**
   * Constructor for Gremlin class 
   * @param sprite the image of the Gremlin to draw
   * @param x the horizontal location
   * @param y the vertical location
   * @param SPEED the speed of the Gremlin, number of pixels it moves per frame
   * @param COOLDOWN the cooldown of the Gremlin shooting
   */
  public Gremlin(PImage sprite, int x, int y, int SPEED, float COOLDOWN) {
    super(sprite, x, y, SPEED, COOLDOWN);
  }

  /**
   * if the gremlin is on the centre of a tile, set the possible directions to move in,
   * and randomly generate the next direction for the gremlin to move in if it can't
   * keep going in the same direction. 
   */
  public void tick(GameObject[][] stoneWalls, BrickWall[][] brickWalls) {
    if (this.x % 20 == 0 && this.y % 20 == 0) {
      this.setPossibleDirections(stoneWalls, brickWalls);
      if (!this.possibleDirections[this.direction]) {
        int oppDirection = this.direction - 2;
        if (oppDirection < 0) {
          oppDirection += 4;
        }
        this.possibleDirections[oppDirection] = false;
        int choices = 0;
        int choice = 0;
        for (int i = 0; i < 4; i++) {
          if (this.possibleDirections[i]) {
            choices++;
            choice = i;
          }
        }
        if (choices == 0) {
          this.possibleDirections[oppDirection] = true;
          this.direction = oppDirection;
        }
        else if (choices == 1) {
          this.direction = choice;
        } else {
          if (App.randomGenerator.nextInt(2) == 0) {
              this.direction = choice;
          } else {
            for (int i = 0; i < 4; i++) {
              if (this.possibleDirections[i]) {
                this.direction = i;
                break;
              }
            }
          }
        }
      }
    }
    this.move();
    this.updateTimer();
  }

  /**
   * randomly generate new coordinates for the gremlin to be in once dying, 
   * but at least 10 tiles away from the wizards current location
   * @param openCoords list of possible coordinates to move to
   * @param wizardX the x coordinate of the wizard 
   * @param wizardY the y coordinate of the wizard
   */
  public void respawn(List<int[]> openCoords, int wizardX, int wizardY) {
    double distance = 0;
    int spawn = 0;
    while (distance < 200) {
      spawn = (int) (App.randomGenerator.nextFloat() * openCoords.size());
      double yDistance = (openCoords.get(spawn)[1] - wizardY);
      double xDistance = (openCoords.get(spawn)[0] - wizardX);
      distance = Math.sqrt((yDistance * yDistance) + (xDistance * xDistance));
    }
    this.x = openCoords.get(spawn)[0];
    this.y = openCoords.get(spawn)[1];
  }
} 