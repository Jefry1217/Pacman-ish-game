package gremlins;

import processing.core.PImage;
import processing.core.PApplet;

import java.util.List;

public class Powerup extends GameObject{

  /**
   * the cooldown time for the powerup to spawn, a randomly 
   * generated number between 5 and 10.
   */
  private float cooldown;

  /**
   * Length of time that the powerup is active for.
   */
  private float activateTime = 3f;

  /**
   * amount to reduce the cooldown and activateTime by each frame.
   */
  private double decrement = 1.0 / 60;

  /**
   * Constructor for Powerup class
   * @param sprite the image of the powerup
   * @param x the horizontal location of the powerup
   * @param y the vertical location of the powerup
   */
  public Powerup(PImage sprite, int x, int y) {
    super(sprite, x, y);
  }

  /**
   * Sets the cooldown attribute to a random float between 5 and 10.
   */
  public void setPowerupCooldown() {
    this.cooldown = (float) App.randomGenerator.nextInt(11);
    if (this.cooldown < 5) {
      this.cooldown += 5;
    }
  }

  /**
   * Checks if the player hits the powerup and activates the powerup they do.
   * Deactivates powerup ability if it has been active for its designated length of time.
   * @param gremlins The list of Gremlin objects in the current level
   * @param wizard The Wizard object that the player controls
   */
  public void tick(List<Gremlin> gremlins, Wizard wizard) {
    this.cooldown -= this.decrement;
    if (this.cooldown < 0 && wizard.x - (wizard.x % 20) == this.x && wizard.y - (wizard.y % 20) == this.y) {
      for (Gremlin g : gremlins) {
        g.SPEED = 0;
        setPowerupCooldown();
        this.cooldown += 3;
        this.activateTime -= this.decrement;
      }
    }
    if (this.activateTime < 0) {
      this.activateTime = 3;
      for (Gremlin g : gremlins) {
        g.SPEED = 1;
      }
    }
  }

  /**
   * draws nothing, or the powerup on the map, or the countdown timer, depending on the situation. 
   * @param app The PApplet app to draw onto.
   */
  public void drawRelated(PApplet app) {
    if (this.activateTime < 3) {
      app.textSize(60);
      app.text((int) Math.ceil(this.activateTime), 500, 710);
      app.textSize(16);
      this.activateTime -= this.decrement;
    }
    if (this.cooldown < 0) {
      this.draw(app);
    }
  }
} 