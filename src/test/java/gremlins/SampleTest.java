package gremlins;

import processing.core.PApplet;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(OrderAnnotation.class)
public class SampleTest {
    public static App app;

    @BeforeAll
    public static void setupTest() {
        app = new App();
        app.configPath = "initialTests.json";
        app.noLoop();
        PApplet.runSketch(new String[]{"App"}, app);
        app.setup();
        app.delay(3000);
    }
    @Test
    @Order(1)
    public void appStarts() {
        // Test that app has started and is not null
        assertNotNull(app);
    }

    @Test
    @Order(2)
    public void testWizardSpawn() {
        /* Check the player starts at the correct coordinate

        XXXXXXXXXXXXXXXXXXXXX
        X W     G         X  
        X E               X  
        X     X  X   X       
        X

        */
        assertEquals(app.wizard.x, 40);
        assertEquals(app.wizard.y, 20);
        assertNotNull(app.wizard);
    }

    @Test
    @Order(3)
    public void testGremlinSpawn() {
        /* Check the gremlin starts at the correct coordinate
        
        XXXXXXXXXXXXXXXXXXXXX
        X W     G         X  
        X E               X  
        X     X  X   X       
        X

        */
        assertEquals(app.gremlins.size(), 1);
    }

    @Test
    @Order(4)
    public void powerupExists() {
        // Makes sure the powerup was found when reading the file,
        // and it has been instantiated.
        assertNotNull(app.powerup);
    }

    @Test
    @Order(5)
    public void testPowerupSpawn() {
        // Check the powerup coordinates are correct
        assertEquals(app.powerup.x, 320);
        assertEquals(app.powerup.y, 400);
    }

    @Test
    @Order(6)
    public void spacebarWorks() {
        app.keyCode = 32;
        app.keyPressed();
        app.eachFrameUpdate();
        assertEquals(app.fireballs.size(), 1);
        assertFalse(app.wizard.canShoot);
        app.keyReleased();
    }

    @Test
    @Order(7)
    public void testMovement() {
        app.keyCode = PApplet.DOWN;
        app.keyPressed();
        for (int i = 0; i < 10; i++) {
            app.eachFrameUpdate();
        }
        assertEquals(app.wizard.x, 40);
        assertEquals(app.wizard.y, 40);
        assertEquals(2, 2);
        app.keyReleased();

    }

    @Test
    @Order(8)
    public void fireballShootsForward() {
        while(!app.wizard.canShoot) {
            app.eachFrameUpdate();
        }
        app.keyCode = 32;
        app.keyPressed();
        app.eachFrameUpdate();
        assertEquals(app.fireballs.get(app.fireballs.size() - 1).direction, 3);
        app.keyReleased();

    }
    @Test
    @Order(9)
    public void exitWorks() {
        assertEquals(app.wizard.x, 40);
        assertEquals(app.wizard.y, 40);
        app.keyCode = PApplet.DOWN;
        app.keyPressed();
        assertEquals(app.levelCount, 0);
        for (int i = 0; i < 21; i++) {
            app.eachFrameUpdate();
        }
        assertEquals(app.levelCount, 1);
        assertEquals(app.wizard.x, 40);
        assertEquals(app.wizard.y, 20);
        app.keyReleased();
    }
}
