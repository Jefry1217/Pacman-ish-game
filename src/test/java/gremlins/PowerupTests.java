package gremlins;

import processing.core.PApplet;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(OrderAnnotation.class)
public class PowerupTests{
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
    public void powerupWorks() {
        app.fireballDirection = 0;
        app.keyCode = 32;
        app.keyPressed();
        app.eachFrameUpdate();
        app.keyReleased();

        for(int i = 0; i < 600; i++) {
            app.eachFrameUpdate();
        }
        app.keyCode = PApplet.DOWN;
        app.keyPressed();
        app.eachFrameUpdate();
        app.keyReleased();
        for (Gremlin g : app.gremlins) {
            assertEquals(g.SPEED, 0);
        }
    }
}
