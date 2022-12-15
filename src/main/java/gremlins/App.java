package gremlins;

import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PImage;
import processing.data.JSONObject;
import processing.data.JSONArray;

import java.util.Random;
import java.lang.Math;
import java.io.*;


public class App extends PApplet {
  /**
   * Setting height and width of window, and size of all sprites. 
   */
  private static final int WIDTH = 720;
  private static final int HEIGHT = 720;
  private static final int SPRITESIZE = 20;
  
  /**
   * Setting framrate the game will run at
   */
  public static final int FPS = 60;

  /**
   * The random generator to use for random spawns and timer lengths etc.
   */
  public static final Random randomGenerator = new Random();

  /**
   * The path to the file which has information
   * such as number of levels, cooldown times,
   * player lives, etc.
   */
  public String configPath;

  /**
   * The number of levels in the game
   */
  private int NUMLEVELS;
  
  /**
   * All the sprites that will be used. 
   */
  private PImage brickwallSprite;
  private PImage stonewallSprite;
  private PImage gremlinSprite;
  private PImage slimeSprite;
  private PImage fireballSprite;
  private PImage doorSprite;
  private PImage powerupSprite;
  private PImage teleporterSprite;
  private PImage[] wizardSprites = new PImage[4];
  private PImage[] brokenSprites = new PImage[4];

  /**
   * Two dimensional array representing each tile of the map,
   * allows to check whether a brickwall or stonewall exists
   * in a specific location
   */
  private BrickWall[][] brickWalls = new BrickWall[33][36];
  private GameObject[][] stoneWalls = new GameObject[33][36];

  /**
   * ArrayList to containing brickwalls that are currently being destroyed
   */
  private List<BrickWall> beingDestroyed = new ArrayList<BrickWall>();

  /**
   * ArrayLists containing Gremlins, currently active fireballs,
   * and currently active slimes.
   */
  public List<Gremlin> gremlins = new ArrayList<Gremlin>();
  public List<Projectile> fireballs = new ArrayList<Projectile>();
  public List<Projectile> slimes = new ArrayList<Projectile>();
  public List<Teleporter> teleporters = new ArrayList<Teleporter>();

  /**
   * Lists containing objects that need to be removed
   */
  private List<Projectile> slimesToRemove = new ArrayList<Projectile>();
  private List<Projectile> fireballsToRemove = new ArrayList<Projectile>(); 
  private List<BrickWall> toKill = new ArrayList<BrickWall>();

  /**
   * ArrayList containing all coords without brickwalls or stonewalls.
   */
  private List<int[]> openCoords = new ArrayList<int[]>();

  /**
   * the Wizard object which the player controls. 
   */
  public Wizard wizard;

  /**
   * The exit the wizard must walk over to finish the level
   */
  private GameObject door;

  /**
   * The powerup that randomly spawns in the same spot on the map
   * every 5-10 seconds, causes gremlins to freeze for 3 seconds.
   * Powerup is not in every level however
   */
  public Powerup powerup;
  
  /**
   * Contains all individual level data such as
   * wizard and gremlin shooting cooldowns, wizard lives, and
   * level layout file. 
   */
  private JSONArray levelsArray;

  /**
   * Counter of the current level, starting at 0.
   */
  public int levelCount = 0;

  /**
   * Counter of current lives the player has.
   */
  public int lives;

  /**
   * Used to set the direciton of fireball when 
   * Projectile fireball objects are created
   */
  public int fireballDirection = 2; //0=left, 1=up, 2=right, 3=down

  /**
   * changes to true when player runs out of lives or finishes all levels
   */
  private boolean gameOver = false;

  /**
   * Constructor for app class, sets config file
   */
  public App() {
    this.configPath = "config.json";
  }

  /**
   * Initialise the setting of the window size.
  */
  public void settings() {
    size(WIDTH, HEIGHT);
  }

  /**
   * Load all resources such as images, config file, array with levels information, and then starts first level. 
  */
  public void setup() {
    frameRate(FPS);
    textSize(16);

    // Load all sprites needed for game
    this.stonewallSprite = loadImage(this.getClass().getResource("stonewall.png").getPath().replace("%20", " "));
    this.brickwallSprite = loadImage(this.getClass().getResource("brickwall.png").getPath().replace("%20", " "));
    this.gremlinSprite = loadImage(this.getClass().getResource("gremlin.png").getPath().replace("%20", " "));
    this.slimeSprite = loadImage(this.getClass().getResource("slime.png").getPath().replace("%20", " "));
    this.fireballSprite = loadImage(this.getClass().getResource("fireball.png").getPath().replace("%20", " "));
    this.wizardSprites[0] = loadImage(this.getClass().getResource("wizard0.png").getPath().replace("%20", " "));
    this.powerupSprite = loadImage(this.getClass().getResource("powerup.png").getPath().replace("%20", " "));
    this.doorSprite = loadImage(this.getClass().getResource("door.png").getPath().replace("%20", " "));
    this.teleporterSprite = loadImage(this.getClass().getResource("teleporter.png").getPath().replace("%20", " "));
    for (PImage sprite : new PImage[] {this.doorSprite, this.teleporterSprite, this.powerupSprite}) {
      sprite.resize(SPRITESIZE, SPRITESIZE);
    }
    this.levelCount = 0;
    for(int i = 0; i < 4; i++) {
      this.wizardSprites[i] = loadImage(this.getClass().getResource("wizard" + Integer.toString(i) + ".png").getPath().replace("%20", " "));
    }
    for(int i = 0; i < 4; i++) {
      this.brokenSprites[i] = loadImage(this.getClass().getResource("brickwall_destroyed" + Integer.toString(i) + ".png").getPath().replace("%20", " "));
    }

    JSONObject conf = loadJSONObject(new File(this.configPath));

    // 
    this.levelsArray = conf.getJSONArray("levels");
    this.lives = conf.getInt("lives");
    this.NUMLEVELS = levelsArray.size();

    try {
      startLevel();
    }
    catch (FileNotFoundException e) {
      System.out.println("oh no");
      System.exit(0);
    }
  }

  /**
   * Removes all instantiated objects if there were any from previous levels, then instantiates
   * all objects as needed with new correct positions and attributes as according to the
   * config file
   * @throws FileNotFoundException
   */
  public void startLevel() throws FileNotFoundException{

    // getting new attributes as according to new level
    JSONObject levelDict = levelsArray.getJSONObject(levelCount);
    String levelName = levelDict.getString("layout");

    // removing all instantiated objects from previous level if there was one
    gremlins.removeAll(gremlins);
    fireballs.removeAll(fireballs);
    slimes.removeAll(slimes);
    beingDestroyed.removeAll(beingDestroyed);
    openCoords.removeAll(openCoords);
    teleporters.removeAll(teleporters);
    slimesToRemove.removeAll(slimesToRemove);
    fireballsToRemove.removeAll(fireballsToRemove);
    toKill.removeAll(toKill);
    for (BrickWall[] line : this.brickWalls) {
      for (int i = 0; i < line.length; i++) {
        line[i] = null;
      }
    }
    for (GameObject[] line : this.stoneWalls) {
      for (int i = 0; i < line.length; i++) {
        line[i] = null;
      }
    }
    this.powerup = null;
    
    File f = new File(levelName);
    Scanner s = new Scanner(f);

    // iterating through the new level file and instantiating objects as needed
    for (int i = 0; i < 33; i++) {
      int xcord = 0;
      int ycord = i * 20;
      int xArrayIndex = 0;
      String line = s.nextLine();
      for (char ch : line.toCharArray()) {
        if (xcord == 0 || ycord == 0) {
          if (Character.compare('X', ch) != 0) {
            System.out.println("OUTSIDE IS NOT STONE NOOOOOO");
            System.exit(0);
          }
        } 
        if (
        Character.isWhitespace(ch) || 
        Character.compare('E', ch) == 0 || 
        Character.compare('G', ch) == 0 ||
        Character.compare('W', ch) == 0
                ) {
          int[] temp = {xcord, ycord};
          this.openCoords.add(temp);
        }
        if (Character.compare('X', ch) == 0){
          this.stoneWalls[i][xArrayIndex] = new GameObject(this.stonewallSprite, xcord, ycord);
        } else if (Character.compare('B', ch) == 0) {
          this.brickWalls[i][xArrayIndex] = new BrickWall(this.brickwallSprite, xcord, ycord);
        } else if (Character.compare('G', ch) == 0) {
          this.gremlins.add(new Gremlin(this.gremlinSprite, xcord, ycord, 1, levelDict.getFloat("enemy_cooldown")));
        } else if (Character.compare('W', ch) == 0) {
          this.wizard = new Wizard(this.wizardSprites[0], xcord, ycord, 2, levelDict.getFloat("wizard_cooldown"));
        } else if (Character.compare('E', ch) == 0) {
          this.door = new GameObject(this.doorSprite, xcord, ycord);
        } else if (Character.compare('P', ch) == 0) {
          this.powerup = new Powerup(this.powerupSprite, xcord, ycord);
        } else if (Character.isDigit(ch)) {
          Teleporter tempConnectedWith = null;
          for (Teleporter t : this.teleporters) {
            if (t.teleporterNumber == ch - '0') {
              System.out.println(ch - '0');
              tempConnectedWith = t;
            }
          }
          Teleporter tempTeleporter = new Teleporter(this.teleporterSprite, xcord, ycord, ch - '0');
          if (tempConnectedWith != null) {
            tempConnectedWith.connectedWith = tempTeleporter;
            tempTeleporter.connectedWith = tempConnectedWith;
          }
          this.teleporters.add(tempTeleporter);
        }
        xArrayIndex++;
        xcord += 20;
      }
    }
    // for (Teleporter t : this.teleporters) {
    //   if (t.connectedWith == null) {
    //     for (Teleporter tele : this.teleporters) {
    //       if (!t.equals(tele) && tele.connectedWith == null && t.) {

    //       }
    //     }
    //   }
    // }
    // starts powerup spawn timer if powerup exists in this level
    if (this.powerup != null) {
      this.powerup.setPowerupCooldown();
    }
    s.close();
  }

  /**
   * Receive key pressed signal from the keyboard and changes wizard direction or
   * shoots fireball if allowed. 
  */
  public void keyPressed() {
    // Left: 37
    // Up: 38
    // Right: 39
    // Down: 40
    // Space: 49
    if (gameOver) {
      setup();
      this.gameOver = false;
    }
    if (this.keyCode >= 37 && this.keyCode <=40) {
      this.fireballDirection = -1;
      this.wizard.nextDirection = this.keyCode - 37;
    }
    if (this.keyCode == 32 && this.wizard.canShoot) {
      if (this.fireballDirection == -1) {
        this.fireballs.add(new Projectile(fireballSprite, this.wizard.x - (this.wizard.x % 20), this.wizard.y - (this.wizard.y % 20), this.wizard.direction));
      } else {
        this.fireballs.add(new Projectile(fireballSprite, this.wizard.x - (this.wizard.x % 20), this.wizard.y - (this.wizard.y % 20), this.fireballDirection));
      }
      this.wizard.setCannotShoot();
    }
  }
  
  /**
   * Receive key released signal from the keyboard
   * Sets next direction to standing still if key released was arrow key.
   * Saves fireball direction to direction currently moving so fireballs go 
   * in correct direction if player presses space whilst standing still.
  */
  public void keyReleased(){
    if (this.keyCode >= 37 && this.keyCode <= 40) {
      this.wizard.nextDirection = -1;
    }
    if (this.wizard.direction != -1) {
      this.fireballDirection = this.wizard.direction;
    }
  }

  /**
   * Draws the lives remaining with wizard sprites at the bottom of the window
   */
  public void drawLives() {
    fill(255, 255, 255);
    text("Lives: ", 50, 690);
    for (int i = 0; i < this.lives; i++) {
      this.image(this.wizardSprites[0], 105 + (30 * i), 673);
    }
  }

  /**
   * Draws text displaying the current level out of the total number of levels at the bottom of the window
   */
  public void drawLevels() {
    fill(255, 255, 255);
    text("Levels: " + (this.levelCount + 1) + "/" + this.NUMLEVELS, 300, 690); //this should be not overlapping if have heaps of lives? 
  }

  /**
   * Displays a blank screen with either "GAME OVER" or "YOU WIN" depending on
   * if the player lost all their lives or finished all the levels
   */
  public void gameOver() {
    background(190, 152, 114);
    textSize(100);
    if (this.lives == 0) {
      text("GAME OVER", 100, 300);
    } else {
      text("YOU WIN", 100, 300);
    }
  }


  public void eachFrameUpdate() {
    /**
     * Everything to do with updating slimes
     */
    // causes any gremlin that can shoot to shoot a slime
    for (Gremlin g : gremlins) {
      if (g.canShoot) {
        this.slimes.add(new Projectile(slimeSprite, g.x - (g.x % 20), g.y - (g.y % 20), g.direction));
        g.setCannotShoot();
      }
    }
    // updates position for all slimes, and checks if they have hit a wall. If they are no longer
    // on an open coord, then adds to the slimesToRemove List. 
    for (Projectile slime : slimes) {
      slime.tick();
      boolean openCoord = false;
      if (slime.x % 20 == 0 && slime.y % 20 == 0) {
        for (int[] coords : this.openCoords) {
          if(slime.x == coords[0] && slime.y == coords[1]) {
            openCoord = true;
            break;
          }
        }
        if (!openCoord) {
          slimesToRemove.add(slime);
          continue;
        }
      }
    }
    // checks if the wizard has hit any slime, and decreases lives if so. 
    Projectile s = this.wizard.onASlime(this.slimes);
    if (s != null) {
      if (--this.lives == 0) {
        gameOver = true;
        gameOver();
      }
      try {
        this.startLevel();
      }
      catch (FileNotFoundException e) {
        System.out.println("oh no");
        System.exit(0);
      }
    }

    /**
     * Calls functions to update the powerup attributes as needed
     */
    if (this.powerup != null) {
      this.powerup.tick(this.gremlins, this.wizard);
    }

    /**
     * Everything to do with updating the wizard.
     */
    // If wizard is moving, change sprite to match the direction moving. 
    if (this.wizard.direction != -1) {
      this.wizard.sprite = wizardSprites[this.wizard.direction];
    }
    this.wizard.tick(this.stoneWalls, this.brickWalls); //updating wizard location

    // check if the wizard has come into contact with any gremlins
    Gremlin g = this.wizard.onAGremlin(gremlins);
    if (g != null) {
      if (--this.lives == 0) {
        gameOver = true;
        gameOver();
      }
      else {
        try {
          this.startLevel();
        }
        catch (FileNotFoundException e) {
          System.out.println("oh no");
          System.exit(0);
        }
      }
    }
    // check if the wizard has reached the exit
    if (this.door.x == this.wizard.x - (this.wizard.x % 20) && this.door.y == this.wizard.y - (this.wizard.y % 20)) {
      if (++this.levelCount > this.NUMLEVELS - 1) {
        gameOver = true;
        gameOver();
      }
      else {
        try {
          this.startLevel();
        }
        catch (FileNotFoundException e) {
          System.out.println("oh no");
          System.exit(0);
        }
      }
    }

    /**
     * Everything to do with updating brickWalls
     */
    // updates the sprite of all BrickWall objects that have been hit by a fireball
    for (BrickWall b : this.beingDestroyed) {
      if (b.phase == 16) {
        toKill.add(b);
      } else {
        b.sprite = this.brokenSprites[(int) Math.floor(b.phase++ / 4 )];
      }
    }
    // removes BrickWall object from brickWalls array once it has been destroyed, and adds to open coords. 
    for (BrickWall b : toKill) {
      int[] coords = {b.x, b.y};
      this.openCoords.add(coords);
      this.beingDestroyed.remove(b);
      this.brickWalls[b.y / 20][b.x / 20] = null;
    }

    /**
     * Everything to do with updating fireballs
     */
    // checks if any fireballs have hit any walls, gremlins, or slimes.
    for (Projectile f : fireballs) {
      f.tick();
      if (f.x % 20 == 0 && f.y % 20 == 0) {
        if (brickWalls[ f.y / 20][ f.x / 20] != null ||
        stoneWalls[ f.y / 20][ f.x / 20] != null) {
          fireballsToRemove.add(f);
          if (brickWalls[f.y / 20][f.x / 20] != null) {
            beingDestroyed.add(brickWalls[f.y / 20][f.x / 20]);
          }
        }
      }
      Gremlin grem = f.onAGremlin(gremlins);
      if (grem != null) {
        fireballsToRemove.add(f);
        grem.respawn(this.openCoords, this.wizard.x, this.wizard.y);
      }
      Projectile slime = f.onASlime(this.slimes);
      if (slime != null) {
        fireballsToRemove.add(f);
        slimesToRemove.add(slime);
      }

    /**
     * Removes all fireballs, slimes, and gremlins that have hit something.
     */
    }
    for (Projectile slime : slimesToRemove) {
      this.slimes.remove(slime);
    }
    for (Projectile fireball : fireballsToRemove) {
      this.fireballs.remove(fireball);
    }
    for (Gremlin grem : gremlins) {
      grem.tick(this.stoneWalls, this.brickWalls);
    }
    
    /**
     * Everything to do with teleporting movingobjects
     */
    List<MovingObject> tempAllMoving = new ArrayList<MovingObject>();
    tempAllMoving.addAll(fireballs);
    tempAllMoving.addAll(slimes);
    tempAllMoving.addAll(gremlins);
    tempAllMoving.add(wizard);
    for (Teleporter t : this.teleporters) {
      t.tryTeleportEverything(tempAllMoving);
    }
  }

  /**
   * Called each frame, updates everything that needs to be updated and 
   * draws everything onto the window. 
   */
  public void draw() {
    if (gameOver) {
      gameOver();
    }
    else {
      eachFrameUpdate();
      /**
       * Drawing everything
       */
      background(190, 152, 114);
      for (Projectile slime : slimes) {
        slime.draw(this);
      }
      if (this.powerup != null) {
        this.powerup.drawRelated(this);
      }
      for (GameObject[] stoneRow : this.stoneWalls) {
        for (GameObject stone : stoneRow) {
          if (stone != null) {
            stone.draw(this);
          }
        }
      }
      for (BrickWall[] brickRow : this.brickWalls) {
        for (GameObject b : brickRow) {
          if (b != null) {
            b.draw(this);
          }
        }
      }
      for (Gremlin gremlin : this.gremlins) {
        gremlin.draw(this);
      }
      for (Projectile fireball : this.fireballs) {
        fireball.draw(this);
      }
      this.wizard.draw(this);
      if(!this.wizard.canShoot) {
        this.wizard.updateRechargeBar(this);
      }
      this.door.draw(this);
      for (Teleporter t : this.teleporters) {
        t.draw(this);
      }
      drawLives();
      drawLevels();
    }
  }

  /**
   * runs the game thorugh PApplet library
   * @param args arguments given when running the code. 
   */
  public static void main(String[] args) {
    PApplet.main("gremlins.App");
  }
}