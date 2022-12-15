package gremlins;

import processing.core.PImage;
import java.util.List;
import java.util.ArrayList;

public class Teleporter extends GameObject{

  /**
   * The number of the teleporter, used for finding the other teleporter object
   * that it is connected to
   */
  protected final int teleporterNumber;

  /**
   * The other teleporter object that it is connected to
   */
  protected Teleporter connectedWith;

  /**
   * A list of objects which have previously just teleported
   * from another teleporter to this one and haven't moved off yet.
   * This is used to stop the effect of infinitely teleporting 
   * between the two connected teleporters once the player hits one.
   */
  protected List<MovingObject> stillOnTeleporter = new ArrayList<MovingObject>();
  
  /**
   * The constructor for the teleporter
   * @param sprite the image of the teleporter
   * @param x the horizontal location
   * @param y the vertical location
   * @param teleporterNumber the number of the teleporter, used for finding the other teleporter
   * object that it is connected to
   */
  public Teleporter(PImage sprite, int x, int y, int teleporterNumber) {
    super(sprite, x, y);
    this.teleporterNumber = teleporterNumber;
  }

  /**
   * Loops through all moving objects and sees if any of them are on the teleporter.
   * Changes their coordinates to the connected teleporter if they are. 
   * @param fireballs list of active fireballs
   * @param slimes list of active slimes
   * @param gremlins list of gremlins
   * @param wizard the player
   */
  public void tryTeleportEverything(List<MovingObject> allMoving) {
    // joining everything into one arraylist to easily iterate over
    for (MovingObject m : allMoving) {
      if (m.x - (m.x % 20) == this.x && m.y - (m.y % 20) == this.y && !this.stillOnTeleporter.contains(m)) {
        m.x = this.connectedWith.x;
        m.y = this.connectedWith.y;
        this.connectedWith.stillOnTeleporter.add(m);
        return;
      }
      if (this.stillOnTeleporter.contains(m) && (m.x - (m.x % 20) != this.x || m.y - (m.y % 20) != this.y)) {
        this.stillOnTeleporter.remove(m);
      }
    }
  }
} 