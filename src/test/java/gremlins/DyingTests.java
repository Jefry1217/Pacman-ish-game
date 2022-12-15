package gremlins;

import processing.core.PApplet;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(OrderAnnotation.class)
public class DyingTests{
    public static App app;

    @BeforeAll
    public static void setupTest() {
        app = new App();
        app.configPath = "DyingTests.json";
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
    public void fireballDestroysSlime() {
        app.fireballDirection = 0;
        app.keyCode = 32;
        app.keyPressed();
        app.eachFrameUpdate();
        assertEquals(app.slimes.size(), 1);
        assertEquals(app.fireballs.size(), 1);
        for (int i = 0; i < 15; i++) {
            app.eachFrameUpdate();
        }
        assertEquals(app.slimes.size(), 1);
        assertEquals(app.fireballs.size(), 0);
        assertEquals(2, 2);
        app.keyReleased();
    }
    
    @Test
    @Order(3)
    public void gremlinDiesFromFireball() {
        assertNotEquals(app.gremlins.get(0).x, 140);
        app.keyCode = 32;
        app.keyPressed();
        for (int i = 0; i < 26; i++) {
            app.eachFrameUpdate();
        }
        assertEquals(app.fireballs.size(), 0);
        app.keyReleased();  
    }
}