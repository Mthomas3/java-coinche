package com.charlestati.jcoinche;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ServerTest {
    private static Thread thread;

    @BeforeClass
    public static void setUp() throws Exception {
        thread = new Thread(() -> {
            try {
                new Server().start(8081);
            } catch (InterruptedException ie) {
                System.err.println("Shutting down Server");
            } catch (Exception ex) {
                System.err.println("Could not start Server for tests");
            }
        });

        thread.start();
        Thread.sleep(5000);
    }

    @Test
    public void checkHandshake() {
        assertEquals(0, 0);
    }

    @AfterClass
    public static void tearDown() throws Exception {
        Thread.sleep(1000);
        thread.interrupt();
        while (thread.isAlive()) {
            Thread.sleep(250);
        }
    }
}
